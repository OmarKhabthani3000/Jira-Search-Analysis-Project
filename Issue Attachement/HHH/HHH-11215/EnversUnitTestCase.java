/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Immutable;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
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
                Type.class
        };
    }


    // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
        //configuration.setProperty("org.hibernate.envers.audit_strategy", "org.hibernate.envers.strategy.ValidityAuditStrategy");
        //configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
    }

    // Add your tests, using standard JUnit.
    @Test
    public void npeWhenUsingNestedCollectionWithManyToOneRelationship() throws Exception {
        Session session = openSession();
        fillDictionary(session);
        Product product = new Product();
        product.setId(1L);
        product.setName("test");
        product.getItems().add(new Item("bread", new Type(1L)));

        inTransaction(product, session);

        product.getItems().add(new Item("bread2", new Type(2L)));
        inTransaction(product, session);

        product.getItems().remove(0);
        inTransaction(product, session);
        //clean session
        session.clear();

        AuditReader reader = getAuditReader();
        List<Number> revisions = reader.getRevisions(Product.class, 1L);
        debugAuditTable(session);

        //must be 3 revisions
        assertThat(revisions.size(), equalTo(3));


        //initially we have 1 item
        Product firstRevision = reader.find(Product.class, 1L, revisions.get(0));
        assertThat(firstRevision.getItems().size(), equalTo(1));
        assertThat(firstRevision.getItems().get(0).getName(), equalTo("bread"));
        assertThat(firstRevision.getItems().get(0).getType().getId(), equalTo(1L));

        //then add another one
        Product secondRevision = reader.find(Product.class, 1L, revisions.get(1));
        assertThat(secondRevision.getItems().size(), equalTo(2));
        assertThat(secondRevision.getItems().get(0).getName(), equalTo("bread"));
        assertThat(secondRevision.getItems().get(0).getType().getId(), equalTo(1L));
        assertThat(secondRevision.getItems().get(1).getName(), equalTo("bread2"));
        assertThat(secondRevision.getItems().get(1).getType().getId(), equalTo(2L));

        //delete first item
        Product thirdRevision = reader.find(Product.class, 1L, revisions.get(2));
        assertThat(thirdRevision.getItems().size(), equalTo(1));
        assertThat(thirdRevision.getItems().get(0).getName(), equalTo("bread2"));
        assertThat(thirdRevision.getItems().get(0).getType().getId(), equalTo(2L));
    }

    private void fillDictionary(Session session) {
        inTransaction(new Type(1L, "type1"), session);
        inTransaction(new Type(2L, "type2"), session);
        inTransaction(new Type(3L, "type3"), session);
    }

    private void debugAuditTable(Session session) {
        session.createNativeQuery("select * from ITEMS_AUD").stream()
                .map(a -> Arrays.toString((Object[])a))
                .forEach(System.out::println);
    }

    private void inTransaction(Object entity, Session session) {
        Transaction transaction = session.beginTransaction();
        session.persist(entity);
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

    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Type type;

    public Item() {
    }

    public Item(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        return type != null ? type.equals(item.type) : item.type == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}

@Entity
@Immutable
class Type {
    @Id
    private Long id;
    private String name;

    public Type() {
    }

    public Type(Long id) {
        this.id = id;
    }

    public Type(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Type type = (Type) o;

        if (id != null ? !id.equals(type.id) : type.id != null) return false;
        return name != null ? name.equals(type.name) : type.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}


