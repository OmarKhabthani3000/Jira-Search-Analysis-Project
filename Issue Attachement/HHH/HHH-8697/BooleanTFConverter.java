package com.mycompany.tools.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanTFConverter implements AttributeConverter<Boolean, String>{
	
    @Override
    public String convertToDatabaseColumn(Boolean value) {
        if (value) {
            return "T";
        } else {
            return "F";
        }
    }
    @Override
    public Boolean convertToEntityAttribute(String value) {
        return "T".equals(value);
    }
}
