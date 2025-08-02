package hhh4459;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.Proxy;
import org.hibernate.envers.Audited;

@Audited
@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true, optimisticLock = OptimisticLockType.NONE)
@Table(name = "ATable")
@AccessType("field")
@Proxy
@DiscriminatorFormula("(case when parameter = 'A' then 'B' else case when parameter = 'C' then 'D' else 'UNKNOWN' end end end)")
@DiscriminatorValue("A")
public class ExampleTestClass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AnID")
	private Long id;
	@Column(name = "Text")
	private String text;
}
