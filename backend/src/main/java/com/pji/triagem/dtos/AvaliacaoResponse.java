package com.pji.triagem.dtos;

import com.pji.triagem.enums.Classificacao;
import java.time.LocalDateTime;
import java.util.List;

public record AvaliacaoResponse(
        Long id,
        Classificacao classificacao,
        Integer score,
        Boolean redFlagDetected,
        String recomendacao,
        List<String> chips,
        LocalDateTime criadoEm,
        CriancaResumoResponse crianca,
        SintomaResumoResponse sintoma
) {
}
