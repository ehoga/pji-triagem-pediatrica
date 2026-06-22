package com.pji.triagem.dto.request;

import lombok.Data;

@Data
public class RegisterForm {

    private String login;
    private String password;
    private String email;
    private String name;

}
