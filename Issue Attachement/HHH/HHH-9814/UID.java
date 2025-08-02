package test.hh9814;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UID implements java.io.Serializable {


	public UID() {}

	@Column(name="A_ID",length=8)
	private String aId;
	
	public String getAId() {
		return aId;
	}
	
	public void setAId(String __value) {
		this.aId = __value;
	}
	
	
	@Column(name="B_ID",length=56)
	private String bId;
	
	public String getBId() {
		return bId;
	}
	
	public void setBId(String __value) {
		this.bId = __value;
	}
	
	public UID(String aId, String bId) {
		super();
		this.aId = aId;
		this.bId = bId;
	}
	
	public int hashCode() {
		int c = aId.hashCode();
		return c * 37 + bId.hashCode();
	}
	
	public boolean equals(Object o) {
		return o == this ||
				(o instanceof UID && ((UID)o).aId.equals(aId) && ((UID)o).bId.equals(bId));
	}
	
}
