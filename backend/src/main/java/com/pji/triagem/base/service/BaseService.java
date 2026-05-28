package com.pji.triagem.base.service;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T> {

    T find(Long id);

    Page<T> findAll(Pageable pageable);

    T save(T entidade);

    T update(Long id, T entity);

    Page<T> findAllWithPredicate(Predicate predicate, Pageable pageable);
}
