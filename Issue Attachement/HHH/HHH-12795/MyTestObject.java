package org.hibernate.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.FlushModeType;
import org.hibernate.annotations.NamedQuery;

@Entity
@NamedQuery(flushMode=FlushModeType.MANUAL,name=MyTestObject.NAMED_QUERY,query="Select id from MyTestObject")
public class MyTestObject {
    static final String NAMED_QUERY = "test1";
    @Id
    long id;
}
