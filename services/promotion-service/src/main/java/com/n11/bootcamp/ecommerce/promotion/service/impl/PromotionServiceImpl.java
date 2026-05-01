package com.n11.bootcamp.ecommerce.promotion.service.impl;

import com.n11.bootcamp.ecommerce.promotion.dto.CreatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionValidationResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.UpdatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.dto.ValidationFailure;
import com.n11.bootcamp.ecommerce.promotion.entity.Promotion;
import com.n11.bootcamp.ecommerce.promotion.exception.DuplicatePromotionCodeException;
import com.n11.bootcamp.ecommerce.promotion.exception.PromotionNotFoundException;
import com.n11.bootcamp.ecommerce.promotion.mapper.PromotionMapper;
import com.n11.bootcamp.ecommerce.promotion.repository.PromotionRepository;
import com.n11.bootcamp.ecommerce.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional
    public PromotionResponse create(CreatePromotionRequest request) {
        String normalizedCode = request.code().toUpperCase();
        if (promotionRepository.existsByCode(normalizedCode)) {
            throw new DuplicatePromotionCodeException(normalizedCode);
        }

        Promotion promotion = promotionMapper.toEntity(request);
        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PromotionResponse update(Long id, UpdatePromotionRequest request) {
        Promotion existing = loadOrThrow(id);
        promotionMapper.updateEntity(existing, request);
        return promotionMapper.toResponse(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Promotion existing = loadOrThrow(id);
        promotionRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getById(Long id) {
        return promotionMapper.toResponse(loadOrThrow(id));
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
    public PromotionValidationResponse validate(String code) {
        String normalizedCode = code.toUpperCase();
        Promotion promotion = promotionRepository.findByCode(normalizedCode).orElse(null);

        if (promotion == null) {
            return PromotionValidationResponse.invalid(normalizedCode, ValidationFailure.NOT_FOUND);
        }

        Instant now = Instant.now();

        if (!promotion.isActive()) {
            return PromotionValidationResponse.invalid(normalizedCode, ValidationFailure.INACTIVE);
        }
        if (now.isBefore(promotion.getValidFrom())) {
            return PromotionValidationResponse.invalid(normalizedCode, ValidationFailure.NOT_YET_VALID);
        }
        if (now.isAfter(promotion.getValidUntil())) {
            return PromotionValidationResponse.invalid(normalizedCode, ValidationFailure.EXPIRED);
        }
        if (promotion.getMaxUses() != null && promotion.getTimesRedeemed() >= promotion.getMaxUses()) {
            return PromotionValidationResponse.invalid(normalizedCode, ValidationFailure.MAX_USES_REACHED);
        }

        return promotionMapper.toValidationResponse(promotion);
    }

    private Promotion loadOrThrow(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException(id));
    }
}