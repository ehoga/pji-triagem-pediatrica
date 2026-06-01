package com.pji.triagem.base.utils;

import com.pji.triagem.base.dto.BaseAuthDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static BaseAuthDTO getAuthDTO() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            return (BaseAuthDTO) authentication.getPrincipal();
        }
        return null;
    }

    public static String getToken() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            return authentication.getCredentials().toString();
        }
        return null;
    }
    public  static String buildToken(String token) {
        return BEARER_PREFIX + token;
    }
}
