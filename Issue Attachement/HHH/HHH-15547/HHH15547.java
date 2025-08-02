package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class HHH15547 {
	private EntityManagerFactory entityManagerFactory;
	
	@Entity(name = "Library")
	public static class Library {
		@Id
		public Long id;
		@ElementCollection
		public Set<String> books;
		@ElementCollection
		public List<String> movies;
	}
	
	@BeforeEach
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@AfterEach
	public void destroy() {
		entityManagerFactory.close();
	}
	
	@Test
	public void hhh15547_test() {
		doInEM(em -> {
			Library lib = new Library();
			lib.id = 1L;
			lib.books = Set.of("b1", "b2");
			lib.movies = List.of("m1", "m2");
			em.persist(lib);
		});
		
		doInEM(em -> {
			TypedQuery<Library> query = em.createQuery(
					"SELECT l FROM Library AS l "
					+ "LEFT JOIN FETCH l.books "
					+ "LEFT JOIN FETCH l.movies", 
					Library.class);
			Library lib = query.getSingleResult();
			lib.movies.add("m3");
		});
		
		doInEM(em -> {
			Library lib = em.find(Library.class, 1L);
			// fails, is actually "m1", "m2", "m1", "m2", "m3"
			assertEquals(List.of("m1", "m2", "m3"), lib.movies);
		});
	}
	
	void doInEM(Consumer<EntityManager> function) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		function.accept(entityManager);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
