package dk.tdc.visitation.polcalink.resource.dbResource.polcadb.inca.servingfp;

import java.util.List;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

public class HibernateTestClass {

	/**
	 * @param args
	 */
	
	public static EntityManagerFactory entityManagerFactory;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EntityManager em = null;
		
		List<ChargerEntityExt5> ChargerList = null;

		Query query = null;
		String queryString = "SELECT ce FROM ChargerEntityExt5 ce WHERE ce.chargerId=?1";
		try {

			try {
				em = createEntityManager();
			} catch (Exception e) {
				System.out.println(e);
			}

			query = em.createQuery(queryString);
			query.setParameter(1, "110284026");
			ChargerList = query.getResultList();
			System.out.println(ChargerList.size());

		} catch (IllegalStateException e) {
			System.out.println(e);
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e); 
		} finally{
			em.close();
		}
		
	}

	public static EntityManager createEntityManager() {
		
		entityManagerFactory = Persistence.createEntityManagerFactory("polcaincapu");
		return entityManagerFactory.createEntityManager();
	}
}


@Entity
@Table(name = "Charger")
class ChargerEntityExt5 extends ChargerEntity1Abstract implements Serializable {
	
	@Transient
	private static final long serialVersionUID = -1L;
	
	@OneToMany(mappedBy="chargerEntityExt5",targetEntity=PortEntityExt5.class,
			fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	public List<PinEntityExt5> pinEntityExtList;

	public List<PinEntityExt5> getPinEntityExtList() {
		return pinEntityExtList;
	}

	public void setPinEntityExtList(List<PinEntityExt5> pinEntityExtList) {
		this.pinEntityExtList = pinEntityExtList;
	}
	
}



@MappedSuperclass
class ChargerEntity1Abstract implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	public Long Id;
	
	@Column(name="CHARGER_ID")
	public String chargerId;


	// *** Constructor ****
	public ChargerEntity1Abstract() {
	}

	@Override
    public String toString() {
	    return "ChargerEntity [cpeId=" + Id!=null? Id.toString():""  + 
	    		", chargerId=" + chargerId!=null? chargerId.toString():""  + "]";
    }

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}
	
}



@Entity
@Table(name = "PIN")
class PinEntityExt5 extends PinEntity1Abstract implements Serializable{

	@Transient
	private static final long serialVersionUID = -1L;
	
	@ManyToOne(optional=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="CHARGER_ID", referencedColumnName="CHARGER_ID", insertable= false, updatable = false)
	private ChargerEntityExt5 chargerEntityExt5;

	public ChargerEntityExt5 getChargerEntityExt5() {
		return chargerEntityExt5;
	}

	public void setChargerEntityExt5(ChargerEntityExt5 chargerEntityExt5) {
		this.chargerEntityExt5 = chargerEntityExt5;
	}
	
}


@MappedSuperclass
@IdClass(ChargerPin.class)
class PinEntity1Abstract implements Serializable{
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="CHARGER_ID", nullable = true)
	public String chargerId;
	
	@Id
	@Column(name="PIN_NUMBER", nullable = true)
	public Long pin;
	
	public PinEntity1Abstract() {
	}

	
	@Override
    public String toString() {
	    return "PinEntity [chargerId=" + chargerId + 
	    		", port=" + pin!=null? pin.toString():""  + "]";
    }


	public String getChargerId() {
		return chargerId;
	}


	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}


	public Long getPin() {
		return pin;
	}


	public void setPin(Long pin) {
		this.pin = pin;
	}

}


class ChargerPin implements java.io.Serializable{

	@Transient
	private static final long serialVersionUID = 1L;

	private String chargerId;
	
	private Long pin;

	// **** Constructor *****
	public ChargerPin() {
    }
	
	public ChargerPin(String kapId, Long port) {
		this.chargerId = kapId;
		this.pin = port;
	}

	
	
	@Override
    public String toString() {
	    return "kapId [chargerId=" + chargerId!=null? chargerId.toString():"" +
	    		", port=" + pin!=null? pin.toString():""  + "]";
    }
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
	    int result = 1;
	    int kap = chargerId != null ?(Integer.parseInt(chargerId)):0;
	    int kap_port = pin!=null?(pin).intValue():0;
	    result = prime * result + kap;
	    result = prime * result + kap_port;
	    return result;
	    
	}

	@Override
	public boolean equals(Object obj) {
	    //return ((o instanceof CpePort) && (kapId.equalsIgnoreCase(((CpePort) o).getKapId())) && (port == (((CpePort) o).getPort())));
		if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (!(obj instanceof CpePort1))
		    return false;
	    ChargerPin other = (ChargerPin) obj;
	    if (chargerId == null) {
		    if (other.chargerId != null)
			    return false;
	    } else if ((other.chargerId != null) && chargerId.equals(other.chargerId)) {
		    return false;
	    }
	    
	    if (pin == null) {
		    if (other.pin != null)
			    return false;
	    } else if ((other.pin != null) && (pin != other.pin))
		    return false;
	    
	    return true;
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public Long getPin() {
		return pin;
	}

	public void setPin(Long pin) {
		this.pin = pin;
	}
	
}
