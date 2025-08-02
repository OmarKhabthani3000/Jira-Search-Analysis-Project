/**
 * Copyright (c) 2006 TD Securities
 * Created on May 3, 2006
 */
package test;

import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract business entity implementation, providing basic common functionality
 */
@MappedSuperclass
public abstract class PersistentEntity implements Serializable {
	@Id
	private Long id;

	public Long getId() {
		return id;
	}
	public boolean isNew() {
		return id == null;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
