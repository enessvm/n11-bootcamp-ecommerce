package com.n11.bootcamp.ecommerce.user.service;

import com.n11.bootcamp.ecommerce.user.dto.AuthResponse;
import com.n11.bootcamp.ecommerce.user.dto.LoginRequest;
import com.n11.bootcamp.ecommerce.user.dto.RefreshRequest;
import com.n11.bootcamp.ecommerce.user.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(String refreshToken);
}
