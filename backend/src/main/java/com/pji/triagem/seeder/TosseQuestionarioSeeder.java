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
@Order(3)
public class TosseQuestionarioSeeder implements CommandLineRunner {

    private static final String CODIGO_SINTOMA = "cough";

    private final SintomaRepository sintomaRepository;
    private final PerguntaRepository perguntaRepository;

    public TosseQuestionarioSeeder(SintomaRepository sintomaRepository, PerguntaRepository perguntaRepository) {
        this.sintomaRepository = sintomaRepository;
        this.perguntaRepository = perguntaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Sintoma tosse = sintomaRepository.findByCodigo(CODIGO_SINTOMA)
                .orElseThrow(() -> new IllegalStateException("Sintoma não encontrado para seed: " + CODIGO_SINTOMA));

        if (!perguntaRepository.findBySintomaIdOrderByOrdemAsc(tosse.getId()).isEmpty()) {
            return;
        }

        perguntaRepository.saveAll(questionario(tosse));
    }

    static List<Pergunta> questionario(Sintoma sintoma) {
        return List.of(
                yesNo(sintoma, "cianose", "Os lábios ou dedos ficaram azulados ou arroxeados?", null, 6, 0, 3, true, false, 1),
                yesNo(sintoma, "apneia", "Houve pausa na respiração durante a crise de tosse?", null, 6, 0, 3, true, false, 2),
                options(sintoma, "frequencia", "Como está a tosse?", null, 3,
                        opcao("ocasional", "Ocasional", 1, false, 1),
                        opcao("persistente", "Persistente", 3, false, 2),
                        opcao("crises", "Em crises intensas", 4, false, 3)),
                yesNo(sintoma, "chiado_grave", "A criança está com chiado forte ou esforço para respirar?", null, 6, 0, 3, true, false, 4),
                yesNo(sintoma, "vomita_apos_tosse", "A tosse causa vômitos repetidos?", null, 2, 0, 1, false, false, 5)
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
