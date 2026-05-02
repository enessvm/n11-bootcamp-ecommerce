package com.n11.bootcamp.ecommerce.order.client;

import com.n11.bootcamp.ecommerce.order.client.dto.UserProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/me")
    UserProfileRequest getCurrentProfile();
}