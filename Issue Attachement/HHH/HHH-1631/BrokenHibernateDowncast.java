package com.i2rd.occasio.test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.validator.NotNull;

/**
 * Test for broken hibernate down cast.
 * 
 * @author Pat B. Double (double@i2rd.com)
 */
public class BrokenHibernateDowncast
{
    @Entity
    @Table(name="cat")
    @Inheritance(strategy=InheritanceType.JOINED)
    public static class Cat
    {
        @Id @GeneratedValue(strategy=GenerationType.AUTO, generator="cat_id_seq")
        @NotNull
        @javax.persistence.SequenceGenerator(
                name="cat_id_seq",
                sequenceName="cat_id_seq"
            )
        long id;
        String name;
    }
    
    @Entity
    @Table(name="domesticcat")
    public static class DomesticCat extends Cat
    {
        String region;
    }
    
    @Entity
    @Table(name="bobcat")
    public static class Bobcat extends Cat
    {
        int meanness;
    }

    @Entity
    @Table(name="dog")
    @Inheritance(strategy=InheritanceType.JOINED)
    public static class Dog
    {
        @Id @GeneratedValue(strategy=GenerationType.AUTO, generator="dog_id_seq")
        @NotNull
        @javax.persistence.SequenceGenerator(
                name="dog_id_seq",
                sequenceName="dog_id_seq"
            )
        long id;
        String name;
    }

    @Entity
    public static class ToyDog extends Dog
    {
        int tinyness;
    }
    
    @Entity
    public static class LargeDog extends Dog
    {
        int largeness;
    }
    
    @Entity
    @Table(name="city")
    public static class City
    {
        @Id @GeneratedValue(strategy=GenerationType.AUTO, generator="city_id_seq")
        @NotNull
        @javax.persistence.SequenceGenerator(
                name="city_id_seq",
                sequenceName="city_id_seq"
            )
        long id;
        String name;
        String state;
    }

    @Entity
    @Table(name="pound")
    public static class Pound
    {
        @Id @GeneratedValue(strategy=GenerationType.AUTO, generator="pound_id_seq")
        @NotNull
        @javax.persistence.SequenceGenerator(
                name="pound_id_seq",
                sequenceName="pound_id_seq"
            )
        long id;
        @ManyToOne
        @NotNull
        City location;
        @OneToMany
        Set<Cat> cats = new HashSet<Cat>();
        @OneToMany
        Set<Dog> dogs = new HashSet<Dog>();
    }
    
    public static void main(String[] args) throws Exception
    {
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost/thepound");
        props.put("hibernate.connection.username", "cms");
        props.put("hibernate.connection.password", "");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.connection.release_mode", "on_close");
        props.put("hibernate.hbm2ddl.auto", "create");
        
        Configuration cfg = new AnnotationConfiguration()
            .addAnnotatedClass(Pound.class)
            .addAnnotatedClass(Cat.class)
            .addAnnotatedClass(DomesticCat.class)
            .addAnnotatedClass(Bobcat.class)
            .addAnnotatedClass(City.class)
            .addAnnotatedClass(Dog.class)
            .addAnnotatedClass(ToyDog.class)
            .addAnnotatedClass(LargeDog.class)
            .setProperties(props);
        SchemaUpdate update = new SchemaUpdate(cfg, props);
        update.execute(true, true);
        SessionFactory sessionFactory = cfg.buildSessionFactory();
        Session session = sessionFactory.openSession();

        City anytown = new City();
        anytown.name = "Anytown";
        anytown.state = "NE";
        session.save(anytown);
        
        DomesticCat dc = new DomesticCat();
        dc.name="charley";
        dc.region="Midwest USA";
        session.save(dc);
        
        Bobcat bc = new Bobcat();
        bc.name="bobby";
        bc.meanness=10;
        session.save(bc);

        ToyDog poodle = new ToyDog();
        poodle.name="fifi";
        poodle.tinyness = 1;
        session.save(poodle);
        
        LargeDog dob = new LargeDog();
        dob.name="max";
        dob.largeness = 100;
        session.save(dob);
        
        Pound pound = new Pound();
        pound.location = anytown;
        pound.cats.add(dc);
        pound.cats.add(bc);
        pound.dogs.add(poodle);
        pound.dogs.add(dob);
        session.save(pound);
        
        session.flush();
        
        // This works:
        System.out.println(session.createQuery("from "+Pound.class.getName()
                +" as pound inner join pound.cats as cat left join pound.dogs as dog where pound.location.name='Anytown' and cat.meanness > 5").list());

        // This does not: the difference being the select clause, the extra 'left join' does not seem to make a difference
        System.out.println(session.createQuery("select pound.location.state from "+Pound.class.getName()
                +" as pound inner join pound.cats as cat where pound.location.name='Anytown' and cat.meanness > 5").list());

        // This does not: the difference being the select clause
        System.out.println(session.createQuery("select pound.location.state from "+Pound.class.getName()
                +" as pound inner join pound.cats as cat left join pound.dogs as dog where pound.location.name='Anytown' and cat.meanness > 5").list());

        session.close();
    }
    
    private BrokenHibernateDowncast()
    {
        super();
    }
}
