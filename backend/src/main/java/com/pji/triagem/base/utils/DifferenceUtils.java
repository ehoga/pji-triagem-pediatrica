package com.pji.triagem.base.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DifferenceUtils {

    private DifferenceUtils() {
    }

    public static Map<String, String> getChangedFields(Object originalUser, Object updatedUser) {
        Map<String, String> changes = new HashMap<>();

        Field[] fields = originalUser.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Permite o acesso a campos privados
            try {
                Object originalValue = field.get(originalUser);
                Object updatedValue = field.get(updatedUser);
                if(field.getName().equals("updatedAt") || field.getName().equals("createdAt")){
                    continue;
                }

                if (!Objects.equals(originalValue, updatedValue)) {
                    changes.put(field.getName(), originalValue + "::" + updatedValue);
                }
            } catch (IllegalAccessException e) {
                // Tratar exceção de acesso, se necessário
                System.out.println("erro ao tentar acessar o campo " + field.getName());
            }
        }

        return changes;
    }
}
