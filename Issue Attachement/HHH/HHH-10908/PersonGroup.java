package org.hibernate.hibernate5.collectionfunction;

import java.util.ArrayList;
import java.util.List;

public class PersonGroup {
  
  private long id;
  private String name;
  private List<Person> persons = new ArrayList<>();
  
  public PersonGroup() {
  }
  
  public PersonGroup(String name) {
    this.name = name;
  }
  
  public List<Person> getPersons() {
    return persons;
  }
  
  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }
  
  public void addPerson(Person person) {
    persons.add(person);
  }
  
  public long getId() {
    return id;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
}
