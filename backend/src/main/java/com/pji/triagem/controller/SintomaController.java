package com.pji.triagem.controller;

import com.pji.triagem.dto.response.SintomaResponse;
import com.pji.triagem.service.SintomaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sintomas")
@Tag(name = "Sintomas", description = "Catálogo de sintomas disponíveis para triagem")
public class SintomaController {

    private final SintomaService sintomaService;

    public SintomaController(SintomaService sintomaService) {
        this.sintomaService = sintomaService;
    }

    @GetMapping
    @Operation(
            summary = "Lista sintomas",
            description = "Retorna o catálogo de sintomas ordenado para exibição no aplicativo.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<SintomaResponse> listar() {
        return sintomaService.listar();
    }
}
