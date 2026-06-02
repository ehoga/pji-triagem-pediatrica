package com.pji.triagem.dto.response;

import com.pji.triagem.model.Classificacao;
import java.util.List;

public record ResultadoTriagemResponse(
        Classificacao classificacao,
        Integer score,
        Boolean redFlagDetected,
        String recomendacao,
        List<String> chips
) {
}
