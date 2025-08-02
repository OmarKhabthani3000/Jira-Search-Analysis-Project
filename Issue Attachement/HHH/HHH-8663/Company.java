package labmanager.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import piag.HexUtil;
import piag.crypto.Cipher;
import piag.crypto.CipherFactory;

@Entity
@Table(name = "COMPANY")
@NamedQueries(
{
    @NamedQuery(name = "Company.findAllForCombo", query = "SELECT c FROM Company c WHERE c.oldpk like '%*%' ORDER BY c.name"),
    @NamedQuery(name = "Company.countOfComboItems", query = "SELECT COUNT(c) FROM Company c WHERE c.oldpk like '%*%'")
})
public class Company implements Serializable {

	private static final long serialVersionUID = 19830910L;
	@Id
	private byte[] id;
	private byte[] historicId;
	@Column(length = 254)
	private String name;
	@Transient
	private byte[] nameEnc;
	@Column(length = 254)
	private String oldpk;

	public Company() {
		id = HexUtil.randomUUIDAsBytes();
		historicId = id;
	}

	public String getId() {
		return HexUtil.bytes2Hex(this.id);
	}

	public String getHistoricId() {
		return HexUtil.bytes2Hex(this.historicId);
	}

	public String getName() {
		if (this.name == null && this.nameEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.name = cipher.decrypt(String.class, nameEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.name;
	}
	
	public String getOldpk() {
		return oldpk;
	}

	public void setOldpk(String oldpk) {
		this.oldpk = oldpk;
	}

}