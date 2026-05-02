package com.n11.bootcamp.ecommerce.cart.client;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "promotion-service")
public interface PromotionServiceClient {


    @GetMapping("/products/{id}")
    ProductSummaryDto getProduct(@PathVariable("id") long id);
}