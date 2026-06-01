package com.pji.triagem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.pji.triagem.dto.request.RespostaTriagemRequest;
import com.pji.triagem.dto.response.ResultadoTriagemResponse;
import com.pji.triagem.model.Classificacao;
import com.pji.triagem.model.OpcaoPergunta;
import com.pji.triagem.model.Pergunta;
import com.pji.triagem.model.PesoYesNo;
import com.pji.triagem.model.Sintoma;
import com.pji.triagem.model.TipoPergunta;
import com.pji.triagem.repository.PerguntaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TriagemServiceTest {

    private final Sintoma sintoma = new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1);
    private final PerguntaRepository repository = org.mockito.Mockito.mock(PerguntaRepository.class);
    private final TriagemService service = new TriagemService(repository);

    @Test
    void redFlagSempreClassificaComoHigh() {
        Pergunta pergunta = yesNo("red_flag", 1, 0, 0, true);
        when(repository.findBySintomaIdAndCodigo(1L, "red_flag")).thenReturn(Optional.of(pergunta));

        ResultadoTriagemResponse resultado = service.classificar(1L, List.of(new RespostaTriagemRequest("red_flag", "yes")));

        assertThat(resultado.classificacao()).isEqualTo(Classificacao.HIGH);
        assertThat(resultado.redFlagDetected()).isTrue();
    }

    @Test
    void scoreEntreZeroEDoisClassificaComoLow() {
        Pergunta pergunta = options("duracao", opcao("curta", 2, false));
        when(repository.findBySintomaIdAndCodigo(1L, "duracao")).thenReturn(Optional.of(pergunta));

        ResultadoTriagemResponse resultado = service.classificar(1L, List.of(new RespostaTriagemRequest("duracao", "curta")));

        assertThat(resultado.classificacao()).isEqualTo(Classificacao.LOW);
        assertThat(resultado.score()).isEqualTo(2);
    }

    @Test
    void scoreEntreTresECincoClassificaComoMod() {
        Pergunta pergunta = options("duracao", opcao("media", 4, false));
        when(repository.findBySintomaIdAndCodigo(1L, "duracao")).thenReturn(Optional.of(pergunta));

        ResultadoTriagemResponse resultado = service.classificar(1L, List.of(new RespostaTriagemRequest("duracao", "media")));

        assertThat(resultado.classificacao()).isEqualTo(Classificacao.MOD);
    }

    @Test
    void scoreMaiorOuIgualASeisClassificaComoHigh() {
        Pergunta pergunta = options("duracao", opcao("longa", 6, false));
        when(repository.findBySintomaIdAndCodigo(1L, "duracao")).thenReturn(Optional.of(pergunta));

        ResultadoTriagemResponse resultado = service.classificar(1L, List.of(new RespostaTriagemRequest("duracao", "longa")));

        assertThat(resultado.classificacao()).isEqualTo(Classificacao.HIGH);
    }

    private Pergunta yesNo(String codigo, int scoreYes, int scoreNo, int scoreDunno, boolean redFlagOnYes) {
        Pergunta pergunta = new Pergunta(sintoma, codigo, "Pergunta?", null, TipoPergunta.YESNO, 1);
        pergunta.definirPesoYesNo(new PesoYesNo(scoreYes, scoreNo, scoreDunno, redFlagOnYes, false));
        return pergunta;
    }

    private Pergunta options(String codigo, OpcaoPergunta... opcoes) {
        Pergunta pergunta = new Pergunta(sintoma, codigo, "Pergunta?", null, TipoPergunta.OPTIONS, 1);
        for (OpcaoPergunta opcao : opcoes) {
            pergunta.adicionarOpcao(opcao);
        }
        return pergunta;
    }

    private OpcaoPergunta opcao(String codigo, int score, boolean redFlag) {
        return new OpcaoPergunta(codigo, codigo, score, redFlag, 1);
    }
}
