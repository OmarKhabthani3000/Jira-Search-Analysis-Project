
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.envers.Audited;

@Entity
@Audited
public class TestEntity {

	@Id
	@GeneratedValue
	private Integer id;
	private String foo;
	private String bar;

	TestEntity() {

	}

	public TestEntity(String foo, String bar) {
		this.foo = foo;
		this.bar = bar;
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public String getBar() {
		return bar;
	}

	public void setBar(String bar) {
		this.bar = bar;
	}

	public Integer getId() {
		return id;
	}

}
