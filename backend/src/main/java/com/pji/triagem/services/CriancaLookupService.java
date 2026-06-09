package com.pji.triagem.services;

import com.pji.triagem.dtos.CriancaResumoResponse;

public interface CriancaLookupService {

    // Trocar por busca owner-aware quando F01/F02 entregarem JWT e CriancaRepository.
    // O contrato do #42 exige 404 para criança de outro usuário.
    CriancaResumoResponse buscarResumo(Long criancaId);
}
