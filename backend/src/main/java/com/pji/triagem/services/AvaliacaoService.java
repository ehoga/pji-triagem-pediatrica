package com.pji.triagem.services;

import com.pji.triagem.dtos.AvaliacaoRequest;
import com.pji.triagem.dtos.AvaliacaoResponse;
import com.pji.triagem.dtos.CriancaResumoResponse;
import com.pji.triagem.dtos.ResultadoTriagemResponse;
import com.pji.triagem.dtos.SintomaResumoResponse;
import com.pji.triagem.models.Avaliacao;
import com.pji.triagem.models.RespostaAvaliacao;
import com.pji.triagem.models.Sintoma;
import com.pji.triagem.repositories.AvaliacaoRepository;
import com.pji.triagem.repositories.SintomaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final SintomaRepository sintomaRepository;
    private final TriagemService triagemService;
    private final CriancaLookupService criancaLookupService;

    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            SintomaRepository sintomaRepository,
            TriagemService triagemService,
            CriancaLookupService criancaLookupService
    ) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.sintomaRepository = sintomaRepository;
        this.triagemService = triagemService;
        this.criancaLookupService = criancaLookupService;
    }

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest request) {
        CriancaResumoResponse crianca = criancaLookupService.buscarResumo(request.criancaId());
        Sintoma sintoma = sintomaRepository.findById(request.sintomaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sintoma não encontrado"));

        ResultadoTriagemResponse resultado = triagemService.classificar(request.sintomaId(), request.respostas());
        Avaliacao avaliacao = new Avaliacao(
                request.criancaId(),
                sintoma,
                resultado.classificacao(),
                resultado.score(),
                resultado.redFlagDetected(),
                request.respostas().stream()
                        .map(resposta -> new RespostaAvaliacao(resposta.perguntaCodigo(), resposta.valor()))
                        .toList()
        );

        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        return new AvaliacaoResponse(
                salva.getId(),
                resultado.classificacao(),
                resultado.score(),
                resultado.redFlagDetected(),
                resultado.recomendacao(),
                resultado.chips(),
                salva.getCriadoEm(),
                crianca,
                SintomaResumoResponse.from(sintoma)
        );
    }
}
