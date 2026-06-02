package com.pji.triagem.repository;

import com.pji.triagem.model.Sintoma;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SintomaRepository extends JpaRepository<Sintoma, Long> {

    List<Sintoma> findAllByOrderByOrdemAsc();
}
