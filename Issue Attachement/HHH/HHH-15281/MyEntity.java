package org.hibernate.bugs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="MY_ENTITY")
public class MyEntity {

  @Id
  @GeneratedValue(generator = "ID_GEN")
  @GenericGenerator(name = "ID_GEN",
      strategy = "org.hibernate.bugs.MyIdGenerator")
  private String oid;

  @Column
  private String content;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
