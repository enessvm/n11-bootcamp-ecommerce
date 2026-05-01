package com.n11.bootcamp.ecommerce.product.client;

import com.n11.bootcamp.ecommerce.product.client.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.product.client.dto.StockBatchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @PostMapping("/stock/batch")
    StockBatchResponse batch(@RequestBody StockBatchRequest request);
}