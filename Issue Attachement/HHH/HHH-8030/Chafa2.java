package test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chafa2")
public class Chafa2 {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "addressId")
  private int id;
}
