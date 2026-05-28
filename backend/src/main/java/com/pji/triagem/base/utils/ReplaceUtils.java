package com.pji.triagem.base.utils;

public class ReplaceUtils {

    private ReplaceUtils(){}

    /**
     * Remove qualquer caractere especial de uma string, como / - . espaços em branco, etc.
     *
     * @param input A string de entrada que pode conter caracteres especiais.
     * @return A string limpa, sem caracteres especiais.
     */
    public static String refactoryString(String input) {
        if (input == null) {
            return null;
        }
        // Substitui tudo que não for uma letra ou número por vazio
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String removeSpacesEmpty(String input) {
        return input.replaceAll("\\s", "");
    }
}
