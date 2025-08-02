package test;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Stateless
public class CountryServiceBean {

	@PersistenceContext
	private EntityManager entityManager;

	public void persistCountry(Country country){
		entityManager.persist(country);
	}

	public Country mergeCountry(Country country){
		return entityManager.merge(country);
	}

	public List<Country> findAllCountries(){
		TypedQuery<Country> query = entityManager.createQuery("SELECT c FROM Country c", Country.class);
		return query.getResultList();
	}

	public void moveNameToEnd(Country country,String newName) {
		// load from DB:
		Country loaded=entityManager.find(Country.class,country.getId());
		if(loaded!=null && loaded.getNameList().size()>2) {
	        NameElement movedElement=loaded.getNameList().remove(1);
	        loaded.getNameList().add(movedElement);

	        try {
				String json=new ObjectMapper().writeValueAsString(loaded.getNameList());
				System.out.println("country names are moved. New JSON is now: "+json);

				if(newName!=null) {
					// if the name is changed, the update is ALWAYS working!
					loaded.setName(newName);
				}
			} catch (JsonProcessingException e) {
				System.out.println("An exception occurred! "+e);
			}
		}
	}

	public void deleteAllCountries() {
		String deleteSQL="DELETE FROM country";
		entityManager.createNativeQuery(deleteSQL).executeUpdate();
	}

}
