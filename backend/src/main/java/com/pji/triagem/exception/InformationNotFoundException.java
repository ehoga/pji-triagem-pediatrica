package com.pji.triagem.exception;

import java.io.Serial;

public class InformationNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public InformationNotFoundException() {
    }
    public InformationNotFoundException(String message) {
        super(message);
    }
}
