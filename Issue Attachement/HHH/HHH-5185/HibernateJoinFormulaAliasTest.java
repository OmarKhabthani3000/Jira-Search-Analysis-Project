package org.aptivate.hibernate.test;

import java.sql.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.aptivate.hibernate.test.base.HibernateTestBase;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

/**
 * @see <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-5041">HHH-5041</a>
 * @author Chris Wilson <chris+rita@aptivate.org>
 */
public class HibernateJoinFormulaAliasTest extends HibernateTestBase
{
    @Entity
    @Table(name="houses")
    private static class House
    {
        @Id
        public Integer id;
        
        @OneToMany(mappedBy="firstHome")
        @Fetch(FetchMode.JOIN)
        public Set<Cat> cats1;

        @OneToMany(mappedBy="secondHome")
        @Fetch(FetchMode.JOIN)
        public Set<Cat> cats2;
    }
    
    @Entity
    @Table(name="cats")
    private static class Cat
    {
        @Id
        public Integer id;

        @ManyToOne
        @Fetch(FetchMode.SELECT)
        public House firstHome;
        
        @ManyToOne
        @Fetch(FetchMode.SELECT)
        public House secondHome;
        
        @Column(name="kittens")
        public Integer kittens;

        @Formula("(kittens * 4) + 3")
        public Integer legs;
        
        @Column
        public Date birthday;
        
        @Formula("EXTRACT(MONTH FROM birthday)")
        public Integer birthMonth;
        
    }
    
    protected Class[] getMappings()
    {
        return new Class[]{House.class, Cat.class};
    }
    
    /*
    public void setUp() throws Exception
    {
        super.setUp();
        
        Session session = openSession();
        Connection conn = session.connection();
        
        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO product SET id = 1," +
            "product_idnf = 'KIT', description = 'Kit'");
        stmt.execute("INSERT INTO product SET id = 2, " +
            "product_idnf = 'KIT_KAT', description = 'Chocolate'");
        
        session.close();
    }
    */
    
    public void testFailing()
    {
        Session session = openSession();
        session.get(House.class, 1, LockMode.READ);
        session.close();
    }    
}
