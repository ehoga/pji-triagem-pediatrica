package com.pji.triagem.repositories;

import com.pji.triagem.models.Pergunta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {

    @EntityGraph(attributePaths = {"opcoes", "pesoYesNo"})
    List<Pergunta> findBySintomaIdOrderByOrdemAsc(Long sintomaId);

    @EntityGraph(attributePaths = {"opcoes", "pesoYesNo"})
    Optional<Pergunta> findBySintomaIdAndCodigo(Long sintomaId, String codigo);
}
