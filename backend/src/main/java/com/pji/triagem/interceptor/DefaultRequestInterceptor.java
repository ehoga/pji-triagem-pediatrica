package com.pji.triagem.interceptor;

import com.pji.triagem.base.utils.AuthUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class DefaultRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = AuthUtils.getToken();

        if (token != null) {
            requestTemplate.header(AuthUtils.AUTH_HEADER, AuthUtils.buildToken(token));
        }

    }
}
