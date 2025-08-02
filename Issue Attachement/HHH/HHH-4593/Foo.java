package foo;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class Foo implements Serializable {
	@Id
	@GeneratedValue
	@Column(name="ID")
	public int id;

	@ElementCollection
	@CollectionTable(name="FOO_BAR", joinColumns = @JoinColumn(referencedColumnName="FOO_ID"))
	@Column(name="NAME")
	public Set<String> bar;
}
