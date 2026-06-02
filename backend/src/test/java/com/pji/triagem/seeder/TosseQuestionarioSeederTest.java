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

class TosseQuestionarioSeederTest {

    private final Sintoma tosse = new Sintoma("cough", "Tosse", "Seca, com catarro ou persistente", "cough", "#56CCF2", 2);

    @Test
    void questionarioTemCincoPerguntasComRedFlagsRespiratorias() {
        List<Pergunta> perguntas = TosseQuestionarioSeeder.questionario(tosse);

        assertThat(perguntas).hasSize(5);
        assertThat(perguntas)
                .extracting(Pergunta::getCodigo)
                .containsExactly("cianose", "apneia", "frequencia", "chiado_grave", "vomita_apos_tosse");
        assertThat(perguntas)
                .filteredOn(pergunta -> pergunta.getTipo() == TipoPergunta.YESNO)
                .filteredOn(pergunta -> !"vomita_apos_tosse".equals(pergunta.getCodigo()))
                .allSatisfy(pergunta -> assertThat(pergunta.getPesoYesNo().getRedFlagOnYes()).isTrue());
    }

    @Test
    void engineAlcancaOsTresNiveisERedFlag() {
        List<Pergunta> perguntas = TosseQuestionarioSeeder.questionario(tosse);
        TriagemService triagemService = new TriagemService(repository(perguntas));

        assertThat(triagemService.classificar(1L, List.of(resposta("frequencia", "ocasional"))).classificacao())
                .isEqualTo(Classificacao.LOW);
        assertThat(triagemService.classificar(1L, List.of(resposta("frequencia", "crises"))).classificacao())
                .isEqualTo(Classificacao.MOD);
        assertThat(triagemService.classificar(1L, List.of(
                resposta("frequencia", "crises"),
                resposta("vomita_apos_tosse", "yes")
        )).classificacao()).isEqualTo(Classificacao.HIGH);
        assertThat(triagemService.classificar(1L, List.of(resposta("cianose", "yes"))).redFlagDetected())
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
