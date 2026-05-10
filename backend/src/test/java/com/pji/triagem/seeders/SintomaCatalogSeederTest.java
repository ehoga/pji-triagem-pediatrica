package com.pji.triagem.seeders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pji.triagem.models.Sintoma;
import com.pji.triagem.repositories.SintomaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class SintomaCatalogSeederTest {

    @Test
    void catalogoInicialContemOsNoveSintomasDoDesign() {
        List<Sintoma> sintomas = SintomaCatalogSeeder.catalogoInicial();

        assertThat(sintomas)
                .hasSize(9)
                .extracting(Sintoma::getCodigo)
                .containsExactly("fever", "cough", "vomit", "diarrhea", "belly", "breath", "rash", "trauma", "ear");

        assertThat(sintomas)
                .allSatisfy(sintoma -> {
                    assertThat(sintoma.getNome()).isNotBlank();
                    assertThat(sintoma.getDescricaoCurta()).isNotBlank();
                    assertThat(sintoma.getIconeRef()).isNotBlank();
                    assertThat(sintoma.getCorHex()).matches("^#[0-9A-Fa-f]{6}$");
                });
    }

    @Test
    void populaCatalogoQuandoTabelaEstaVazia() {
        SintomaRepository repository = org.mockito.Mockito.mock(SintomaRepository.class);
        when(repository.count()).thenReturn(0L);

        new SintomaCatalogSeeder(repository).run();

        verify(repository).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void naoDuplicaCatalogoQuandoJaExistemSintomas() {
        SintomaRepository repository = org.mockito.Mockito.mock(SintomaRepository.class);
        when(repository.count()).thenReturn(1L);

        new SintomaCatalogSeeder(repository).run();

        verify(repository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }
}
