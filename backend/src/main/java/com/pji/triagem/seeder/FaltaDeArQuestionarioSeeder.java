package com.pji.triagem.seeder;

import com.pji.triagem.model.OpcaoPergunta;
import com.pji.triagem.model.Pergunta;
import com.pji.triagem.model.PesoYesNo;
import com.pji.triagem.model.Sintoma;
import com.pji.triagem.model.TipoPergunta;
import com.pji.triagem.repository.PerguntaRepository;
import com.pji.triagem.repository.SintomaRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(4)
public class FaltaDeArQuestionarioSeeder implements CommandLineRunner {

    private static final String CODIGO_SINTOMA = "breath";

    private final SintomaRepository sintomaRepository;
    private final PerguntaRepository perguntaRepository;

    public FaltaDeArQuestionarioSeeder(SintomaRepository sintomaRepository, PerguntaRepository perguntaRepository) {
        this.sintomaRepository = sintomaRepository;
        this.perguntaRepository = perguntaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Sintoma faltaDeAr = sintomaRepository.findByCodigo(CODIGO_SINTOMA)
                .orElseThrow(() -> new IllegalStateException("Sintoma não encontrado para seed: " + CODIGO_SINTOMA));

        if (!perguntaRepository.findBySintomaIdOrderByOrdemAsc(faltaDeAr.getId()).isEmpty()) {
            return;
        }

        perguntaRepository.saveAll(questionario(faltaDeAr));
    }

    static List<Pergunta> questionario(Sintoma sintoma) {
        return List.of(
                yesNo(sintoma, "cianose", "Os lábios ou dedos estão azulados ou arroxeados?", null, 6, 0, 3, true, false, 1),
                yesNo(sintoma, "tiragem_grave", "A costela ou o pescoço afundam ao respirar?", "Sinal de esforço respiratório.", 6, 0, 3, true, false, 2),
                options(sintoma, "fala_alimenta", "A criança consegue falar, mamar ou beber?", null, 3,
                        opcao("normal", "Consegue normalmente", 0, false, 1),
                        opcao("pouco", "Consegue pouco", 3, false, 2),
                        opcao("nao_consegue", "Não consegue", 6, true, 3)),
                options(sintoma, "respiracao", "Como está o ritmo da respiração?", null, 4,
                        opcao("normal", "Normal", 0, false, 1),
                        opcao("rapida", "Rápida", 3, false, 2),
                        opcao("muito_rapida", "Muito rápida", 6, true, 3)),
                yesNo(sintoma, "sonolencia", "A criança está confusa, muito sonolenta ou irritada?", null, 6, 0, 3, true, false, 5)
        );
    }

    private static Pergunta yesNo(
            Sintoma sintoma,
            String codigo,
            String texto,
            String sub,
            int scoreYes,
            int scoreNo,
            int scoreDunno,
            boolean redFlagOnYes,
            boolean redFlagOnNo,
            int ordem
    ) {
        Pergunta pergunta = new Pergunta(sintoma, codigo, texto, sub, TipoPergunta.YESNO, ordem);
        pergunta.definirPesoYesNo(new PesoYesNo(scoreYes, scoreNo, scoreDunno, redFlagOnYes, redFlagOnNo));
        return pergunta;
    }

    private static Pergunta options(Sintoma sintoma, String codigo, String texto, String sub, int ordem, OpcaoPergunta... opcoes) {
        Pergunta pergunta = new Pergunta(sintoma, codigo, texto, sub, TipoPergunta.OPTIONS, ordem);
        for (OpcaoPergunta opcao : opcoes) {
            pergunta.adicionarOpcao(opcao);
        }
        return pergunta;
    }

    private static OpcaoPergunta opcao(String codigo, String texto, int score, boolean redFlag, int ordem) {
        return new OpcaoPergunta(codigo, texto, score, redFlag, ordem);
    }
}
