package dk.tdc.visitation.polcalink.resource.dbResource.polcadb.inca.servingfp;

import static org.eclipse.persistence.config.PersistenceUnitProperties.*; 


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.*;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.TargetServer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.ejb.*;

import dk.tdc.visitation.polcalink.resource.dbResource.polcadb.inca.JpaINCAHelper;

public class EmbeddedIDTester {

	/**
	 * @param args
	 */
	public static EntityManagerFactory entityManagerFactory;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EntityManager em = null;
		
		List<Charger> ChargerList = null;
		Query query = null;
		String queryString = "SELECT ce FROM Charger ce";
		try {

			try {
				em = createEntityManager();
			} catch (Exception e) {
				System.out.println(e);
			}

			query = em.createQuery(queryString);
			ChargerList = query.getResultList();

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
		Map<Object, Object> properties = new HashMap<Object, Object>();

		properties.put(TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL);
		properties.put(JDBC_USER, "--------------");
		properties.put(JDBC_PASSWORD ,"--------------");
		properties.put(JDBC_DRIVER,"oracle.jdbc.OracleDriver");			
		properties.put(JDBC_URL, "--------------" );


		properties.put(LOGGING_LEVEL, "FINE");
		properties.put(LOGGING_TIMESTAMP, "false");
		properties.put(LOGGING_THREAD, "false");
		properties.put(LOGGING_SESSION, "false");

		properties.put(TARGET_SERVER, TargetServer.None);

		
		entityManagerFactory = Persistence.createEntityManagerFactory("unitName", properties);
		return entityManagerFactory.createEntityManager();
	}

}

@Entity
@Table(name = "Charger")
class Charger extends ChargerEntityAbstract implements Serializable {
		
	@OneToMany(mappedBy="charger",targetEntity=PinEntityExt2.class,
			fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	public List<PinEntityExt2> pinEntityExtList;
	
	public List<PinEntityExt2> getPinEntityExtList() {
		return pinEntityExtList;
	}

	public void setPinEntityExtList(List<PinEntityExt2> pinEntityExtList) {
		this.pinEntityExtList = pinEntityExtList;
	}
	
}

@MappedSuperclass
class ChargerEntityAbstract implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	private Long Id;

	@Column(name="CHARGER_ID", nullable = true)
	private String chargerId;
	
	// *** Constructor ****
	public ChargerEntityAbstract() {
	}

	// *** Getter and Setters ***
	public Long getId() {
		return this.Id;
	}
	public void setId(Long Id) {
		this.Id = Id;
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
class PinEntityExt2 extends PinEntityAbstract implements Serializable{

	@Transient
	private static final long serialVersionUID = -1L;
	
	@ManyToOne(optional=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="CHARGER_ID", referencedColumnName="CHARGER_ID", insertable= false, updatable = false)
	private Charger charger;
	
	public Charger getCharger() {
		return this.charger;
	}

	public void setCharger(Charger charger) {
		this.charger = charger;
	}
	
}


@MappedSuperclass
@IdClass(ChargerPin.class)
class PinEntityAbstract implements Serializable{
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="CHARGER_ID", nullable = true)
	private String chargerId;
	
	@Id
	@Column(name="PIN_NUMBER", nullable = true)
	private Long pinNumber;
	
	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public Long getPinNumber() {
		return pinNumber;
	}

	public void setPinNumber(Long pinNumber) {
		this.pinNumber = pinNumber;
	}	
}


class ChargerPin implements java.io.Serializable{

	@Transient
	private static final long serialVersionUID = 1L;

	public String chargerId;
	
	public Long pinNumber;

	// **** Constructor *****
	public ChargerPin() {
	}
	
	public ChargerPin(String chargerId, Long pinNumber) {
		this.chargerId = chargerId;
		this.pinNumber = pinNumber;
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public Long getPinNumber() {
		return pinNumber;
	}

	public void setPinNumber(Long pinNumber) {
		this.pinNumber = pinNumber;
	}
	

	
	@Override
	public int hashCode() {
		
	    final int prime = 31;
	    int result = 1;
	    int chargerIdHash = chargerId != null ?(Integer.parseInt(chargerId)):0;
	    int pinNumberHash = pinNumber!=null?(pinNumber).intValue():0;
	    result = prime * result + chargerIdHash;
	    result = prime * result + pinNumberHash;
	    return result;
	    
	}

	@Override
	public boolean equals(Object obj) {
	   
	   if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (!(obj instanceof ChargerPin))
		    return false;
	    ChargerPin other = (ChargerPin) obj;
	    if (chargerId == null) {
		    if (other.chargerId != null)
			    return false;
	    } else if (!chargerId.equals(other.chargerId))
		    return false;
	    
	    if (pinNumber == null) {
		    if (other.pinNumber != null)
			    return false;
	    } else if (pinNumber != other.pinNumber)
		    return false;
	    
	    return true;
	}
	
}

