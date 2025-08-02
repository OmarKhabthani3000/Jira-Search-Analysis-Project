package hibernatebugtest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

	@Autowired
	PersonRepository personRepository;

	@RequestMapping(value="person", method=RequestMethod.GET)
	public List<Person> selectPersonBirthYear(){
		List<Person> personList = personRepository.findBirthYear();
		return personList;
	}

}
