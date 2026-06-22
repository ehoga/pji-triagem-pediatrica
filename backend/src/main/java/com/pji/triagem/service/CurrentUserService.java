package com.pji.triagem.service;

import com.pji.triagem.model.User;

public interface CurrentUserService {

    User getCurrentUser();

    boolean isAdmin(User user);
}
