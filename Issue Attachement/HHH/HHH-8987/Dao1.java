import org.hibernate.annotations.AccessType;

import javax.persistence.Column;
import javax.persistence.Id;

@javax.persistence.Entity
@org.hibernate.annotations.Entity(dynamicUpdate = false)
@AccessType("field")
public class Dao1
{
	@Id
	@Column(nullable = false)
	private Dao1PK id;

	public Dao1()
	{
	}

	public Dao1(Dao2 dao2, Dao3 dao3) throws NotNullViolationException
	{
		if (dao2 == null)
			throw new NotNullViolationException(this, "id.dao2.id");
		if (dao3 == null)
			throw new NotNullViolationException(this, "id.dao3.id");

		this.id = new Dao1PK(dao2, dao3);
	}

}
