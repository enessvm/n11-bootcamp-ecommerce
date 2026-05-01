package com.n11.bootcamp.ecommerce.product.client.dto;

import java.util.List;

public record StockBatchRequest(
        List<Long> productIds
) {}