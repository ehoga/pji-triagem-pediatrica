package com.pji.triagem.service.impl.auth;

import com.pji.triagem.dto.response.UserAuth;
import com.pji.triagem.model.TypeUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Objects;

@Getter
@Setter
public class CustomUsernamePasswordToken extends UsernamePasswordAuthenticationToken {

    private TypeUser typeUser;

    public CustomUsernamePasswordToken(UserAuth userAuth, String password, TypeUser typeUser) {
        super(userAuth, password);
        this.typeUser = typeUser;
    }

    public CustomUsernamePasswordToken(String login, String password, TypeUser typeUser) {
        super(login, password);
        this.typeUser = typeUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CustomUsernamePasswordToken that = (CustomUsernamePasswordToken) o;
        return typeUser == that.typeUser;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(typeUser);
        return result;
    }
}
