package com.pji.triagem.service.impl.auth;

import com.pji.triagem.dto.response.UserAuth;
import com.pji.triagem.model.TypeUser;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof CustomUsernamePasswordToken customToken)) {
            throw new BadCredentialsException("Tipo de token inválido");
        }

        String username = customToken.getPrincipal().toString();
        String password = customToken.getCredentials().toString();
        TypeUser typeUser = customToken.getTypeUser();

        // Carrega o usuário com base no username e typeUser
        UserAuth userDetails = customUserDetailsService.loadUserByUsernameAndType(username, typeUser);

        // Verifica a senha (você pode usar um PasswordEncoder aqui)
        if (!new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Senha incorreta");
        }

        // Retorna o token autenticado com as permissões do usuário
        return new CustomUsernamePasswordToken(userDetails, password, typeUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomUsernamePasswordToken.class.isAssignableFrom(authentication);
    }

}
