package duplicatemappingbug;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.Proxy;
import org.hibernate.envers.Audited;

@Audited
@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true, optimisticLock = OptimisticLockType.NONE)
@Table(name = "BTable")
@AccessType("field")
@Proxy
public class ChildOfExampleTestClass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AnID")
	private Long id;
	@Column(name = "Text")
	private String text;
}
