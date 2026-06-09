package com.pji.triagem.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pji.triagem.dtos.AvaliacaoRequest;
import com.pji.triagem.dtos.CriancaResumoResponse;
import com.pji.triagem.dtos.RespostaTriagemRequest;
import com.pji.triagem.dtos.ResultadoTriagemResponse;
import com.pji.triagem.enums.Classificacao;
import com.pji.triagem.models.Avaliacao;
import com.pji.triagem.models.Sintoma;
import com.pji.triagem.repositories.AvaliacaoRepository;
import com.pji.triagem.repositories.SintomaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AvaliacaoServiceTest {

    @Test
    void criaAvaliacaoUsandoContratoDeLookupDaCrianca() {
        AvaliacaoRepository avaliacaoRepository = org.mockito.Mockito.mock(AvaliacaoRepository.class);
        SintomaRepository sintomaRepository = org.mockito.Mockito.mock(SintomaRepository.class);
        TriagemService triagemService = org.mockito.Mockito.mock(TriagemService.class);
        CriancaLookupService criancaLookupService = org.mockito.Mockito.mock(CriancaLookupService.class);

        Sintoma sintoma = new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1);
        AvaliacaoRequest request = new AvaliacaoRequest(
                10L,
                20L,
                List.of(new RespostaTriagemRequest("temperatura", "38_39"))
        );

        when(criancaLookupService.buscarResumo(10L)).thenReturn(new CriancaResumoResponse(10L, "Maria", "flor"));
        when(sintomaRepository.findById(20L)).thenReturn(Optional.of(sintoma));
        when(triagemService.classificar(20L, request.respostas())).thenReturn(new ResultadoTriagemResponse(
                Classificacao.MOD,
                4,
                false,
                "Procure orientação de um profissional de saúde nas próximas horas.",
                List.of("Agendar consulta", "Monitorar temperatura", "Evitar automedicação")
        ));
        when(avaliacaoRepository.save(org.mockito.ArgumentMatchers.any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = new AvaliacaoService(
                avaliacaoRepository,
                sintomaRepository,
                triagemService,
                criancaLookupService
        ).criar(request);

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(avaliacaoRepository).save(captor.capture());
        assertThat(captor.getValue().getCriancaId()).isEqualTo(10L);
        assertThat(captor.getValue().getClassificacao()).isEqualTo(Classificacao.MOD);
        assertThat(response.crianca().nome()).isEqualTo("Maria");
        assertThat(response.sintoma().codigo()).isEqualTo("fever");
    }
}
