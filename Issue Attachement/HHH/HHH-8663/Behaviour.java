package labmanager.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import piag.HexUtil;
import piag.crypto.Cipher;
import piag.crypto.CipherFactory;
import piag.crypto.CryptoUtils;

@Entity
@XmlRootElement
@Table(name = "BEHAVIOUR")
@NamedQueries(
{
    @NamedQuery(name = "Behaviour.findAll", query = "SELECT b FROM Behaviour b WHERE b.countryCode='DE' ORDER BY b.name"),
    @NamedQuery(name = "Behaviour.findAllWithLinkCount", query = "SELECT (SELECT COUNT(l) from Link l where l.behaviour_id=b.id) as countLinks, b FROM Behaviour b WHERE b.countryCode='DE' ORDER BY b.name"),
    @NamedQuery(name = "Behaviour.findAllFilteredWithLinkCount", query = "SELECT (SELECT COUNT(l) from Link l where l.behaviour_id=b.id) as countLinks, b FROM Behaviour b WHERE b.countryCode='DE' AND b.name LIKE :name and b.oldpk LIKE :oldpk and b.description LIKE :description ORDER BY b.name"),
    @NamedQuery(name = "Behaviour.count", query = "SELECT COUNT(b) FROM Behaviour b WHERE b.countryCode='DE'"),
    @NamedQuery(name = "Behaviour.countWithFilter", query = "SELECT COUNT(b) FROM Behaviour b WHERE b.countryCode='DE' AND b.name LIKE :name AND b.oldpk LIKE :oldpk AND b.description LIKE :description"),
    @NamedQuery(name = "Behaviour.findBehaviourByID", query = "SELECT b FROM Behaviour b WHERE b.countryCode='DE' AND b.id=:id"),
    @NamedQuery(name = "Behaviour.linksCountofBehaviour", query = "SELECT count(l) FROM Link l WHERE l.behaviour_id=:behaviour_id"),
})
public class Behaviour implements Serializable {

	private static final long serialVersionUID = 19830910L;
	
	@Id
	private byte[] id;

	private Integer classification;
	
	@Transient
	private byte[] classificationEnc;
	
	@Column(length = 2)
	private String countryCode;
	
	@Transient
	private byte[] countryCodeEnc;
	
	@Column(length = 200)
	private String description;
	
	@Transient
	private byte[] descriptionEnc;
	
	@Column(length = 40)
	private String entity;
	
	@Transient
	private byte[] entityEnc;
	
	@Column(length = 50)
	private String name;
	
	@Transient
	private byte[] nameEnc;
	
	@Column(length = 254)
	private String oldpk;
	
	@Column(length = 254)
	private String oldtable;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="behaviour_id")
	@OrderBy("name ASC")
	private List<Link> links;
	
	@Transient
	private Long countLinks;
	
	
	public Behaviour() {
		id = HexUtil.randomUUIDAsBytes();
	}

	public String getId() {
		return HexUtil.bytes2Hex(this.id);
	}

	public void setId(String id) {
		this.id = HexUtil.hex2Bytes(id);
	}

	public Integer getClassification() {
		if (this.classification == null && this.classificationEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.classification = cipher.decrypt(Integer.class,
					classificationEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.classification;
	}

	public void setClassification(Integer value) {
		this.classification = value;
		this.classificationEnc = null;
	}

	public String getCountryCode() {
		if (this.countryCode == null && this.countryCodeEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.countryCode = cipher.decrypt(String.class, countryCodeEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.countryCode;
	}

	public void setCountryCode(String value) {
		this.countryCode = value;
		this.countryCodeEnc = null;
	}

	public String getDescription() {
		if (this.description == null && this.descriptionEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.description = cipher.decrypt(String.class, descriptionEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.description;
	}

	public void setDescription(String value) {
		this.description = value;
		this.descriptionEnc = null;
	}

	public String getEntity() {
		if (this.entity == null && this.entityEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.entity = cipher.decrypt(String.class, entityEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.entity;
	}

	public void setEntity(String value) {
		this.entity = value;
		this.entityEnc = null;
	}

	@XmlElement(nillable = true)
	public String getName() {
		if (this.name == null && this.nameEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.name = cipher.decrypt(String.class, nameEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
		this.nameEnc = null;
	}

	public String getOldpk() {
		return this.oldpk;
	}

	public void setOldpk(String value) {
		this.oldpk = value;

	}

	public String getOldtable() {
		return this.oldtable;
	}

	public void setOldtable(String value) {
		this.oldtable = value;

	}
	
	@XmlTransient
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	@XmlTransient
	public Long getCountLinks() {
		return countLinks;
	}

	public void setCountLinks(Long countLinks) {
		this.countLinks = countLinks;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Behaviour)) {
			return false;
		}
		Behaviour other = (Behaviour) object;
		if ((this.id == null) || (!this.id.equals(other.id)))
			return false;
		return true;
	}

	/**
	 * JPA callback on PrePersist and PreUpdate, activated in orm.xml if encryption is turned on.
	 */
	private void encrypt() {
		Cipher cipher = CipherFactory.getCipher();
		if (CryptoUtils.isSecured(Behaviour.class, "classification")) {
			if (this.classificationEnc == null && this.classification != null) {
				this.classificationEnc = cipher.encrypt(this.classification);
			}
			this.classification = null;
		}
		if (CryptoUtils.isSecured(Behaviour.class, "countryCode")) {
			if (this.countryCodeEnc == null && this.countryCode != null) {
				this.countryCodeEnc = cipher.encrypt(this.countryCode);
			}
			this.countryCode = null;
		}
		if (CryptoUtils.isSecured(Behaviour.class, "description")) {
			if (this.descriptionEnc == null && this.description != null) {
				this.descriptionEnc = cipher.encrypt(this.description);
			}
			this.description = null;
		}
		if (CryptoUtils.isSecured(Behaviour.class, "entity")) {
			if (this.entityEnc == null && this.entity != null) {
				this.entityEnc = cipher.encrypt(this.entity);
			}
			this.entity = null;
		}
		if (CryptoUtils.isSecured(Behaviour.class, "name")) {
			if (this.nameEnc == null && this.name != null) {
				this.nameEnc = cipher.encrypt(this.name);
			}
			this.name = null;
		}
		CipherFactory.returnCipher(cipher);
	}

	/**
	 * JPA callback on PostLoad, activated in orm.xml if encryption is turned on.
	 */
	private void clear() {
		this.classification = null;
		this.countryCode = null;
		this.description = null;
		this.entity = null;
		this.name = null;
	}

	@Override
	public String toString() {
		return "piag.Behaviour[id=" + id + "]";
	}

}
