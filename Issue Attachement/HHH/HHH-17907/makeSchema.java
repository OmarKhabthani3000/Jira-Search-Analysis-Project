package testlob;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class makeSchema {

	public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPersistenceUnit");
        emf.createEntityManager().close();
        emf.close();
        System.out.println("done.");
	}
	
}
