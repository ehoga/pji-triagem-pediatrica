package com.pji.triagem.base.utils;

public class DocumentUtils {

    private DocumentUtils() {
    }

    public static boolean isValidCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");  // Remove qualquer caractere que não seja número.

        // Verifica se o CPF tem 11 dígitos.
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais, o que invalida o CPF.
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Cálculo do primeiro dígito verificador.
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }

        // Cálculo do segundo dígito verificador.
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }

        // Verifica se os dígitos calculados são iguais aos dígitos informados no CPF.
        return cpf.charAt(9) - '0' == firstDigit && cpf.charAt(10) - '0' == secondDigit;
    }

    public static boolean isValidCNPJ(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", "");  // Remove qualquer caractere que não seja número.

        // Verifica se o CNPJ tem 14 dígitos.
        if (cnpj.length() != 14) {
            return false;
        }

        // Verifica se todos os dígitos são iguais, o que invalida o CNPJ.
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Cálculo do primeiro dígito verificador.
        int[] weights = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }

        // Cálculo do segundo dígito verificador.
        weights = new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }

        // Verifica se os dígitos calculados são iguais aos dígitos informados no CNPJ.
        return cnpj.charAt(12) - '0' == firstDigit && cnpj.charAt(13) - '0' == secondDigit;
    }
}
