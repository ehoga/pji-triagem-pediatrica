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

class FebreQuestionarioSeederTest {

    private final Sintoma febre = new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1);

    @Test
    void questionarioTemCincoPerguntasComRedFlags() {
        List<Pergunta> perguntas = FebreQuestionarioSeeder.questionario(febre);

        assertThat(perguntas).hasSize(5);
        assertThat(perguntas)
                .extracting(Pergunta::getCodigo)
                .containsExactly("idade_menor_3m", "temperatura", "petequias", "respiracao_dificil", "prostracao");
        assertThat(perguntas)
                .filteredOn(pergunta -> pergunta.getTipo() == TipoPergunta.YESNO)
                .allSatisfy(pergunta -> assertThat(pergunta.getPesoYesNo().getRedFlagOnYes()).isTrue());
    }

    @Test
    void engineAlcancaOsTresNiveisERedFlag() {
        List<Pergunta> perguntas = FebreQuestionarioSeeder.questionario(febre);
        TriagemService triagemService = new TriagemService(repository(perguntas));

        assertThat(triagemService.classificar(1L, List.of(resposta("temperatura", "ate_38"))).classificacao())
                .isEqualTo(Classificacao.LOW);
        assertThat(triagemService.classificar(1L, List.of(resposta("temperatura", "acima_39"))).classificacao())
                .isEqualTo(Classificacao.MOD);
        assertThat(triagemService.classificar(1L, List.of(
                resposta("temperatura", "acima_39"),
                resposta("idade_menor_3m", "dunno")
        )).classificacao()).isEqualTo(Classificacao.HIGH);
        assertThat(triagemService.classificar(1L, List.of(resposta("idade_menor_3m", "yes"))).redFlagDetected())
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
