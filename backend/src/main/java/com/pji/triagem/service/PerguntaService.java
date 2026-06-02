package com.pji.triagem.service;

import com.pji.triagem.dto.response.PerguntaResponse;
import com.pji.triagem.repository.PerguntaRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerguntaService {

    private final PerguntaRepository perguntaRepository;

    public PerguntaService(PerguntaRepository perguntaRepository) {
        this.perguntaRepository = perguntaRepository;
    }

    @Cacheable("perguntasPorSintoma")
    @Transactional(readOnly = true)
    public List<PerguntaResponse> listarPorSintoma(Long sintomaId) {
        return perguntaRepository.findBySintomaIdOrderByOrdemAsc(sintomaId)
                .stream()
                .map(PerguntaResponse::from)
                .toList();
    }
}
