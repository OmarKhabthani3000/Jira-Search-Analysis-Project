package foo;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity implementation class for Entity: Order
 *
 */
@Entity
public class Client implements Serializable {
	@Id
	public int id;

	@Embedded
	public Name name;
}
