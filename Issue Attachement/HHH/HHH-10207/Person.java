package org.hibernate.hibernate5.constraintname;

public class Person {
  
  private long id;
  private PersonGroup personGroup;
  
  public Person(PersonGroup personGroup) {
    this.personGroup = personGroup;
  }

  public long getId() {
    return id;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public PersonGroup getPersonGroup() {
    return personGroup;
  }
  
  public void setPersonGroup(PersonGroup personGroup) {
    this.personGroup = personGroup;
  }

}
