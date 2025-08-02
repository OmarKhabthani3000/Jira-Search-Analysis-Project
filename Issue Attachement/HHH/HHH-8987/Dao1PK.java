import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Embeddable
public class Dao1PK implements Serializable
{
	@ManyToOne
	@JoinColumn(name = "dao2id", nullable = false)
	private Dao2 dao2;
	@ManyToOne
	@JoinColumn(name = "dao3id", nullable = false)
	private Dao3 dao3;

	public Dao1PK()
	{
	}

	public Dao1PK(final Dao2 dao2, final Dao3 dao3)
	{
		this.dao2 = dao2;
		this.dao3 = dao3;
	}

	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final Dao1PK that = (Dao1PK) o;

		return (dao2.getId() == that.dao2.getId()) && (dao3.getId() == that.dao3.getId());
	}

	public int hashCode()
	{
		int result;
		result = dao2.getId();
		result = 29 * result + dao3.getId();
		return result;
	}

	public Dao2 getDao2()
	{
		return dao2;
	}

	public Dao3 getDao3()
	{
		return dao3;
	}
}
