package org.wfp.rita.test.hibernate;

import java.sql.Connection;
import java.sql.Statement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.wfp.rita.test.base.HibernateTestBase;

public class HibernateAnnotationJoinOnFormulaTest extends HibernateTestBase
{
    @Entity
    @Table(name="product")
    private static class Product
    {
        @Id
        public Integer id;
        
        @Column(name="product_idnf", length=18, nullable=false, unique=true,
            columnDefinition="char(18)")
        public String productIdnf;
        
        @Column(name="description", nullable=false)
        public String description;

        @ManyToOne
        @ForeignKey(name="none")
        @Formula(value="SUBSTR(productIdnf, 1, 3)")
        @Fetch(FetchMode.JOIN)
        private Product productFamily;
        
        public Product getProductFamily()
        {
            return productFamily;
        }
    }
    
    protected Class[] getMappings()
    {
        return new Class[]{Product.class};
    }
    
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
    
    public void testFailing()
    {
        Session session = openSession();
        Product kit    = (Product) session.load(Product.class, 1, LockMode.READ);
        Product kitkat = (Product) session.load(Product.class, 2, LockMode.READ);
        assertEquals(kit, kitkat.getProductFamily());
        session.close();
    }    
}
