package hib;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TABLE_B")
public class EntityB {
  @Id
  long tableBId;
}
