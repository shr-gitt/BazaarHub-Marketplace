package com.bazaarhub.backend.shared.helper;

import com.bazaarhub.backend.shared.enums.EnumWithId;
import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;

@Converter
public class EnumConverter<T extends Enum<T> & EnumWithId> implements AttributeConverter<T , String>{

    private final Class<T> enumClass;

    protected EnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(T attribute){
        return attribute != null ? attribute.getId() : null;
    }

    @Override
    public T convertToEntityAttribute(String dbData){
        if (dbData.isEmpty() || dbData.isEmpty())
                return null;

        for(T constant: enumClass.getEnumConstants()){
            if( constant.getId().equals(dbData))
                return constant;
        }

        throw new IllegalArgumentException(
                "Unknown enum id " + dbData + " for " + enumClass.getSimpleName()
        );
    }
}
