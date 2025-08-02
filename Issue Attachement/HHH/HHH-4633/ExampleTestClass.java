package duplicatemappingbug;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

@Audited
@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true, optimisticLock = OptimisticLockType.NONE)
@Table(name = "ATable")
@AccessType("field")
@Proxy
public class ExampleTestClass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AnID")
	private Long id;
	@Column(name = "Text")
	private String text;
	
	@OneToMany
	@JoinColumn(name = "AnIDToJoinOn")
	@Where(clause = "Type = 1")
	private Set<ChildOfExampleTestClass> childrenOfType1 = new HashSet<ChildOfExampleTestClass>();

	@OneToMany
	@JoinColumn(name = "AnIDToJoinOn")
	@Where(clause = "Type = 1")
	private Set<ChildOfExampleTestClass> childrenOfType2 = new HashSet<ChildOfExampleTestClass>();
}
