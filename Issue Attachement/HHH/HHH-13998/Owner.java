package org.hibernate.test.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Owner {
  @Id @GeneratedValue private int id;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy
  private List<Owned> ownedList = new ArrayList<>();

  public int getId() {
    return id;
  }

  public List<Owned> getOwnedList() {
    return ownedList;
  }
}
