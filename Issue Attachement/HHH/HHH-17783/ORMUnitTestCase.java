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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static jakarta.persistence.CascadeType.ALL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework. Although ORMStandaloneTestCase is perfectly
 * acceptable as a reproducer, usage of this class is much preferred. Since we nearly always include a regression test with bug fixes, providing your reproducer
 * using this method simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                ManyEntity.class,
                OneEntity.class
        };
    }


    // If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
    @Override
    protected String[] getMappings() {
        return new String[]{};
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
        configuration.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.FALSE.toString());
    }


    // Add your tests, using standard JUnit.
    @Test
    public void hhh123Test() throws Exception {
        // BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
        Session s = openSession();
        Transaction tx = s.beginTransaction();

        var many1 = new ManyEntity("a", "Many 1");
        var many2 = new ManyEntity("b", "Many 2");

        var one = new OneEntity("One 1");
        one.addMany(many1);
        one.addMany(many2);
        s.persist(one);

        tx.commit();
        s.close();

        s = openSession();

        var cb = s.getCriteriaBuilder();
        var query = cb.createQuery(ManyEntity.class);
        Root<ManyEntity> root = query.from(ManyEntity.class);
        query.select(root).where(root.get("id").in(many1.getId(), many2.getId()));
        var actual = s.createQuery(query).getResultList();

        s.close();

        assertThat(actual).containsExactly(many1, many2);
        assertThat(actual.get(0).getOne().getId()).isEqualTo(one.getId());
    }


    @Entity
    @Table(name = "many")
    public static class ManyEntity {

        @Id
        @Column(name = "id", nullable = false)
        private String id;

        @Column(name = "description", nullable = false)
        private String description;

        @ManyToOne
        @JoinColumn(name = "one_id")
        private OneEntity one;


        public ManyEntity(String id, String description) {
            this.id = id;
            this.description = description;
        }


        public ManyEntity() {}


        public String getId() {
            return id;
        }


        public String getDescription() {
            return description;
        }


        public OneEntity getOne() {
            return one;
        }


        public void setOne(OneEntity one) {
            this.one = one;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ManyEntity)) return false;
            ManyEntity that = (ManyEntity) o;
            return Objects.equals(getId(), that.getId()) && Objects.equals(getDescription(), that.getDescription());
        }


        @Override
        public int hashCode() {
            return 31 * getId().hashCode() + getDescription().hashCode();
        }
    }


    @Entity
    @Table(name = "one")
    public static class OneEntity {

        @Column(name = "description", nullable = false)
        private String description;

        @Id
        @Column(name = "id", nullable = false)
        private UUID id = UUID.randomUUID();

        @OneToMany(orphanRemoval = true, mappedBy = "one", cascade = {ALL}, fetch = FetchType.EAGER)
        private Set<ManyEntity> many = new HashSet<>();


        public OneEntity(String description) {
            this.description = description;
        }


        public OneEntity() {}


        public UUID getId() {
            return id;
        }


        public String getDescription() {
            return description;
        }


        public void addMany(ManyEntity newMany) {
            many.add(newMany);
            newMany.setOne(this);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OneEntity)) return false;
            OneEntity oneEntity = (OneEntity) o;
            return Objects.equals(getDescription(), oneEntity.getDescription());
        }


        @Override
        public int hashCode() {
            return Objects.hash(getDescription());
        }
    }
}
