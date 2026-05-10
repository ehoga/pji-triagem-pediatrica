package com.pji.triagem.dtos;

import jakarta.validation.constraints.NotBlank;

public record RespostaTriagemRequest(
        @NotBlank
        String perguntaCodigo,

        @NotBlank
        String valor
) {
}
