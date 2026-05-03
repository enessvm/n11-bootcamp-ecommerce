package com.n11.bootcamp.ecommerce.order.dto;

import java.util.List;

public record OrderListResponse(
        List<OrderListEntry> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {}
