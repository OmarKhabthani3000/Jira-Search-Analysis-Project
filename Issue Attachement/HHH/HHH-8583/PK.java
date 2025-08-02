package org.hibernate.bugs.entity2;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PK implements Serializable {
	public String field1;
	public String field2;
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof PK) )
			return false;
		PK o = (PK)obj;
		return field1==null?o.field1==null:field1.equals(o.field1) 
				&&field2==null?o.field2==null:field2.equals(o.field2);
	}
	
	@Override
	public int hashCode() {
		return (""+field1+field2).hashCode();
	}
}
