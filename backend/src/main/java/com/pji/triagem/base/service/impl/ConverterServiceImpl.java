package com.pji.triagem.base.service.impl;

import com.pji.triagem.base.annotation.MapToDTO;
import com.pji.triagem.base.utils.FieldUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScope
public class ConverterServiceImpl {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T convert(Object data, Class<T> type) {
        return modelMapper.map(data, type);
    }


    public <T> List<T> convert(List<?> dataList, Class<T> type) {
        return dataList.stream().map(d -> convert(d, type)).collect(Collectors.toList());
    }

    public <T> Page<T> convert(Page<?> dataList, Class<T> type) {
        return dataList.map(d -> convert(d, type));
    }


    public <T> T refreshReferences(Object data, T target) {
        clearId(target);
        Field[] fields = data.getClass().getDeclaredFields();

        for (Field field : fields) {
            MapToDTO mapToDTO = field.getAnnotation(MapToDTO.class);

            if (mapToDTO != null) {
                field.setAccessible(true);

                try {
                    Object value = field.get(data);
                    FieldUtils.setNestedValue(target, mapToDTO.targetProperty(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Erro ao preencher campo mapeado: " + e.getMessage(), e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return target;
    }

    public void clearId(Object target) {
        try {
            Class<?> targetClass = target.getClass();
            Field idField = targetClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(target, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao limpar o identificador (id): " + e.getMessage(), e);
        }
    }
}
