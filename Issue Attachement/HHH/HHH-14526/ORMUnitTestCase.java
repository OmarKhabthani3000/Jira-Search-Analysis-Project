/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import javax.persistence.*;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                DataType.class,
                ObjectType.class,
                SimpleType.class,
                Prop.class
        };
    }


    // If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
    @Override
    protected String[] getMappings() {
        return new String[]{
//              "Foo.hbm.xml",
//				"Bar.hbm.xml"
        };
    }

    // If those mappings reside somewhere other than resources/org/hibernate/test, change this.
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/test/";
    }

    // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
        //configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
    }

    // Add your tests, using standard JUnit.
    @Test
    public void test_append_properties() throws Exception {
        // BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
        try (Session sess = openSession()) {
            Long sId = doInTransaction(sess, (s, tx) -> {
                SimpleType simpleType = new SimpleType();
                simpleType.setName("simple");
                return (Long) s.save(simpleType);
            });
            doInTransaction(sess, (s, tx) -> {
                SimpleType simpleType = s.find(SimpleType.class, sId);
                assertEquals("simple", simpleType.getName());
                return null;
            });
            Long id = doInTransaction(sess, (s, tx) -> {
                ObjectType objectType = new ObjectType();
                objectType.setName("name");
                return (Long) s.save(objectType);
            });
            doInTransaction(sess, (s, tx) -> {
                ObjectType objectType = sess.find(ObjectType.class, id);

                Prop property1 = new Prop();
                property1.setName("Prop1");
                property1.setObjectType(objectType);

                objectType.setProperties(List.of(property1));
                s.save(objectType);
                return id;
            });
            doInTransaction(sess, (s, tx) -> {
                ObjectType objectType = sess.find(ObjectType.class, id);
                assertEquals(1, objectType.getProperties().size());
                return null;
            });
        }
    }

    Long doInTransaction(Session s, Action action) {
        Transaction tx = s.beginTransaction();
        try {
            Long result = action.doIt(s, tx);
            tx.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            return null;
        }
    }

    interface Action {
        Long doIt(Session s, Transaction tx);
    }

    @Entity
    @Table(name = "DATA_TYPE")
    @Inheritance(strategy = InheritanceType.JOINED)
    @DiscriminatorColumn(name = "supertype_id")
    public static abstract class DataType {

        private Long id;
        private String name;

        @Id
        @Column(name = "ID")
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Column(name = "name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Entity
    @DiscriminatorValue("8")
    @Table(name = "OBJ_TYPE")
    @PrimaryKeyJoinColumn(name = "TYPE_ID")
    public static class ObjectType extends DataType {

        private String description;
        private List<Prop> properties;

        @Column(name = "desc", table = "OBJ_TYPE")
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @OneToMany(mappedBy = "objectType", cascade = CascadeType.ALL, orphanRemoval = true)
        public List<Prop> getProperties() {
            return properties;
        }

        public void setProperties(List<Prop> properties) {
            this.properties = properties;
        }
    }

    @Entity
    @Table(name = "PROP")
    public static class Prop {

        private Long id;
        private String name;
        private ObjectType objectType;

        @Id
        @Column(name = "ID")
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Column(name = "name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JoinColumn(name = "OBJ_TYPE_ID")
        @ManyToOne(targetEntity = ObjectType.class)
        public ObjectType getObjectType() {
            return objectType;
        }

        public void setObjectType(ObjectType objectType) {
            this.objectType = objectType;
        }
    }

    @Entity
    @DiscriminatorValue("2")
    @Table(name = "DATA_TYPE")
    public static class SimpleType extends DataType {
    }

}
