package com.n11.bootcamp.ecommerce.promotion.controller;

import com.n11.bootcamp.ecommerce.promotion.dto.CreatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.PromotionValidationResponse;
import com.n11.bootcamp.ecommerce.promotion.dto.UpdatePromotionRequest;
import com.n11.bootcamp.ecommerce.promotion.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/promotions")
    public ResponseEntity<PromotionResponse> create(@Valid @RequestBody CreatePromotionRequest request) {
        PromotionResponse created = promotionService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/promotions/{id}")
    public PromotionResponse update(@PathVariable Long id,
                                    @Valid @RequestBody UpdatePromotionRequest request) {
        return promotionService.update(id, request);
    }

    @DeleteMapping("/promotions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        promotionService.delete(id);
    }

    @GetMapping("/promotions/{id}")
    public PromotionResponse getById(@PathVariable Long id) {
        return promotionService.getById(id);
    }

    @GetMapping("/promotions")
    public List<PromotionResponse> listAll() {
        return promotionService.listAll();
    }

    @GetMapping("/promotions/validate/{code}")
    public PromotionValidationResponse validate(@PathVariable String code) {
        return promotionService.validate(code);
    }
}