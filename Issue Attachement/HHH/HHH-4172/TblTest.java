import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TBL_TEST", schema = "TRANSACAO")
public class TblTest implements java.io.Serializable {
	private Long id;
	private String name;
	private Long age;

	/** default constructor */
	public TblTest() {
	}

	public TblTest(Long id, String name, Long age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 30, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME", nullable = true)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "AGE", nullable = true)
	public Long getAge() {
		return this.age;
	}

	public void setAge(Long age) {
		this.age = age;
	}
}