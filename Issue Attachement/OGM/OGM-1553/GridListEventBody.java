package org.hibernate.ogm.datastore.mongodb.test.gridfs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bson.types.ObjectId;
import org.hibernate.ogm.datastore.mongodb.options.GridFSBucket;
import org.hibernate.ogm.datastore.mongodb.type.GridFS;

@Entity
@Table(name = "GridListEventBody")
public class GridListEventBody implements Serializable {

	@Id
	private Long id;

	private String name;

	@GridFSBucket("gridlist")
	GridFS gridlist;
	
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

	public GridFS getGridlist() {
		return gridlist;
	}

	public void setGridlist(GridFS gridlist) {
		this.gridlist = gridlist;
	}
}
