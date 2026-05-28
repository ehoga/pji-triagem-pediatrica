package com.pji.triagem.factory;

import com.pji.triagem.exception.InvalidLoginException;
import com.pji.triagem.model.TypeUser;
import com.pji.triagem.service.LoginProcessor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LoginProcessorFactory {

    private final Map<TypeUser, LoginProcessor> processorMap;

    //constructor
    public LoginProcessorFactory(List<LoginProcessor> processors) {
        this.processorMap = processors.stream()
                .collect(Collectors.toMap(LoginProcessor::getTypeUser, Function.identity()));
    }

    public LoginProcessor getProcessor(TypeUser typeUser) {
        return Optional.ofNullable(processorMap.get(typeUser))
                .orElseThrow(() -> new InvalidLoginException("Tipo de usuário não suportado"));
    }
}