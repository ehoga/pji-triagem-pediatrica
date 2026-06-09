package com.pji.triagem.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AvaliacaoRequest(
        @NotNull
        Long criancaId,

        @NotNull
        Long sintomaId,

        @NotEmpty
        List<@Valid RespostaTriagemRequest> respostas
) {
}
