package org.hibernate.hibernate5.index;


public class PersonGroup {
  
  private long id;
  private String name;
  
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

}
