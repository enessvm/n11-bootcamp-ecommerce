package com.n11.bootcamp.ecommerce.order.client.dto;

import java.util.List;

public record ProductBatchRequest(
        List<Long> ids
) {}