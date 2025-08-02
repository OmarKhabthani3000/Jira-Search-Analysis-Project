package net.orless.hibernate.tests;

import java.util.ArrayList;
import java.util.List;

public class A {
  private String id;
  private List b = new ArrayList();
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public List getB() {
    return b;
  }
  public void setB(List b) {
    this.b = b;
  }
}
