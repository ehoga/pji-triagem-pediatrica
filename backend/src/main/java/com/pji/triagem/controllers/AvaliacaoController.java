package com.pji.triagem.controllers;

import com.pji.triagem.dtos.AvaliacaoRequest;
import com.pji.triagem.dtos.AvaliacaoResponse;
import com.pji.triagem.services.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações", description = "Processamento e persistência de triagens")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cria avaliação",
            description = "Processa as respostas do questionário, persiste o resultado e retorna a classificação.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public AvaliacaoResponse criar(@Valid @RequestBody AvaliacaoRequest request) {
        return avaliacaoService.criar(request);
    }
}
