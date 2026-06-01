package com.pji.triagem.base.validator.impl;


import com.pji.triagem.base.utils.DocumentUtils;
import com.pji.triagem.base.validator.DocumentValidator;

public class CPFValidator implements DocumentValidator {

    @Override
    public boolean isValid(String document) {
        return DocumentUtils.isValidCPF(document);
    }

}
