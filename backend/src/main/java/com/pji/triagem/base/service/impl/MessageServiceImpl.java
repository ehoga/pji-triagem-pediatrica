package com.pji.triagem.base.service.impl;

import jakarta.persistence.criteria.Path;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
@Service
public class MessageServiceImpl {
    protected MessageSource messageSource;

    public MessageServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(FieldError fieldError) {
        return messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
    }

    public String getMessage(Path<?> path) {
        String message;
        try {
            message = messageSource.getMessage(path.toString(), null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            message = path.toString();
        }
        return message;
    }
}
