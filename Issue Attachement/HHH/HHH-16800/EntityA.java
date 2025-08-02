package hib;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TABLE_A")
public class EntityA {
  @Id
  long tableAId;


  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "tableBId", referencedColumnName = "tableBId")
  EntityB entityB;
}
