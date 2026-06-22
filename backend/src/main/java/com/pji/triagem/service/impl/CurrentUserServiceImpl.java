package com.pji.triagem.service.impl;

import com.pji.triagem.base.dto.BaseAuthDTO;
import com.pji.triagem.dto.response.UserAuth;
import com.pji.triagem.exception.ValidationException;
import com.pji.triagem.model.TypeUser;
import com.pji.triagem.model.User;
import com.pji.triagem.service.CurrentUserService;
import com.pji.triagem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserService userService;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ValidationException("Usuário autenticado não encontrado");
        }

        Object principal = authentication.getPrincipal();
        Long userId;

        if (principal instanceof BaseAuthDTO authDTO) {
            userId = Long.valueOf(authDTO.getUuid());
        } else if (principal instanceof UserAuth userAuth) {
            userId = userAuth.getId();
        } else {
            throw new ValidationException("Usuário autenticado inválido");
        }

        return userService.find(userId);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && TypeUser.ADMIN.equals(user.getType());
    }
}
