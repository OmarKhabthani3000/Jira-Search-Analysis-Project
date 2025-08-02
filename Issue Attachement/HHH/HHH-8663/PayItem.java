package labmanager.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import piag.HexUtil;

@Entity
@Table(name="PAYITEM")
public class PayItem implements Serializable {

	private static final long serialVersionUID = 19830910L;
	
	@Id
	private byte[] id;
	
	private byte[] las_id;

	
	public PayItem() {
		id = HexUtil.randomUUIDAsBytes();
	}

	public String getId() {
		return HexUtil.bytes2Hex(this.id);
	}

	public String getLas_id() {
		return HexUtil.bytes2Hex(las_id);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PayItem)) {
			return false;
		}
		PayItem other = (PayItem) object;
		if ((this.id == null) || (!this.id.equals(other.id)))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "piag.PayItem[id=" + id + "]";
	}

}
