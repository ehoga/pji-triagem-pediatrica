package com.pji.triagem.dto.response;

import com.pji.triagem.model.OpcaoPergunta;

public record OpcaoPerguntaResponse(
        String codigo,
        String texto,
        Integer ordem
) {

    public static OpcaoPerguntaResponse from(OpcaoPergunta opcao) {
        return new OpcaoPerguntaResponse(opcao.getCodigo(), opcao.getTexto(), opcao.getOrdem());
    }
}
