package com.pji.triagem.dtos;

import com.pji.triagem.models.Sintoma;

public record SintomaResumoResponse(
        Long id,
        String codigo,
        String nome
) {

    public static SintomaResumoResponse from(Sintoma sintoma) {
        return new SintomaResumoResponse(sintoma.getId(), sintoma.getCodigo(), sintoma.getNome());
    }
}
