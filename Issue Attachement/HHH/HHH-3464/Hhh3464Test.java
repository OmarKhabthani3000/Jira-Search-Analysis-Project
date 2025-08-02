/*
 *  Copyright 2014 Vitalii Tymchyshyn
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.*;
import java.util.Locale;

/**
 * @author Vitalii Tymchyshyn
 */

public class Hhh3464Test {
    @Test
    public void test() {
        Locale.setDefault(Locale.ENGLISH);
        SessionFactory sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .addAnnotatedClass(TestEntity.class)
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new TestEntity("I"));
        session.getTransaction().commit();
        session.beginTransaction();
        Assert.assertEquals(1, session.createCriteria(TestEntity.class).add(Expression.eq("value", "I")).list().size());
        Assert.assertEquals(1, session.createCriteria(TestEntity.class).add(Expression.eq("value", "I")
                .ignoreCase()).list().size());
        session.close();
    }

    @Entity
    @Table(name = "test_entity")
    public static class TestEntity {
        private Long id;
        private String value;

        public TestEntity(String key) {
            this.value = key;
        }

        public TestEntity() {
        }

        @Column
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Id
        @GeneratedValue(generator="increment")
        @GenericGenerator(name="increment", strategy = "increment")
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
