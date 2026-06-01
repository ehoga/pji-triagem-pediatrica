package com.pji.triagem.exception;

import lombok.Generated;

public class ResourceNotFoundException extends RuntimeException {

    private final String resource;

    public ResourceNotFoundException(String message, String resource) {
        super(message);
        this.resource = resource;
    }

    @Generated
    public ResourceNotFoundException(String resource) {
        this.resource = resource;
    }

    @Generated
    public String getResource() {
        return this.resource;
    }
}
