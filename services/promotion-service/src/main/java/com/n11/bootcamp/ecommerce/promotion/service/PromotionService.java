package com.n11.bootcamp.ecommerce.promotion.service;

import com.n11.bootcamp.ecommerce.promotion.dto.CreatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionValidationResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.UpdatePromotionRequest;

import java.util.List;

public interface PromotionService {

    PromotionResponse create(CreatePromotionRequest request);

    PromotionResponse update(Long id, UpdatePromotionRequest request);

    void delete(Long id);

    PromotionResponse getById(Long id);

    List<PromotionResponse> listAll();

    PromotionValidationResponse validate(String code);
}