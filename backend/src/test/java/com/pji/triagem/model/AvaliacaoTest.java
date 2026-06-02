package com.pji.triagem.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AvaliacaoTest {

    @Test
    void prePersistDefineCriadoEmQuandoNaoInformado() {
        Sintoma sintoma = new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1);
        List<RespostaAvaliacao> respostas = List.of(new RespostaAvaliacao("temperatura", "38_39"));
        Avaliacao avaliacao = new Avaliacao(10L, sintoma, Classificacao.MOD, 4, false, respostas);

        avaliacao.prePersist();

        assertThat(avaliacao.getCriadoEm()).isNotNull();
        assertThat(avaliacao.getCriancaId()).isEqualTo(10L);
        assertThat(avaliacao.getSintoma()).isSameAs(sintoma);
        assertThat(avaliacao.getClassificacao()).isEqualTo(Classificacao.MOD);
        assertThat(avaliacao.getScore()).isEqualTo(4);
        assertThat(avaliacao.getRedFlagDetected()).isFalse();
        assertThat(avaliacao.getRespostas()).containsExactlyElementsOf(respostas);
        assertThat(avaliacao.getProtocoloVersao()).isEqualTo("1.0.0");
    }
}
