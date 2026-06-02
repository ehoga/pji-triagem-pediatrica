package com.pji.triagem.dto.response;

import com.pji.triagem.model.OpcaoPergunta;
import com.pji.triagem.model.Pergunta;
import com.pji.triagem.model.TipoPergunta;
import java.util.Comparator;
import java.util.List;

public record PerguntaResponse(
        Long id,
        String codigo,
        String texto,
        String sub,
        TipoPergunta tipo,
        Integer ordem,
        List<OpcaoPerguntaResponse> opcoes
) {

    public static PerguntaResponse from(Pergunta pergunta) {
        List<OpcaoPerguntaResponse> opcoes = pergunta.getTipo() == TipoPergunta.OPTIONS
                ? pergunta.getOpcoes().stream()
                        .sorted(Comparator.comparing(OpcaoPergunta::getOrdem))
                        .map(OpcaoPerguntaResponse::from)
                        .toList()
                : List.of(
                        new OpcaoPerguntaResponse("yes", "Sim", 1),
                        new OpcaoPerguntaResponse("no", "Não", 2),
                        new OpcaoPerguntaResponse("dunno", "Não sei", 3)
                );

        return new PerguntaResponse(
                pergunta.getId(),
                pergunta.getCodigo(),
                pergunta.getTexto(),
                pergunta.getSub(),
                pergunta.getTipo(),
                pergunta.getOrdem(),
                opcoes
        );
    }
}
