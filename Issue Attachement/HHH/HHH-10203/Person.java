package org.hibernate.hibernate5.uniquedelegate;

public class Person {
  
  private long id;
  private String name;
  
  public Person(String name) {
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

}
