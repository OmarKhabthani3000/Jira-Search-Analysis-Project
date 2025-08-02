/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.Audited;
import org.junit.Test;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * This template demonstrates how to develop a test case for Hibernate Envers, using
 * its built-in unit test framework.
 */
public class EnversUnitTestCase extends AbstractEnversTestCase {

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                Product.class,
        };
    }


    // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
        configuration.setProperty("org.hibernate.envers.audit_strategy", "org.hibernate.envers.strategy.ValidityAuditStrategy");
        //configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
    }

    // Add your tests, using standard JUnit.
    @Test
    public void invalidAuditingNestedCollectionWithNullValue() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("test");
        product.getItems().add(new Item("bread", null));

        Session session = openSession();
        inTransaction(product, session);

        product.getItems().add(new Item("bread2", 2L));
        inTransaction(product, session);

        product.getItems().remove(0);
        inTransaction(product, session);


        AuditReader reader = getAuditReader();
        List<Number> revisions = reader.getRevisions(Product.class, 1L);
        debugAuditTable(session);

        //must be 3 revisions
        assertThat(revisions.size(), equalTo(3));


        //initially we have 1 item
        Product firstRevision = reader.find(Product.class, 1L, revisions.get(0));
        assertThat(firstRevision.getItems().size(), equalTo(1));
        assertThat(firstRevision.getItems().get(0).getName(), equalTo("bread"));

        //then add another one
        Product secondRevision = reader.find(Product.class, 1L, revisions.get(1));
        assertThat(secondRevision.getItems().size(), equalTo(2));
        assertThat(secondRevision.getItems().get(0).getName(), equalTo("bread"));
        assertThat(secondRevision.getItems().get(1).getName(), equalTo("bread2"));

        //delete first item
        Product thirdRevision = reader.find(Product.class, 1L, revisions.get(2));
        assertThat(thirdRevision.getItems().size(), equalTo(1));
        assertThat(thirdRevision.getItems().get(0).getName(), equalTo("bread2"));
    }

    private void debugAuditTable(Session session) {
        session.createNativeQuery("select * from ITEMS_AUD").stream()
                .map(a -> Arrays.toString((Object[])a))
                .forEach(System.out::println);
    }

    private void inTransaction(Product product, Session session) {
        Transaction transaction = session.beginTransaction();
        session.persist(product);
        transaction.commit();
    }
}

@Entity
@Audited
class Product {
    @Id
    private Long id;

    private String name;

    @ElementCollection
    @CollectionTable(name = "ITEMS",
            joinColumns = @JoinColumn(name = "PRODUCT_ID")
    )
    @OrderColumn(name = "ORDER_COL")
    @Audited
    private List<Item> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (id != null ? !id.equals(product.id) : product.id != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        return items != null ? items.equals(product.items) : product.items == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}

@Embeddable
@Audited
class Item {
    private String name;
    private Long value;

    public Item() {
    }

    public Item(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        return value != null ? value.equals(item.value) : item.value == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}


