package de.dzbank.iam.api.repository.db.iam;

import javax.persistence.*;
import java.util.Date;

@Entity
public class EntityExample {
  @Id
  @GeneratedValue
  private Long id;

  @Temporal(TemporalType.TIMESTAMP )
  private Date createdOn;

  private String description;

  // may be duplicate on some entries; we don't want those
  private String duplicate;
}
