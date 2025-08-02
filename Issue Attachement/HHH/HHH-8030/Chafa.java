package test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "chafa")
public class Chafa {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "personId")
  private int id;

  @ManyToOne(optional=false)
  @JoinTable(name="PersonAddress",
    joinColumns = {
      @JoinColumn(name="personId", unique = true)           
    },
    inverseJoinColumns = {
      @JoinColumn(name="addressId")
    }     
  )
  private Chafa2 address;
}
