package com.example.demo;

import jakarta.persistence.*;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SessionFactory
@DomainModel(annotatedClasses = {
        BooleanToStringTest.BooleanTestPOJO.class
})
@JiraKey("HHH-16401")
public class BooleanToStringTest {
    @BeforeAll
    public void setUp(SessionFactoryScope scope) {
        scope.inTransaction(session -> {
            session.persist( createPOJO(true, "CODE123", 1l) );
            session.persist( createPOJO(true, "CODE456", 2l) );
            session.persist( createPOJO(true, "CODE789", 3l) );
            session.persist( createPOJO(false, "CODE987", 4l) );
            session.persist( createPOJO(false, "CODE654", 5l) );
            session.persist( createPOJO(false, "CODE321", 6l) );
        });
    }

    private Object createPOJO(boolean b, String code, long id) {
        BooleanTestPOJO t = new BooleanTestPOJO();
        t.setTest(b);
        t.setCode(code);
        t.setId(id);
        return t;
    }

    @Test
    public void testEqualsTrueFalse(SessionFactoryScope scope) {
        scope.inTransaction(session -> {
            List<BooleanTestPOJO> test = session.createQuery(
                    "from BooleanTestPOJO pojo where pojo.test = true ",
                    BooleanToStringTest.BooleanTestPOJO.class
            ).getResultList();
            assertEquals( 3, test.size());
            assertTrue(test.stream().anyMatch(pojo -> pojo.getTest() && pojo.getCode().equals("CODE123")));
            assertTrue(test.stream().anyMatch(pojo -> pojo.getTest() && pojo.getCode().equals("CODE456")));
            assertTrue(test.stream().anyMatch(pojo -> pojo.getTest() && pojo.getCode().equals("CODE789")));
            test = session.createQuery(
                    "from BooleanTestPOJO pojo where pojo.test = false ",
                    BooleanToStringTest.BooleanTestPOJO.class
            ).getResultList();
            assertTrue(test.stream().anyMatch(pojo -> !pojo.getTest() && pojo.getCode().equals("CODE987")));
            assertTrue(test.stream().anyMatch(pojo -> !pojo.getTest() && pojo.getCode().equals("CODE654")));
            assertTrue(test.stream().anyMatch(pojo -> !pojo.getTest() && pojo.getCode().equals("CODE321")));
        });
    }

    @AfterAll
    public void tearDown(SessionFactoryScope scope) {
        scope.inTransaction(session -> {
            session.createMutationQuery("delete from BooleanTestPOJO ").executeUpdate();
        });
    }

    @Entity(name = "BooleanTestPOJO")
    public static class BooleanTestPOJO {

        @Id
        @Column
        private Long id;
        @Column
        @Convert(converter = BooleanJNConverter.class)
        private Boolean test;

        @Column
        private String code;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Boolean getTest() {
            return test;
        }

        public void setTest(Boolean test) {
            this.test = test;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Converter
    public static class BooleanJNConverter implements AttributeConverter<Boolean, String> {
        @Override
        public String convertToDatabaseColumn(Boolean attribute) {
            return Optional.ofNullable(attribute).map(b -> b ? "J" : "N").orElse(null);
        }

        @Override
        public Boolean convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData).map("J"::equals).orElse(null);
        }
    }


}
