package org.hibernate.bugs;


import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class ConvertedEntity {

    @Id
    public long id;

    @Convert ( converter = ConvertedDateConverter.class )
    public ConvertedDate date;

}
