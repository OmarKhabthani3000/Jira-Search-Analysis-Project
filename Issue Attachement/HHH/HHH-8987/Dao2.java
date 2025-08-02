import org.hibernate.annotations.AccessType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@javax.persistence.Entity
@org.hibernate.annotations.Entity(dynamicUpdate = false)
@SequenceGenerator(name = "Dao2Id", sequenceName = "Dao2Id", allocationSize = 1)
@AccessType("field")
public class Dao2
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Dao2Id")
	@Column(name = "id", nullable = false)
	@SuppressWarnings({"UnusedDeclaration"})
	private int id;

	private String text;

	public Dao2()
	{
	}

	public Dao2(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}
}
