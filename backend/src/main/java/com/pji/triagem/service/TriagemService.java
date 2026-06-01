package com.pji.triagem.service;

import com.pji.triagem.dto.request.RespostaTriagemRequest;
import com.pji.triagem.dto.response.ResultadoTriagemResponse;
import com.pji.triagem.model.Classificacao;
import com.pji.triagem.model.OpcaoPergunta;
import com.pji.triagem.model.Pergunta;
import com.pji.triagem.model.PesoYesNo;
import com.pji.triagem.model.TipoPergunta;
import com.pji.triagem.repository.PerguntaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TriagemService {

    private final PerguntaRepository perguntaRepository;

    public TriagemService(PerguntaRepository perguntaRepository) {
        this.perguntaRepository = perguntaRepository;
    }

    @Transactional(readOnly = true)
    public ResultadoTriagemResponse classificar(Long sintomaId, List<RespostaTriagemRequest> respostas) {
        int scoreTotal = 0;
        boolean redFlagDetected = false;

        for (RespostaTriagemRequest resposta : respostas) {
            Pergunta pergunta = perguntaRepository.findBySintomaIdAndCodigo(sintomaId, resposta.perguntaCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada: " + resposta.perguntaCodigo()));

            ResultadoResposta resultado = pontuar(pergunta, resposta.valor());
            scoreTotal += resultado.score();
            redFlagDetected = redFlagDetected || resultado.redFlag();
        }

        Classificacao classificacao = classificarScore(scoreTotal, redFlagDetected);
        return new ResultadoTriagemResponse(
                classificacao,
                scoreTotal,
                redFlagDetected,
                recomendacaoPara(classificacao),
                chipsPara(classificacao)
        );
    }

    private ResultadoResposta pontuar(Pergunta pergunta, String valor) {
        if (pergunta.getTipo() == TipoPergunta.OPTIONS) {
            OpcaoPergunta opcao = pergunta.getOpcoes().stream()
                    .filter(item -> item.getCodigo().equals(valor))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Opção inválida para pergunta: " + pergunta.getCodigo()));
            return new ResultadoResposta(opcao.getScore(), opcao.getRedFlag());
        }

        PesoYesNo peso = pergunta.getPesoYesNo();
        if (peso == null) {
            throw new IllegalStateException("Pergunta YESNO sem pesos: " + pergunta.getCodigo());
        }

        return switch (valor) {
            case "yes" -> new ResultadoResposta(peso.getScoreYes(), peso.getRedFlagOnYes());
            case "no" -> new ResultadoResposta(peso.getScoreNo(), peso.getRedFlagOnNo());
            case "dunno" -> new ResultadoResposta(peso.getScoreDunno(), false);
            default -> throw new IllegalArgumentException("Resposta YESNO inválida: " + valor);
        };
    }

    private Classificacao classificarScore(int score, boolean redFlagDetected) {
        if (redFlagDetected || score >= 6) {
            return Classificacao.HIGH;
        }
        if (score >= 3) {
            return Classificacao.MOD;
        }
        return Classificacao.LOW;
    }

    private String recomendacaoPara(Classificacao classificacao) {
        return switch (classificacao) {
            case LOW -> "Observe a criança em casa e acompanhe a evolução dos sintomas.";
            case MOD -> "Procure orientação de um profissional de saúde nas próximas horas.";
            case HIGH -> "Procure atendimento de urgência imediatamente.";
        };
    }

    private List<String> chipsPara(Classificacao classificacao) {
        return switch (classificacao) {
            case LOW -> List.of("Hidratar", "Observar sinais", "Reavaliar se piorar");
            case MOD -> List.of("Agendar consulta", "Monitorar temperatura", "Evitar automedicação");
            case HIGH -> List.of("Ir ao pronto atendimento", "Não aguardar melhora", "Levar documentos");
        };
    }

    private record ResultadoResposta(Integer score, Boolean redFlag) {
    }
}
