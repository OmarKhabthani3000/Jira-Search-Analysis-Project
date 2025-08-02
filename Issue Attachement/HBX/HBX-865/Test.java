

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.NotEmpty;

@Entity
public class Test
{
	@Id
	@GeneratedValue
	long id;
	
	@NotNull
	String test;

	public long getId()
    {
    	return id;
    }

	public void setId(long id)
    {
    	this.id = id;
    }

	public String getTest()
    {
    	return test;
    }

	public void setTest(String test)
    {
    	this.test = test;
    }

	@Override
    public int hashCode()
    {
	    final int PRIME = 31;
	    int result = 1;
	    result = PRIME * result + (int) (id ^ (id >>> 32));
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    final Test other = (Test) obj;
	    if (id != other.id)
		    return false;
	    return true;
    }
	
	
}
