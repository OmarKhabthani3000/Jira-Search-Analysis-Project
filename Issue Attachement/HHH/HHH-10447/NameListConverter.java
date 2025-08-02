/* Copyright (c) 1994 - 2016 by OneVision Software AG, Regensburg, Germany
 * All rights reserved, strictly confidential
 */
package test;

import java.io.IOException;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("rawtypes")
@Converter
public class NameListConverter implements AttributeConverter<List,String> {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List data) {

        try {
            String json=mapper.writeValueAsString(data);
            System.out.println("Serialize to: "+json);
            return json;
        } catch (JsonProcessingException e) {
            throw new PersistenceException(e);
        }
	}

	@Override
	public List convertToEntityAttribute(String data) {
        try {
        	System.out.println("DeSerialize from: " + data);
        	JavaType type=mapper.getTypeFactory().constructCollectionType(List.class,NameElement.class);
            List<NameElement> value= mapper.readValue(data,type);
            return value;
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
	}

}
