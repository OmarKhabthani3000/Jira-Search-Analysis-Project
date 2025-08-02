package org.hibernate.ogm.datastore.mongodb.test.gridfs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BigEvent")
public class GridListEvent {

	@Id
	public Long id;

	String name;

	@OneToOne
	private GridListEventBody ogmEventBody;

	
	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public GridListEventBody getOgmEventBody() {
		return ogmEventBody;
	}

	public void setOgmEventBody(GridListEventBody ogmEventBody) {
		this.ogmEventBody = ogmEventBody;
	}
}
