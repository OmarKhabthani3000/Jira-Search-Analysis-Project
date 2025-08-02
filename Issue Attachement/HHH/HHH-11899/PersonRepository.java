package hibernatebugtest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends JpaRepository<Person, String>{

	@Query("SELECT id, name, extract(year from birthDate), birthDate FROM Person")
	public List<Person> findBirthYear();
}
