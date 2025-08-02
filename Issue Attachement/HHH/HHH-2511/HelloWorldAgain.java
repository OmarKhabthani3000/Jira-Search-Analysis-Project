
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
public class HelloWorldAgain {

	@Id
	@GeneratedValue()
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Generated(GenerationTime.ALWAYS)
	@Column(updatable = false, insertable = false)
	private Date modified;

	public Long getId() {
		return id;
	}

	public Date getModified() {
		return modified;
	}

}
