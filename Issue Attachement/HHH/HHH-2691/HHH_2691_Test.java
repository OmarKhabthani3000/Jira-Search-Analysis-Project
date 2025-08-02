package org.hibernate.test.hql.hhh_2691;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.junit.functional.FunctionalTestCase;

/**
 * @author Martin Backhaus
 * @since 2009.10.02 12:20
 */
public class HHH_2691_Test extends FunctionalTestCase
{

    public HHH_2691_Test(final String string)
    {
        super(string);
    }
    
    public String[] getMappings()
    {
        return new String[] { "hql/hhh_2691/HHH_2691.hbm.xml" };
    }
    
    protected void prepareTest() throws Exception 
    {
        final Session s = openSession();

        // create dummy data
        s.beginTransaction();
        final Source source = new Source();
        source.setFieldA("templateA");
        source.setFieldB("templateB");
        source.setToChange("no template and should be replaced");
        s.saveOrUpdate(source);
        s.getTransaction().commit();
        
        s.close();
    }
    

    public void testInsertSelectWithParameter() 
    {
        final Session s = openSession();

        s.beginTransaction();
        // this one should work even with the current bug
        final Query q1 = s.createQuery("INSERT INTO org.hibernate.test.hql.hhh_2691.HHH_2691_Test$Dest(fieldA, fieldB, toChange) " + 
                                        "SELECT fieldA, fieldB, toChange FROM org.hibernate.test.hql.hhh_2691.HHH_2691_Test$Source");
        q1.executeUpdate();
        
        // this one dosn't work
        final Query q2 = s.createQuery("INSERT INTO org.hibernate.test.hql.hhh_2691.HHH_2691_Test$Dest(fieldA, fieldB, toChange) " +
                                        "SELECT fieldA, fieldB, :newValue FROM org.hibernate.test.hql.hhh_2691.HHH_2691_Test$Source");
        q2.setParameter("newValue", "now it is replaced");
        q2.executeUpdate();        
        
        s.getTransaction().commit();
        
        s.close();
    }

    public static class Source
    {
        private long id;
        private String fieldA;
        private String fieldB;
        private String toChange;
        
        public long getId()
        {
            return id;
        }

        public String getFieldA()
        {
            return fieldA;
        }

        public void setFieldA(String fieldA)
        {
            this.fieldA = fieldA;
        }

        public String getFieldB()
        {
            return fieldB;
        }

        public void setFieldB(String fieldB)
        {
            this.fieldB = fieldB;
        }

        public String getToChange()
        {
            return toChange;
        }

        public void setToChange(String toChange)
        {
            this.toChange = toChange;
        }
    }
    
    public static class Dest
    {
        private long id;
        private String fieldA;
        private String fieldB;
        private String toChange;
        
        public long getId()
        {
            return id;
        }

        public String getFieldA()
        {
            return fieldA;
        }

        public void setFieldA(String fieldA)
        {
            this.fieldA = fieldA;
        }

        public String getFieldB()
        {
            return fieldB;
        }

        public void setFieldB(String fieldB)
        {
            this.fieldB = fieldB;
        }

        public String getToChange()
        {
            return toChange;
        }

        public void setToChange(String toChange)
        {
            this.toChange = toChange;
        }
    }
}
