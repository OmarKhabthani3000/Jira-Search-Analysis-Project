
package de.hhla.zeus.domain.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Foo {

  private String id;

  /* This works if field id is annotated instead!!! */
  @Id
  private UUID uuid;

  Foo() {
  }

  public Foo(String id, UUID uuid) {
    this.id = id;
    this.uuid = uuid;
  }

  public String getId() {
    return id;
  }

  public UUID getUuid() {
    return uuid;
  }
}
