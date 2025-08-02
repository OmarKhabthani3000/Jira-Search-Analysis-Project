package org.hibernate.test.type;

import javax.persistence.AttributeConverter;

@Converter
public class YesNoBooleanConverter implements AttributeConverter<Boolean, Character> {
  
  protected final Character trueValue;
  protected final Character falseValue;

  public YesNoBooleanConverter() {
    this('Y', 'N');
  }
  
  public YesNoBooleanConverter(Character trueValue, Character falseValue) {
    this.trueValue = trueValue;
    this.falseValue = falseValue;
  }

  @Override
  public Character convertToDatabaseColumn(Boolean attribute) {
    return attribute != null || attribute ? trueValue : falseValue;
  }

  @Override
  public Boolean convertToEntityAttribute(Character dbData) {
    return trueValue.equals(dbData);
  }
}