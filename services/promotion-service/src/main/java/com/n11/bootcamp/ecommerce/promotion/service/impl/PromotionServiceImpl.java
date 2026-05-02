package com.n11.bootcamp.ecommerce.promotion.service.impl;

import com.n11.bootcamp.ecommerce.events.promotion.ApplyPromotionCommand;
import com.n11.bootcamp.ecommerce.events.promotion.RevertPromotionCommand;
import com.n11.bootcamp.ecommerce.promotion.dto.CreatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionValidationResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.UpdatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.dto.ValidationFailure;
import com.n11.bootcamp.ecommerce.promotion.entity.Money;
import com.n11.bootcamp.ecommerce.promotion.entity.Promotion;
import com.n11.bootcamp.ecommerce.promotion.entity.PromotionRedemption;
import com.n11.bootcamp.ecommerce.promotion.event.PromotionEventPublisher;
import com.n11.bootcamp.ecommerce.promotion.exception.DuplicatePromotionCodeException;
import com.n11.bootcamp.ecommerce.promotion.exception.PromotionNotFoundException;
import com.n11.bootcamp.ecommerce.promotion.mapper.PromotionMapper;
import com.n11.bootcamp.ecommerce.promotion.repository.PromotionRedemptionRepository;
import com.n11.bootcamp.ecommerce.promotion.repository.PromotionRepository;
import com.n11.bootcamp.ecommerce.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionRedemptionRepository redemptionRepository;
    private final PromotionMapper promotionMapper;
    private final PromotionEventPublisher eventPublisher;

    @Override
    @Transactional
    public PromotionResponse create(CreatePromotionRequest request) {
        String code = request.code().toUpperCase();
        if (promotionRepository.existsByCode(code)) {
            throw new DuplicatePromotionCodeException(code);
        }
        Promotion promotion = promotionMapper.toEntity(request);
        promotion.setCode(code);
        return promotionMapper.toResponse(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public PromotionResponse update(Long id, UpdatePromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException(id));
        promotionMapper.updateEntity(promotion, request);
        return promotionMapper.toResponse(promotion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException(id));
        promotionRepository.delete(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException(id));
        return promotionMapper.toResponse(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> listAll() {
        return promotionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(promotionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionValidationResponse validate(String code,
                                                BigDecimal cartTotal,
                                                String currency) {
        String normalized = code.toUpperCase();
        Promotion promotion = promotionRepository.findByCode(normalized).orElse(null);

        if (promotion == null) {
            return promotionMapper.toInvalidResponse(normalized, ValidationFailure.NOT_FOUND);
        }

        Optional<ValidationFailure> failure = checkAvailability(promotion, cartTotal);
        if (failure.isPresent()) {
            return promotionMapper.toInvalidResponse(normalized, failure.get());
        }
        return promotionMapper.toValidResponse(promotion);
    }


    // ---- Saga  consumer ----
    @Override
    @Transactional
    public void consumeApplyCommand(ApplyPromotionCommand command) {
        // 1. Existing redemption redelivery, replay and return.
        Optional<PromotionRedemption> existing =
                redemptionRepository.findBySagaId(command.sagaId());

        if (existing.isPresent()) {
            PromotionRedemption row = existing.get();
            Promotion promotion = promotionRepository.findById(row.getPromotionId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Redemption references missing promotion id=" + row.getPromotionId()));

            log.info("Replaying redemption sagaId={} code={} discount={}",
                    command.sagaId(), promotion.getCode(),
                    row.getCartDiscount().getAmount());

            eventPublisher.publishApplied(
                    command.sagaId(),
                    promotion.getId(),
                    promotion.getCode(),
                    row.getCartDiscount().getAmount(),
                    row.getCartDiscount().getCurrency()
            );
            return;
        }

        // 2. Validate availability.
        Promotion promotion = promotionRepository.findByCode(command.code()).orElse(null);
        if (promotion == null) {
            log.info("Apply rejected sagaId={} code={} reason=NOT_FOUND",
                    command.sagaId(), command.code());
            eventPublisher.publishApplicationFailed(command.sagaId(), ValidationFailure.NOT_FOUND);
            return;
        }

        Optional<ValidationFailure> failure = checkAvailability(promotion, command.cartTotal());
        if (failure.isPresent()) {
            log.info("Apply rejected sagaId={} code={} reason={}",
                    command.sagaId(), command.code(), failure.get());
            eventPublisher.publishApplicationFailed(command.sagaId(), failure.get());
            return;
        }

        // 3. Atomic claim. Returns 0 if max_uses reached or promotion was deactivated
        int updated = promotionRepository.tryIncrementTimesRedeemed(promotion.getId());
        if (updated == 0) {
            log.info("Apply rejected sagaId={} code={} — max_uses reached",
                    command.sagaId(), command.code());
            eventPublisher.publishApplicationFailed(
                    command.sagaId(), ValidationFailure.MAX_USES_REACHED);
            return;
        }

        // 4. Compute discount, persist redemption snapshot.
        BigDecimal discount = computeDiscount(promotion, command.cartTotal());

        PromotionRedemption redemption = new PromotionRedemption();
        redemption.setSagaId(command.sagaId());
        redemption.setPromotionId(promotion.getId());
        redemption.setCartDiscount(new Money(discount, command.currency()));
        redemptionRepository.save(redemption);

        log.info("Apply succeeded sagaId={} code={} discount={} {}",
                command.sagaId(), promotion.getCode(), discount, command.currency());

        eventPublisher.publishApplied(
                command.sagaId(),
                promotion.getId(),
                promotion.getCode(),
                discount,
                command.currency()
        );
    }

    @Override
    @Transactional
    public void consumeRevertCommand(RevertPromotionCommand command) {
        // Idempotency / anomaly: the redemption row is the source of truth.
        // If it's missing, this is either a duplicate revert (we already deleted it)
        // or we never applied — both indistinguishable here, both go to DLQ for triage.
        PromotionRedemption redemption = redemptionRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException(
                        "Revert: no redemption for sagaId=" + command.sagaId()
                                + " — never applied or already reverted (DLQ for triage)"));

        Promotion promotion = promotionRepository.findById(redemption.getPromotionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Revert: redemption references missing promotion id="
                                + redemption.getPromotionId()));

        int updated = promotionRepository.tryDecrementTimesRedeemed(promotion.getId());
        if (updated == 0) {
            throw new IllegalStateException(
                    "Revert: counter already 0 for promotion id=" + promotion.getId()
                            + " sagaId=" + command.sagaId() + " (DLQ for triage)");
        }

        redemptionRepository.delete(redemption);

        log.info("Revert succeeded sagaId={} code={} promotionId={}",
                command.sagaId(), promotion.getCode(), promotion.getId());

        eventPublisher.publishReverted(command.sagaId(), promotion.getId(), promotion.getCode());
    }

    // ---- helpers ----
    private Optional<ValidationFailure> checkAvailability(Promotion p, BigDecimal cartTotal) {
        if (!p.isActive()) {
            return Optional.of(ValidationFailure.INACTIVE);
        }
        Instant now = Instant.now();
        if (now.isBefore(p.getValidFrom())) {
            return Optional.of(ValidationFailure.NOT_YET_VALID);
        }
        if (now.isAfter(p.getValidUntil())) {
            return Optional.of(ValidationFailure.EXPIRED);
        }
        if (p.getMaxUses() != null && p.getTimesRedeemed() >= p.getMaxUses()) {
            return Optional.of(ValidationFailure.MAX_USES_REACHED);
        }
        if (p.getMinCartTotal() != null && cartTotal.compareTo(p.getMinCartTotal()) < 0) {
            return Optional.of(ValidationFailure.CART_BELOW_MINIMUM);
        }
        return Optional.empty();
    }

    private BigDecimal computeDiscount(Promotion promotion, BigDecimal cartTotal) {
        return switch (promotion.getDiscountType()) {
            case PERCENTAGE -> cartTotal
                    .multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            case FIXED_AMOUNT -> {
                // Discount can't exceed cart total.
                BigDecimal value = promotion.getDiscountValue();
                yield value.compareTo(cartTotal) > 0 ? cartTotal : value;
            }
        };
    }
}