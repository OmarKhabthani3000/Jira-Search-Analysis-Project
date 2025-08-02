import org.hibernate.annotations.AccessType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@javax.persistence.Entity
@org.hibernate.annotations.Entity(dynamicUpdate = false)
@SequenceGenerator(name = "Dao3Id", sequenceName = "Dao3Id", allocationSize = 1)
@AccessType("field")
public class Dao3
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Dao3Id")
	@Column(name = "id", nullable = false)
	@SuppressWarnings({"UnusedDeclaration"})
	private int id;

	private String text;

	public Dao3() {}

	public Dao3(int id)
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
