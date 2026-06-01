package com.pji.triagem.base.service.impl;

import com.pji.triagem.base.repository.BaseRepository;
import com.pji.triagem.base.service.BaseService;
import com.pji.triagem.base.utils.ParseUtils;
import com.pji.triagem.exception.InformationNotFoundException;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Service
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    @Transactional(readOnly = true)
    @Override
    public T find(Long id) {
        Optional<T> entidadeOpt = getRepository().findById(id);

        if (entidadeOpt.isEmpty()) {
            throw new InformationNotFoundException();
        }

        return entidadeOpt.get();
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    @Transactional
    public T save(T entidade) {
        return getRepository().save(entidade);
    }

    @Override
    @Transactional
    public T update(Long id, T entity) {
        T savedEntity = find(id);
        String[] nullProperties = ParseUtils.getBlankPropertyNames(entity);
        BeanUtils.copyProperties(entity, savedEntity, nullProperties);
        return save(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAllWithPredicate(Predicate predicate, Pageable pageable) {
        if(predicate == null)
            return findAll(pageable);
        return getRepository().findAll(predicate, pageable);
    }

    protected abstract BaseRepository<T, Long> getRepository();

    @Transactional(
            readOnly = true
    )
    public Page<T> findAll(Predicate predicate, Pageable pageable) {
        if (predicate == null) {
            return getRepository().findAll(pageable);
        }
        return getRepository().findAll(predicate, pageable);
    }

    protected abstract String getResourceName();

}
