package com.pji.triagem.controller;

import com.pji.triagem.dto.response.PerguntaResponse;
import com.pji.triagem.service.PerguntaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sintomas")
@Tag(name = "Sintomas", description = "Catálogo de sintomas disponíveis para triagem")
public class PerguntaController {

    private final PerguntaService perguntaService;

    public PerguntaController(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @GetMapping("/{id}/perguntas")
    @Operation(
            summary = "Lista perguntas de um sintoma",
            description = "Retorna o questionário para renderização no app sem expor scores ou red flags.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<PerguntaResponse> listarPerguntas(@PathVariable Long id) {
        return perguntaService.listarPorSintoma(id);
    }
}
