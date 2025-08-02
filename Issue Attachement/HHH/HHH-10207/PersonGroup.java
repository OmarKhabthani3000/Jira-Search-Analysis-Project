package org.hibernate.hibernate5.constraintname;

import java.util.HashSet;
import java.util.Set;

public class PersonGroup {
  
  private long id;
  private String name;
  private final Set<Person> persons = new HashSet<>();
  
  public PersonGroup(String name) {
    this.name = name;
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
  
  public Set<Person> getPersons() {
    return persons;
  }

}
