package com.pji.triagem.service;


import com.pji.triagem.dto.response.TokenResponse;
import com.pji.triagem.model.TypeUser;

public interface AuthService {

    /**
     * Metódo para autenticar um usuário
     * @param login - login do usuário
     * @param password - senha do usuário
     * @return token do usuário
     */
    TokenResponse login(String login, String password, TypeUser typeUser);

    TokenResponse refreshTokens(String refreshToken);
}
