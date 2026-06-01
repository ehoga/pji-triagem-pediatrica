package com.pji.triagem.service.impl;

import com.pji.triagem.base.repository.BaseRepository;
import com.pji.triagem.base.service.impl.BaseServiceImpl;
import com.pji.triagem.base.utils.ReplaceUtils;
import com.pji.triagem.base.validator.DocumentValidator;
import com.pji.triagem.dto.response.UserAuth;
import com.pji.triagem.exception.InvalidLoginException;
import com.pji.triagem.exception.ResourceNotFoundException;
import com.pji.triagem.exception.ValidationException;
import com.pji.triagem.factory.DocumentValidatorFactory;
import com.pji.triagem.factory.UserFactory;
import com.pji.triagem.model.TypeUser;
import com.pji.triagem.model.User;
import com.pji.triagem.repository.UserRepository;
import com.pji.triagem.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected BaseRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public String getResourceName() {
        return "User";
    }


    public User findByLoginAndType(String login, TypeUser typeUser) {
        return userRepository.findByLoginAndType(login,typeUser)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName()));
    }

    public User findByUserAndType(Long id, TypeUser typeUser) {
        return userRepository.findByIdType(id,typeUser)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName()));
    }

    public void resetLoginAttempts(String login, TypeUser typeUser) {
        userRepository.updateAttemptsByLoginAndType(login,typeUser,0);
    }

    public void addLoginAttemps(String login, TypeUser typeUser) {
        User user = getSelf().findByLoginAndType(login, typeUser);
        user.setAttemptsCount(user.getAttemptsCount() + 1);
        if(user.getAttemptsCount() > 5){
            user.setIsBlocked(true);
        }
        getSelf().save(user);
    }

    public void isActive(Long id){
        User user = getSelf().find(id);
        user.setIsActive(!user.getIsActive());
        getSelf().save(user);
    }


    public User registerUser(String login, String password, TypeUser type) {
        login = ReplaceUtils.refactoryString(login);
        password = ReplaceUtils.removeSpacesEmpty(password);
        validateRegisterUser(login, type);
        return getSelf().save(UserFactory.createUserTypeClient(login, password));

    }

    public void validateRegisterUser(String login, TypeUser type) {
        DocumentValidator validator = DocumentValidatorFactory.getValidator(login);
        if(!validator.isValid(login)){
            throw new ValidationException("Não foi possivel criar o usuário , pois o CPF/CNPJ não é válido");
        }
        if(userRepository.existsByLoginAndType(login, type)){
            throw new ValidationException("Não foi possivel criar o usuário , pois o CPF já está cadastrado");
        }
    }

    private UserServiceImpl getSelf() {
        return (UserServiceImpl) AopContext.currentProxy();
    }

    public UserAuth loadUserById(Long id) {
        User userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (userEntity.getIsBlocked()) {
            throw new InvalidLoginException("Usuário bloqueado");
        }

        return UserAuth.fromEntity(userEntity);
    }

    @Override
    public User registerClientUser(String login, String password, TypeUser typeUser) {
        login = ReplaceUtils.refactoryString(login);
        password = ReplaceUtils.removeSpacesEmpty(password);

        DocumentValidator validator = DocumentValidatorFactory.getValidator(login);
        if(!validator.isValid(login)){
            throw new ValidationException("Não foi possivel criar o usuário , pois o CPF/CNPJ não é válido");
        }

        User newUser = UserFactory.createUserTypeClient(login, password);
        return userRepository.findByLoginAndType(login, typeUser)
                .orElseGet(() -> getSelf().save(newUser));
    }


}
