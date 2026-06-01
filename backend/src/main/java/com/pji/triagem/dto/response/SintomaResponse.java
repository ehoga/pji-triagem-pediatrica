package com.pji.triagem.dto.response;

import com.pji.triagem.model.Sintoma;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sintoma disponível no catálogo de triagem")
public record SintomaResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "fever")
        String codigo,

        @Schema(example = "Febre")
        String nome,

        @Schema(example = "Temperatura acima de 37.8°C")
        String descricaoCurta,

        @Schema(example = "thermo")
        String iconeRef,

        @Schema(example = "#F59E5C")
        String corHex,

        @Schema(example = "1")
        Integer ordem
) {

    public static SintomaResponse from(Sintoma sintoma) {
        return new SintomaResponse(
                sintoma.getId(),
                sintoma.getCodigo(),
                sintoma.getNome(),
                sintoma.getDescricaoCurta(),
                sintoma.getIconeRef(),
                sintoma.getCorHex(),
                sintoma.getOrdem()
        );
    }
}
