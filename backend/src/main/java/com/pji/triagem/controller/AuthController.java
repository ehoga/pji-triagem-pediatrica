package com.pji.triagem.controller;

import com.pji.triagem.base.dto.ResponseDTO;
import com.pji.triagem.base.service.ResponseService;
import com.pji.triagem.dto.request.LoginForm;
import com.pji.triagem.dto.request.RegisterForm;
import com.pji.triagem.dto.response.TokenResponse;
import com.pji.triagem.model.TypeUser;
import com.pji.triagem.model.User;
import com.pji.triagem.service.AuthService;
import com.pji.triagem.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final ResponseService responseService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<TokenResponse>> loginUser(@RequestBody LoginForm form) {
        return responseService.ok(authService.login(form.getLogin(), form.getPassword(), TypeUser.USER));
    }


    @PostMapping("/register/user")
    public ResponseEntity<ResponseDTO<String>> registerClient(@RequestBody RegisterForm form) {
        userService.registerClientUser(form.getLogin(), form.getPassword(), TypeUser.USER, form.getEmail(), form.getName());
        return responseService.created("Usuario criado com sucesso");
    }

}
