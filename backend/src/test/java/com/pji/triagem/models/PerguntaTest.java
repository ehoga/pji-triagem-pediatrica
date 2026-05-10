package com.pji.triagem.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.pji.triagem.enums.TipoPergunta;
import org.junit.jupiter.api.Test;

class PerguntaTest {

    private final Sintoma sintoma = new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1);

    @Test
    void adicionaOpcoesMantendoVinculoComPergunta() {
        Pergunta pergunta = new Pergunta(sintoma, "temperatura", "Qual foi a maior temperatura?", null, TipoPergunta.OPTIONS, 1);
        OpcaoPergunta opcao = new OpcaoPergunta("acima_39", "Acima de 39°C", 4, false, 1);

        pergunta.adicionarOpcao(opcao);

        assertThat(pergunta.getTipo()).isEqualTo(TipoPergunta.OPTIONS);
        assertThat(pergunta.getOpcoes()).containsExactly(opcao);
        assertThat(opcao.getPergunta()).isSameAs(pergunta);
    }

    @Test
    void definePesoYesNoMantendoVinculoComPergunta() {
        Pergunta pergunta = new Pergunta(sintoma, "red_flag", "Existe sinal de alerta?", null, TipoPergunta.YESNO, 1);
        PesoYesNo peso = new PesoYesNo(6, 0, 3, true, false);

        pergunta.definirPesoYesNo(peso);

        assertThat(pergunta.getTipo()).isEqualTo(TipoPergunta.YESNO);
        assertThat(pergunta.getPesoYesNo()).isSameAs(peso);
        assertThat(peso.getPergunta()).isSameAs(pergunta);
        assertThat(peso.getRedFlagOnYes()).isTrue();
    }
}
