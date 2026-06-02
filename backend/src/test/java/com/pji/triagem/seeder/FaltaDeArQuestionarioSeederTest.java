package com.pji.triagem.seeder;

import static org.assertj.core.api.Assertions.assertThat;

import com.pji.triagem.dto.request.RespostaTriagemRequest;
import com.pji.triagem.model.Classificacao;
import com.pji.triagem.model.Pergunta;
import com.pji.triagem.model.Sintoma;
import com.pji.triagem.model.TipoPergunta;
import com.pji.triagem.repository.PerguntaRepository;
import com.pji.triagem.service.TriagemService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FaltaDeArQuestionarioSeederTest {

    private final Sintoma faltaDeAr = new Sintoma("breath", "Falta de ar", "Respiração rápida ou difícil", "lung", "#EB5757", 6);

    @Test
    void questionarioTemCincoPerguntasComRedFlagsRespiratorias() {
        List<Pergunta> perguntas = FaltaDeArQuestionarioSeeder.questionario(faltaDeAr);

        assertThat(perguntas).hasSize(5);
        assertThat(perguntas)
                .extracting(Pergunta::getCodigo)
                .containsExactly("cianose", "tiragem_grave", "fala_alimenta", "respiracao", "sonolencia");
        assertThat(perguntas)
                .filteredOn(pergunta -> pergunta.getTipo() == TipoPergunta.YESNO)
                .allSatisfy(pergunta -> assertThat(pergunta.getPesoYesNo().getRedFlagOnYes()).isTrue());
    }

    @Test
    void engineAlcancaOsTresNiveisERedFlag() {
        List<Pergunta> perguntas = FaltaDeArQuestionarioSeeder.questionario(faltaDeAr);
        TriagemService triagemService = new TriagemService(repository(perguntas));

        assertThat(triagemService.classificar(1L, List.of(resposta("respiracao", "normal"))).classificacao())
                .isEqualTo(Classificacao.LOW);
        assertThat(triagemService.classificar(1L, List.of(resposta("respiracao", "rapida"))).classificacao())
                .isEqualTo(Classificacao.MOD);
        assertThat(triagemService.classificar(1L, List.of(resposta("respiracao", "muito_rapida"))).classificacao())
                .isEqualTo(Classificacao.HIGH);
        assertThat(triagemService.classificar(1L, List.of(resposta("fala_alimenta", "nao_consegue"))).redFlagDetected())
                .isTrue();
    }

    private PerguntaRepository repository(List<Pergunta> perguntas) {
        PerguntaRepository repository = org.mockito.Mockito.mock(PerguntaRepository.class);
        for (Pergunta pergunta : perguntas) {
            org.mockito.Mockito.when(repository.findBySintomaIdAndCodigo(1L, pergunta.getCodigo()))
                    .thenReturn(Optional.of(pergunta));
        }
        return repository;
    }

    private RespostaTriagemRequest resposta(String perguntaCodigo, String valor) {
        return new RespostaTriagemRequest(perguntaCodigo, valor);
    }
}
