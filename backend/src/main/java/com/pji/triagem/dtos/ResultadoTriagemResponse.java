package com.pji.triagem.dtos;

import com.pji.triagem.enums.Classificacao;
import java.util.List;

public record ResultadoTriagemResponse(
        Classificacao classificacao,
        Integer score,
        Boolean redFlagDetected,
        String recomendacao,
        List<String> chips
) {
}
