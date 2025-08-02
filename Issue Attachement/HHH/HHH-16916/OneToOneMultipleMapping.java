package com.scutaru;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DiscriminatorOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Persistence;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class OneToOneMultipleMapping {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void hhh123Test() {
		Apple apple = new Apple();
		apple.setColor("red");
		apple.setKeepsTheDoctorAway(true);

		Orange orange = new Orange();
		orange.setColor("orange");
		orange.setSowerness(1);

		Container container = new Container();
		container.setApple(apple);
		container.setOrange(orange);
		orange.setContainer(container);
		apple.setContainer(container);

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(container);

		entityManager.getTransaction().commit();
		entityManager.clear();

		final Container containerFound = entityManager.find(Container.class, container.getId());
		assertTrue(containerFound.getApple().isKeepsTheDoctorAway());

		entityManager.close();
	}
}

@EqualsAndHashCode
@Data
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorColumn(name = "FRUIT_TYPE", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
@Table(name = "FRUIT")
class Fruit {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN_FRUIT")
	@SequenceGenerator(name = "SEQ_GEN_FRUIT", allocationSize = 10, sequenceName = "SEQ_FRUIT")
	private Long id;

	private String color;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "container_id", nullable = false)
	private Container container;
}

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@DiscriminatorValue(value = "APPLE")
class Apple extends Fruit {
	private boolean keepsTheDoctorAway;
}

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue(value = "ORANGE")
class Orange extends Fruit {
	private int sowerness;
}

@EqualsAndHashCode
@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "CONTAINER")
class Container {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN_CONTAINER")
	@SequenceGenerator(name = "SEQ_GEN_CONTAINER", allocationSize = 10, sequenceName = "SEQ_CONTAINER")
	private Long id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "container", orphanRemoval = true)
	private Orange orange;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "container", orphanRemoval = true)
	private Apple apple;

}
