package org.hibernate.bugs.entity2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Entity1 {
	@EmbeddedId
	public PK id;
	public String data;
}
