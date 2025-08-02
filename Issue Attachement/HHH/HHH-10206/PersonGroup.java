package org.hibernate.hibernate5.primarykey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PersonGroup {
  
  private long id;
  private String name;
  private Set<Person> persons = new HashSet<>();
  private Map<String, String> comments = new HashMap<>();
  
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
  
  public void setPersons(Set<Person> persons) {
    this.persons = persons;
  }

  public Map<String, String> getComments() {
    return comments;
  }
  
  public void setComments(Map<String, String> comments) {
    this.comments = comments;
  }

}