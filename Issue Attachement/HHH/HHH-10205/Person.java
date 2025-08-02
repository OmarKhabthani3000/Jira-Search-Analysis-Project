package org.hibernate.hibernate5.uniqueconstraint;

public class Person {
  
  private long id;
  private String name;
  private String firstName;
  
  public Person(String name, String firstName) {
    this.name = name;
    this.firstName = firstName;
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
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

}
