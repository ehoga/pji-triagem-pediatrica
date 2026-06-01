package com.pji.triagem.factory;

import com.pji.triagem.base.utils.ReplaceUtils;
import com.pji.triagem.base.validator.DocumentValidator;
import com.pji.triagem.base.validator.impl.CNPJValidator;
import com.pji.triagem.base.validator.impl.CPFValidator;
import com.pji.triagem.exception.ValidationException;

public class DocumentValidatorFactory {


    private DocumentValidatorFactory(){

    }

    public static DocumentValidator getValidator(String document) {
        String cleanDocument = ReplaceUtils.refactoryString(document);
        if (cleanDocument.length() == 11) {
            return new CPFValidator();
        } else if (cleanDocument.length() == 14) {
            return new CNPJValidator();
        } else {
            throw new ValidationException("CPF/CNPJ inválido.");
        }
    }
}
