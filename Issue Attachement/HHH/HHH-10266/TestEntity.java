package test;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Persistence;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
//@DynamicUpdate
public class TestEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Version
	private long version;
	
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(insertable=true, updatable=false)
	private Date createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date lastModifiedDate;

	private int value;
	
	public long getID() {
		return id;
	}
	
	public long getVersion() {
		return version;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TestEntity [id=" + id + ", version=" + version + ", value="
				+ value + ", createdDate=" + (createdDate == null ? "null" : createdDate)
				+ ", lastModifiedDate=" + (lastModifiedDate == null ? "null" : lastModifiedDate) + "]";
	}	
	
	public static void main(String[] args) {
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory("Exp");
			em = emf.createEntityManager();
			
			// create a TestEntity
			// want createdDate and lastModifiedDate to be set to current_timestamp
			TestEntity t = new TestEntity();
			t.setValue(1);
			
			em.getTransaction().begin();
			em.persist(t);
			em.getTransaction().commit();

			System.out.println("Created: " + t);
			
			System.out.println("");
			
			// modify the TestEntity
			// want createDate to remain unmodified and lastModifiedDate to be set to
			// current_timestamp
			t.setValue(2);
			
			em.getTransaction().begin();
			em.persist(t);
			em.getTransaction().commit();
			
			System.out.println("Updated: " + t);
			
		} finally {
			try  {
				if(em != null) {
					em.close();
				}
			} finally {
				if(emf != null) {
					emf.close();
				}
			}
		}

	}

}

