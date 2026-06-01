package com.pji.triagem.seeder;

import com.pji.triagem.model.Sintoma;
import com.pji.triagem.repository.SintomaRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SintomaCatalogSeeder implements CommandLineRunner {

    private final SintomaRepository sintomaRepository;

    public SintomaCatalogSeeder(SintomaRepository sintomaRepository) {
        this.sintomaRepository = sintomaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (sintomaRepository.count() > 0) {
            return;
        }

        sintomaRepository.saveAll(catalogoInicial());
    }

    static List<Sintoma> catalogoInicial() {
        return List.of(
                new Sintoma("fever", "Febre", "Temperatura acima de 37.8°C", "thermo", "#F59E5C", 1),
                new Sintoma("cough", "Tosse", "Seca, com catarro ou persistente", "cough", "#56CCF2", 2),
                new Sintoma("vomit", "Vômitos", "Náuseas ou episódios de vômito", "vomit", "#9B7BE0", 3),
                new Sintoma("diarrhea", "Diarreia", "Fezes líquidas ou frequentes", "drop", "#56CCF2", 4),
                new Sintoma("belly", "Dor abdominal", "Dor ou desconforto na barriga", "belly", "#F2C94C", 5),
                new Sintoma("breath", "Falta de ar", "Respiração rápida ou difícil", "lung", "#EB5757", 6),
                new Sintoma("rash", "Manchas na pele", "Vermelhidão, pintas ou erupção", "rash", "#E18ABF", 7),
                new Sintoma("trauma", "Trauma leve", "Quedas, batidas ou cortes pequenos", "bandage", "#7BC393", 8),
                new Sintoma("ear", "Dor de ouvido", "Dor, coceira ou secreção", "ear", "#56CCF2", 9)
        );
    }
}
