package labmanager.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name="LINK")
@NamedQueries(
{
    @NamedQuery(name = "Link.findAll", query = "SELECT l FROM Link l ORDER BY l.name"),
    @NamedQuery(name = "Link.findAllWithPayitemCount", query = "SELECT (SELECT COUNT(p) from PayItem p where p.las_id=l.id) as countPayitems, l FROM Link l ORDER BY l.name"),
    @NamedQuery(name = "Link.findAllFilteredWithPayitemCount", query = "SELECT (SELECT p.id from PayItem p where p.las_id=l.id and ROWNUM <=1) as hasPayitems, l FROM Link l LEFT OUTER JOIN FETCH l.behaviour WHERE l.name LIKE :name and l.oldpk LIKE :oldpk and l.description LIKE :description ORDER BY l.name"),
    //@NamedQuery(name = "Link.findAllFilteredWithPayitemCount", query = "SELECT (SELECT COUNT(p) from PayItem p where p.las_id=l.id) as countPayitems, l FROM Link l WHERE l.name LIKE :name and l.oldpk LIKE :oldpk and l.description LIKE :description ORDER BY l.name"),
    //@NamedQuery(name = "Link.findAllFilteredWithPayitemCount", query = "SELECT (SELECT COUNT(l) from Link l where l.name='XXX_dummy_XXX'), l FROM Link l WHERE l.name LIKE :name and l.oldpk LIKE :oldpk and l.description LIKE :description ORDER BY l.name"),
    @NamedQuery(name = "Link.findAllFree", query = "SELECT l FROM Link l WHERE l.behaviour_id=null ORDER BY l.name"),
    @NamedQuery(name = "Link.count", query = "SELECT COUNT(l) FROM Link l"),
    @NamedQuery(name = "Link.countWithFilter", query = "SELECT COUNT(l) FROM Link l WHERE l.name LIKE :name and l.oldpk LIKE :oldpk AND l.description LIKE :description"),
    @NamedQuery(name = "Link.freeCount", query = "SELECT COUNT(l) FROM Link l where l.behaviour_id=null"),
    @NamedQuery(name = "Link.findLinkByID", query = "SELECT l FROM Link l WHERE l.id=:id"),
})


public class Link implements Serializable {

	private static final long serialVersionUID = 19830910L;
	
	@Id
	private byte[] id;
	
	private byte[] historicId;
	
	@Temporal(TemporalType.DATE)
	private Date validFrom;
	
	@Temporal(TemporalType.DATE)
	private Date validTo;

	/*@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "link")
	private Set<LinkProp> dynProps;*/
	
	@Column(length = 2)
	private String countryCode;
	
	@Transient
	private byte[] countryCodeEnc;
	
	@Column(length = 254)
	private String description;
	
	@Transient
	private byte[] descriptionEnc;
	
	@Column(length = 254)
	private String kind;
	
	@Transient
	private byte[] kindEnc;
	
	@Column(length = 5)
	private String name;
	
	@Transient
	private byte[] nameEnc;
	
	@Column(length = 254)
	private String oldpk;
	
	@Column(length = 254)
	private String oldtable;
	
	@Column(length = 100)
	private String accountClass;
	
	@Transient
	private byte[] accountClassEnc;
	
	@Column(length = 40)
	private String company_id;
	
	@Column(length = 40,insertable=false, updatable=false)
	private String behaviour_id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Behaviour behaviour;
	
	/*@Transient
	private Long countPayitems;*/
	
	@Transient
	private boolean hasPayitems;



	public Link() {
		id = HexUtil.randomUUIDAsBytes();
		historicId = id;
	}

	public String getId() {
		return HexUtil.bytes2Hex(this.id);
	}

	public void setId(String id) {
		this.id = HexUtil.hex2Bytes(id);
	}

	public String getHistoricId() {
		return HexUtil.bytes2Hex(this.historicId);
	}

	public void setHistoricId(String value) {
		this.historicId = HexUtil.hex2Bytes(value);
	}

	public Date getValidFrom() {
		if (this.validFrom == null)
			return null;
		return new Date(this.validFrom.getTime());
	}

	public void setValidFrom(Date value) {
		if (value == null)
			this.validFrom = null;
		else
			this.validFrom = new Date(value.getTime());
	}

	public Date getValidTo() {
		if (this.validTo == null)
			return null;
		return new Date(this.validTo.getTime());
	}

	public void setValidTo(Date value) {
		if (value == null)
			this.validTo = null;
		else
			this.validTo = new Date(value.getTime());
	}
	
	@XmlTransient
	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
	
	@XmlElement(nillable = true)
	public Behaviour getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
	}
	
	/*@XmlTransient
	public Long getCountPayitems() {
		return countPayitems;
	}

	public void setCountPayitems(Long countPayitems) {
		this.countPayitems = countPayitems;
	}*/

	@XmlTransient
	public boolean isHasPayitems() {
		return hasPayitems;
	}

	public void setHasPayitems(boolean hasPayitems) {
		this.hasPayitems = hasPayitems;
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

	public String getKind() {
		if (this.kind == null && this.kindEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.kind = cipher.decrypt(String.class, kindEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.kind;
	}

	public void setKind(String value) {
		this.kind = value;
		this.kindEnc = null;
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

	@XmlElement(nillable = true)
	public String getOldtable() {
		return this.oldtable;
	}

	public void setOldtable(String value) {
		this.oldtable = value;

	}
	
	public String getAccountClass() {
		if (this.accountClass == null && this.accountClassEnc != null) {
			Cipher cipher = CipherFactory.getCipher();
			this.accountClass = cipher.decrypt(String.class, accountClassEnc);
			CipherFactory.returnCipher(cipher);
		}
		return this.accountClass;
	}

	public void setAccountClass(String value) {
		this.accountClass = value;
		this.accountClassEnc = null;
	}

	@XmlTransient
	public String getBehaviour_id() {
		return behaviour_id;
	}

	public void setBehaviour_id(String behaviour_id) {
		this.behaviour_id = behaviour_id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Link)) {
			return false;
		}
		Link other = (Link) object;
		if ((this.id == null) || (!this.id.equals(other.id)))
			return false;
		return true;
	}

	/**
	 * JPA callback on PrePersist and PreUpdate, activated in orm.xml if encryption is turned on.
	 */
	private void encrypt() {
		Cipher cipher = CipherFactory.getCipher();
		if (CryptoUtils.isSecured(Link.class, "accountClass")) {
			if (this.accountClassEnc == null && this.accountClass != null) {
				this.accountClassEnc = cipher.encrypt(this.accountClass);
			}
			this.accountClass = null;
		}
		if (CryptoUtils.isSecured(Link.class, "countryCode")) {
			if (this.countryCodeEnc == null && this.countryCode != null) {
				this.countryCodeEnc = cipher.encrypt(this.countryCode);
			}
			this.countryCode = null;
		}
		if (CryptoUtils.isSecured(Link.class, "description")) {
			if (this.descriptionEnc == null && this.description != null) {
				this.descriptionEnc = cipher.encrypt(this.description);
			}
			this.description = null;
		}
		if (CryptoUtils.isSecured(Link.class, "kind")) {
			if (this.kindEnc == null && this.kind != null) {
				this.kindEnc = cipher.encrypt(this.kind);
			}
			this.kind = null;
		}
		if (CryptoUtils.isSecured(Link.class, "name")) {
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
		this.accountClass = null;
		this.countryCode = null;
		this.description = null;
		this.kind = null;
		this.name = null;
	}

	@Override
	public String toString() {
		return "piag.Link[id=" + id + "]";
	}

}