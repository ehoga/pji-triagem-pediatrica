package com.pji.triagem.repository;

import com.pji.triagem.model.Sintoma;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SintomaRepository extends JpaRepository<Sintoma, Long> {

    Optional<Sintoma> findByCodigo(String codigo);
    List<Sintoma> findAllByOrderByOrdemAsc();
}
