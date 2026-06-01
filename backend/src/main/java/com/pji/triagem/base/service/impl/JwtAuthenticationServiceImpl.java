package com.pji.triagem.base.service.impl;

import com.pji.triagem.base.dto.BaseAuthDTO;
import com.pji.triagem.base.provider.JwtTokenProvider;
import com.pji.triagem.base.service.JwtAuthenticationService;
import com.pji.triagem.exception.BlockedUserException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {

    private final JwtTokenProvider tokenProvider;

    public Authentication getAuthentication(String token) {

        if (!tokenProvider.validateToken(token)) {
            return null;
        }

        if(tokenProvider.isBlockedTemporary(token)) {
            throw new BlockedUserException("Usuário temporariamente bloqueado, altere sua senha.");
        }

        String username = tokenProvider.extractUsername(token);
        String id = tokenProvider.extractId(token);
        List<String> roles = tokenProvider.extractRoles(token);

        if (username == null || roles == null || roles.isEmpty()) {
            return null;
        }

        // converte cada role em GrantedAuthority
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        BaseAuthDTO principal = new BaseAuthDTO(username, id);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        return  tokenProvider.getTokenFromRequest(request);
    }
}
