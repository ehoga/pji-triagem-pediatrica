package com.pji.triagem.service;

import com.pji.triagem.dto.response.SintomaResponse;
import com.pji.triagem.repository.SintomaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SintomaService {

    private final SintomaRepository sintomaRepository;

    public SintomaService(SintomaRepository sintomaRepository) {
        this.sintomaRepository = sintomaRepository;
    }

    @Transactional(readOnly = true)
    public List<SintomaResponse> listar() {
        return sintomaRepository.findAllByOrderByOrdemAsc()
                .stream()
                .map(SintomaResponse::from)
                .toList();
    }
}
