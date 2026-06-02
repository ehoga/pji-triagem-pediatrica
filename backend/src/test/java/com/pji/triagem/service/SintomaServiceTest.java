package com.pji.triagem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.pji.triagem.dto.response.SintomaResponse;
import com.pji.triagem.model.Sintoma;
import com.pji.triagem.repository.SintomaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class SintomaServiceTest {

    @Test
    void listaSintomasOrdenadosComoDto() {
        SintomaRepository repository = org.mockito.Mockito.mock(SintomaRepository.class);
        when(repository.findAllByOrderByOrdemAsc()).thenReturn(List.of(
                new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1),
                new Sintoma("cough", "Tosse", "Seca, com catarro ou persistente", "cough", "#56CCF2", 2)
        ));

        List<SintomaResponse> response = new SintomaService(repository).listar();

        assertThat(response)
                .extracting(SintomaResponse::codigo)
                .containsExactly("fever", "cough");
        assertThat(response.getFirst().nome()).isEqualTo("Febre");
        assertThat(response.getFirst().descricaoCurta()).isEqualTo("Temperatura acima de 37.8°C");
        assertThat(response.getFirst().iconeRef()).isEqualTo("thermo");
        assertThat(response.getFirst().corHex()).isEqualTo("#F59E5C");
    }
}
