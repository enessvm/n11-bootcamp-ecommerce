package com.n11.bootcamp.ecommerce.order.util;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpResolver {

    private ClientIpResolver() {}

    public static String resolve(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null) {
            for (String token : forwarded.split(",")) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty()) {
                    return trimmed;
                }
            }
        }
        return request.getRemoteAddr();
    }
}