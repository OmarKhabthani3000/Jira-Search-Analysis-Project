package annotationbug;


import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "SimpleAnnotation")
public class SimpleAnnotation implements Serializable
{

    /*
     * Properties
     */
    private Integer _id;
    private String _id1;
    private Integer _id2;
    private String _name;


    /*
     * Property getters and setters
     */


    @Column(name="fakeid", nullable=false)
    public Integer getId()
    {
        return _id;
    }

    public void setId(Integer value)
    {
        _id = value;
    }


    @Column
    @Id
    public String getId1()
    {
        return _id1;
    }

    public void setId1(String value)
    {
        _id1 = value;
    }


    @Column
    @Id
    public Integer getId2()
    {
        return _id2;
    }

    public void setId2(Integer value)
    {
        _id2 = value;
    }


    @Column
    public String getName()
    {
        return _name;
    }

    public void setName(String value)
    {
        _name = value;
    }




    /**
     * Equals - defined as a comparison of ID properties only.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SimpleAnnotation))
            return false;
        final SimpleAnnotation other = (SimpleAnnotation) obj;

        if (_id1 == null)
        {
            if (other._id1 != null)
            {
                return false;
            }
        }
        else if (!_id1.equals(other._id1))
        {
            return false;
        }
        if (_id2 == null)
        {
            if (other._id2 != null)
            {
                return false;
            }
        }
        else if (!_id2.equals(other._id2))
        {
            return false;
        }

        return true;
    }

    /**
     * Hashcode - defined as a hash of ID properties only.
     */
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;

        if ((_id1 == null) && (_id2 == null) )
        {
            result = System.identityHashCode(this);
        }
        else
        {
            result = PRIME * result + ((_id1 == null) ? 0 : _id1.hashCode());
            result = PRIME * result + ((_id2 == null) ? 0 : _id2.hashCode());
        }
        return result;
    }
}

