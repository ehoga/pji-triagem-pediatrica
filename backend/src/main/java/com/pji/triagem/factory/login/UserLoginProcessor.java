package com.pji.triagem.factory.login;

import com.pji.triagem.base.provider.JwtTokenProvider;
import com.pji.triagem.dto.response.InfoLoginDTO;
import com.pji.triagem.dto.response.TokenPair;
import com.pji.triagem.dto.response.UserAuth;
import com.pji.triagem.model.TypeUser;
import com.pji.triagem.service.LoginProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserLoginProcessor implements LoginProcessor {

    private final JwtTokenProvider tokenProvider;

    @Override
    public InfoLoginDTO resolveName(Long userId) {
        return null;
    }

    @Override
    public TokenPair generateToken(UserAuth userAuth, InfoLoginDTO infoLoginDTO) {
        return new TokenPair(
                tokenProvider.createAccessToken(userAuth, null),
                tokenProvider.createRefreshToken(userAuth, null)
        );
    }

    @Override
    public TokenPair refreshToken(UserAuth userAuth, String name) {
        return null;
    }

    @Override
    public TypeUser getTypeUser() {
        return TypeUser.USER;
    }
}
