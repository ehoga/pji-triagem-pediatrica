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
@Order(2)
public class FebreQuestionarioSeeder implements CommandLineRunner {

    private static final String CODIGO_SINTOMA = "fever";

    private final SintomaRepository sintomaRepository;
    private final PerguntaRepository perguntaRepository;

    public FebreQuestionarioSeeder(SintomaRepository sintomaRepository, PerguntaRepository perguntaRepository) {
        this.sintomaRepository = sintomaRepository;
        this.perguntaRepository = perguntaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Sintoma febre = sintomaRepository.findByCodigo(CODIGO_SINTOMA)
                .orElseThrow(() -> new IllegalStateException("Sintoma não encontrado para seed: " + CODIGO_SINTOMA));

        if (!perguntaRepository.findBySintomaIdOrderByOrdemAsc(febre.getId()).isEmpty()) {
            return;
        }

        perguntaRepository.saveAll(questionario(febre));
    }

    static List<Pergunta> questionario(Sintoma sintoma) {
        return List.of(
                yesNo(sintoma, "idade_menor_3m", "A criança tem menos de 3 meses?", "Febre nessa idade precisa de avaliação urgente.", 6, 0, 3, true, false, 1),
                options(sintoma, "temperatura", "Qual foi a maior temperatura medida?", null, 2,
                        opcao("ate_38", "Até 38°C", 1, false, 1),
                        opcao("38_39", "Entre 38°C e 39°C", 2, false, 2),
                        opcao("acima_39", "Acima de 39°C", 4, false, 3)),
                yesNo(sintoma, "petequias", "Há manchas roxas ou pontinhos vermelhos que não somem ao apertar?", "Pode ser sinal de gravidade em quadro febril.", 6, 0, 3, true, false, 3),
                yesNo(sintoma, "respiracao_dificil", "A criança está com dificuldade para respirar?", null, 6, 0, 3, true, false, 4),
                yesNo(sintoma, "prostracao", "A criança está muito sonolenta, mole ou difícil de acordar?", null, 6, 0, 3, true, false, 5)
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
