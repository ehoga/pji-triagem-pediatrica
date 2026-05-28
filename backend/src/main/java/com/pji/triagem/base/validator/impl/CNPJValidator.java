package com.pji.triagem.base.validator.impl;


import com.pji.triagem.base.utils.DocumentUtils;
import com.pji.triagem.base.validator.DocumentValidator;

public class CNPJValidator implements DocumentValidator {

    @Override
    public boolean isValid(String document) {
        return DocumentUtils.isValidCNPJ(document);
    }

}
