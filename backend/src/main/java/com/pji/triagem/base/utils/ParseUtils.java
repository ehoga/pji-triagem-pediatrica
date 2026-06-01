package com.pji.triagem.base.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

public class ParseUtils {

    private ParseUtils() {
    }

    public static String[] getBlankPropertyNames (Object source)
    {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds)
        {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null)
            {
                boolean isInvalid = false;

                try
                {
                    isInvalid = StringUtils.trim((String) srcValue).isEmpty();
                }
                catch (Exception e)
                {
                    //
                }

                if (isInvalid)
                {
                    emptyNames.add(pd.getName());
                }
            }
            else
            {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
