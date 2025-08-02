package test.hh9814;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class TestEntity {

	@EmbeddedId
	private UID uid;

	public UID getUid() {
		return uid;
	}

	public void setUid(UID __value) {
		uid = __value;
	}


	@Column(name="NAME",length=100)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String __value) {
		this.name = __value;
	}

}
