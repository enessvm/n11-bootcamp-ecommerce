package com.n11.bootcamp.ecommerce.stock.service.impl;

import com.n11.bootcamp.ecommerce.events.stock.*;
import com.n11.bootcamp.ecommerce.stock.dto.StockAdjustResponse;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchResponse;
import com.n11.bootcamp.ecommerce.stock.entity.ReservationStatus;
import com.n11.bootcamp.ecommerce.stock.entity.StockLevel;
import com.n11.bootcamp.ecommerce.stock.entity.StockReservation;
import com.n11.bootcamp.ecommerce.stock.event.StockEventPublisher;
import com.n11.bootcamp.ecommerce.stock.exception.StockNotInitializedException;
import com.n11.bootcamp.ecommerce.stock.mapper.StockMapper;
import com.n11.bootcamp.ecommerce.stock.repository.StockLevelRepository;
import com.n11.bootcamp.ecommerce.stock.repository.StockReservationRepository;
import com.n11.bootcamp.ecommerce.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private static final Duration RESERVATION_TTL = Duration.ofMinutes(15);

    private final StockLevelRepository stockLevelRepository;
    private final StockReservationRepository reservationRepository;
    private final StockMapper stockMapper;
    private final StockEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public StockBatchResponse batch(StockBatchRequest request) {
        List<StockLevel> found = stockLevelRepository.findAllByProductIdIn(request.productIds());
        List<StockBatchResponse.Item> items = found.stream()
                .map(stockMapper::toBatchItem)
                .toList();
        return new StockBatchResponse(items);
    }

    @Override
    @Transactional
    public void initStock(Long productId) {
        stockLevelRepository.insertIfAbsent(productId, Instant.now());
    }

    @Override
    @Transactional
    public StockAdjustResponse adjustStock(long productId, int availableQuantity) {
        StockLevel stockLevel = stockLevelRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotInitializedException(productId));

        stockLevel.setAvailableQuantity(availableQuantity);

        log.info("Stock adjusted productId={} availableQuantity={}",
                productId, availableQuantity);

        return new StockAdjustResponse(
                stockLevel.getProductId(),
                stockLevel.getAvailableQuantity(),
                stockLevel.getUpdatedAt()
        );
    }

    // ---- Saga Consumers ----

    @Override
    @Transactional
    public void consumeReserveCommand(ReserveStockCommand command) {
        // existing reservations for this saga redelivery and replay.
        if (reservationRepository.existsBySagaId(command.sagaId())) {
            replayPriorReservation(command.sagaId());
            return;
        }

        // Sort by productId — deterministic lock order avoids deadlocks
        // between concurrent sagas reserving the same products.
        var sortedItems = command.items().stream()
                .sorted(comparing(ReserveStockCommand.Item::productId))
                .toList();

        // Lock and validate every item before mutating any. Bail at first failure.
        var lockedLevels = new LinkedHashMap<Long, StockLevel>();
        for (var item : sortedItems) {
            var stockLevel = stockLevelRepository.findByProductIdForUpdate(item.productId()).orElse(null);
            if (stockLevel == null) {
                eventPublisher.publishReservationFailed(
                        command.sagaId(), StockReservationFailed.Reason.STOCK_NOT_FOUND, item.productId());
                return;
            }
            if (stockLevel.getAvailableQuantity() < item.quantity()) {
                eventPublisher.publishReservationFailed(
                        command.sagaId(), StockReservationFailed.Reason.INSUFFICIENT_STOCK, item.productId());
                return;
            }
            lockedLevels.put(item.productId(), stockLevel);
        }

        // All locks held, all checks passed. Decrement and create reservations.
        var expiresAt = Instant.now().plus(RESERVATION_TTL);
        var reservations = new ArrayList<StockReservation>(sortedItems.size());
        for (var item : sortedItems) {
            var stockLevel = lockedLevels.get(item.productId());
            stockLevel.decrement(item.quantity());
            reservations.add(reservationRepository.save(
                    StockReservation.create(command.sagaId(), item.productId(), item.quantity(), expiresAt)
            ));
        }

        eventPublisher.publishStockReserved(command.sagaId(), reservations, expiresAt);
        log.info("Reserved sagaId={} items={}", command.sagaId(), reservations.size());
    }

    @Override
    @Transactional
    public void consumeCommitCommand(CommitStockCommand command) {
        var reservations = reservationRepository.findBySagaId(command.sagaId());

        if (reservations.isEmpty()) {
            log.warn("Commit received for unknown sagaId={} — no reservations found",
                    command.sagaId());
            eventPublisher.publishCommitFailed(command.sagaId());
            return;
        }

        // All reservations are valid
        for (var reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.RESERVED) {
                reservation.setStatus(ReservationStatus.COMMITTED);
            }
        }

        eventPublisher.publishStockCommitted(command.sagaId());
        log.info("Committed sagaId={} reservations={}", command.sagaId(), reservations.size());
    }

    @Override
    @Transactional
    public void consumeReleaseCommand(ReleaseStockCommand command) {
        var reservations = reservationRepository.findBySagaId(command.sagaId());

        if (reservations.isEmpty()) {
            log.warn("Release rejected sagaId={} — no reservations found", command.sagaId());
            throw new IllegalStateException(
                    "Release for sagaId=" + command.sagaId() + " has no reservations");
        }

        for (var reservation : reservations) {
            switch (reservation.getStatus()) {
                // COMMITTED rows: cannot release a committed reservation.
                case COMMITTED -> {
                    log.warn("Release rejected sagaId={} — reservation id={} is COMMITTED",
                            command.sagaId(), reservation.getId());
                    throw new IllegalStateException(
                            "Cannot release COMMITTED reservation id=" + reservation.getId());
                }
                // Restore stock for each RESERVED row, flip status to RELEASED.
                case RESERVED -> {
                    var stockLevel = stockLevelRepository
                            .findByProductIdForUpdate(reservation.getProductId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Reservation references missing stockLevel productId="
                                            + reservation.getProductId()));
                    stockLevel.increment(reservation.getQuantity());
                    reservation.setStatus(ReservationStatus.RELEASED);
                }
                // Already RELEASED/EXPIRED rows: no-op.
                case RELEASED -> {
                    // No-op — already released or expired.
                }
            }
        }

        eventPublisher.publishStockReleased(command.sagaId());
        log.info("Released sagaId={} reservations={}", command.sagaId(), reservations.size());
    }

    // ---- Helpers ----
    private void replayPriorReservation(java.util.UUID sagaId) {
        var existing = reservationRepository.findBySagaId(sagaId);
        // Take the expires_at from any existing row — they share the same
        // timestamp from the original reserve transaction.
        var expiresAt = existing.get(0).getExpiresAt();
        log.info("Replaying StockReserved sagaId={} reservations={}", sagaId, existing.size());
        eventPublisher.publishStockReserved(sagaId, existing, expiresAt);
    }

}