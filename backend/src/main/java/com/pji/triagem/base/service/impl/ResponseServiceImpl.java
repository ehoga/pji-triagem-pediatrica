package com.pji.triagem.base.service.impl;

import com.pji.triagem.base.dto.ResponseDTO;
import com.pji.triagem.base.service.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseServiceImpl implements ResponseService {

    private final MessageServiceImpl messageServiceImpl;

    public ResponseServiceImpl(MessageServiceImpl messageServiceImpl) {
        this.messageServiceImpl = messageServiceImpl;
    }

    public <T> ResponseEntity<ResponseDTO<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(data));
    }

    public <T> ResponseEntity<ResponseDTO<T>> ok(T data) {
        return ResponseEntity.ok(new ResponseDTO<>(data));
    }

    @Override
    public ResponseEntity<ResponseDTO<String>> ok(String response) {
        return ResponseEntity.ok(new ResponseDTO<>(response));
    }


    public <T> ResponseEntity<ResponseDTO<String>> ok(String messageSource, String text) {
        return ResponseEntity.ok(new ResponseDTO<>(messageServiceImpl.getMessage(messageSource) + text));
    }

    public <T> ResponseEntity<T> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
