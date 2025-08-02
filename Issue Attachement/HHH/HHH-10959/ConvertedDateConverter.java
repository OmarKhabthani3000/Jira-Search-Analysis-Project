/**
 * © 2016 AgNO3 Gmbh & Co. KG
 * All right reserved.
 * 
 * Created: Jul 18, 2016 by mbechler
 */
package org.hibernate.bugs;


import java.util.Date;

import javax.persistence.AttributeConverter;


/**
 * @author mbechler
 *
 */
public class ConvertedDateConverter implements AttributeConverter<ConvertedDate, Date> {

    /**
     * {@inheritDoc}
     *
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public Date convertToDatabaseColumn ( ConvertedDate attribute ) {
        if ( attribute == null ) {
            return null;
        }
        return new Date(attribute.timestamp);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public ConvertedDate convertToEntityAttribute ( Date dbData ) {
        if ( dbData == null ) {
            return null;
        }
        ConvertedDate d = new ConvertedDate();
        d.timestamp = dbData.getTime();
        return d;
    }

}
