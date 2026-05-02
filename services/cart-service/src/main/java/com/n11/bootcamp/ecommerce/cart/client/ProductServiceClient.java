package com.n11.bootcamp.ecommerce.cart.client;

import com.n11.bootcamp.ecommerce.cart.client.dto.ProductBatchRequest;
import com.n11.bootcamp.ecommerce.cart.client.dto.ProductBatchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PostMapping("/products/batch")
    ProductBatchResponse batchProducts(@RequestBody ProductBatchRequest request);
}