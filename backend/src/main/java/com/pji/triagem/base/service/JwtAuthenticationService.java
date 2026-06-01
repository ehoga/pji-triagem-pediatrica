package com.pji.triagem.base.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtAuthenticationService {
    Authentication getAuthentication(String token);
    String getTokenFromRequest(HttpServletRequest request);
}
