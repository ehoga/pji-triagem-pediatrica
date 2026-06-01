package com.pji.triagem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.pji.triagem.dto.response.PerguntaResponse;
import com.pji.triagem.model.OpcaoPergunta;
import com.pji.triagem.model.Pergunta;
import com.pji.triagem.model.PesoYesNo;
import com.pji.triagem.model.Sintoma;
import com.pji.triagem.model.TipoPergunta;
import com.pji.triagem.repository.PerguntaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class PerguntaServiceTest {

    private final Sintoma sintoma = new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1);

    @Test
    void listaQuestionarioSemExporScoreOuRedFlag() {
        Pergunta options = new Pergunta(sintoma, "temperatura", "Qual foi a maior temperatura?", null, TipoPergunta.OPTIONS, 1);
        options.adicionarOpcao(new OpcaoPergunta("ate_38", "Até 38°C", 1, false, 1));
        options.adicionarOpcao(new OpcaoPergunta("acima_39", "Acima de 39°C", 4, true, 2));

        Pergunta yesNo = new Pergunta(sintoma, "respiracao", "Está com dificuldade para respirar?", null, TipoPergunta.YESNO, 2);
        yesNo.definirPesoYesNo(new PesoYesNo(6, 0, 3, true, false));

        PerguntaRepository repository = org.mockito.Mockito.mock(PerguntaRepository.class);
        when(repository.findBySintomaIdOrderByOrdemAsc(1L)).thenReturn(List.of(options, yesNo));

        List<PerguntaResponse> response = new PerguntaService(repository).listarPorSintoma(1L);

        assertThat(response).hasSize(2);
        assertThat(response.getFirst().opcoes())
                .extracting("codigo")
                .containsExactly("ate_38", "acima_39");
        assertThat(response.get(1).opcoes())
                .extracting("codigo")
                .containsExactly("yes", "no", "dunno");
    }
}
