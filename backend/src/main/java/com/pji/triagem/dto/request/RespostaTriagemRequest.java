package com.pji.triagem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RespostaTriagemRequest(
        @NotBlank
        String perguntaCodigo,

        @NotBlank
        String valor
) {
}
