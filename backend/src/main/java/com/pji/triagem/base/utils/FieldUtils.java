package com.pji.triagem.base.utils;

import com.pji.triagem.exception.InformationNotFoundException;
import org.apache.commons.lang3.ArrayUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldUtils {

    public static void setNestedValue(Object target, String propertyName, Object value) throws IllegalAccessException, InstantiationException {
        String[] nestedProperties = propertyName.split("\\.");

        Object currentObject = target;

        for (int i = 0; i < nestedProperties.length - 1; i++) {
            String nestedProperty = nestedProperties[i];
            Field nestedField = getDeclaredField(currentObject.getClass(), nestedProperty);
            nestedField.setAccessible(true);

            Object nestedObject = nestedField.get(currentObject);

            if (nestedObject == null) {
                nestedObject = instantiateField(nestedField);
                nestedField.set(currentObject, nestedObject);
            }

            currentObject = nestedObject;
        }

        Field finalField = getDeclaredField(currentObject.getClass(), nestedProperties[nestedProperties.length - 1]);
        finalField.setAccessible(true);
        finalField.set(currentObject, value);
    }

    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Campo não encontrado: " + fieldName, e);
        }
    }

    public static Object instantiateField(Field field) throws IllegalAccessException, InstantiationException {
        Class<?> fieldType = field.getType();
        if (List.class.isAssignableFrom(fieldType)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(fieldType)) {
            return new HashSet<>();
        } else {
            return fieldType.newInstance();
        }
    }

    public static void setValue(Object data, Object value, String fieldName) {
        try {
            Field[] fields = data.getClass().getDeclaredFields();
            if (null != data.getClass().getSuperclass())
                fields = (Field[]) ArrayUtils.addAll(fields, data.getClass().getSuperclass().getDeclaredFields());
            for (int i = 0; i < fields.length; i++) {
                if (fieldName.equals(fields[i].getName())) {
                    fields[i].setAccessible(true);
                    fields[i].set(data, value);
                }
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new InformationNotFoundException(fieldName);
        }
    }
}
