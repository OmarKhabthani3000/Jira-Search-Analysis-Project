package com.tekview.transview.sensornetwork.persist.converter;

import com.tekview.transview.sensornetwork.model.ConnectionState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * User: Tiger
 * Date: 2017-11-09
 */
@Converter(autoApply = true)
public class ConnectionStateConverter implements AttributeConverter<ConnectionState, Short> {
    @Override
    public Short convertToDatabaseColumn(ConnectionState connectionState) {
        return connectionState.getValue();
    }

    @Override
    public ConnectionState convertToEntityAttribute(Short dbData) {
        return ConnectionState.valueOf(dbData);
    }
}
