package com.pji.triagem.repositories;

import com.pji.triagem.models.Sintoma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SintomaRepository extends JpaRepository<Sintoma, Long> {
}
