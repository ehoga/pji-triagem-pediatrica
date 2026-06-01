package com.pji.triagem.base.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private T data;
    private List<String> errors = new ArrayList<>();
    private List<String> links = new ArrayList<>();

    public ResponseDTO() {
    }

    public ResponseDTO(T data) {
        super();
        this.data = data;
    }

    public ResponseDTO(T data, List<String> errors, List<String> links) {
        this.data = data;
        this.errors = errors;
        this.links = links;
    }
}
