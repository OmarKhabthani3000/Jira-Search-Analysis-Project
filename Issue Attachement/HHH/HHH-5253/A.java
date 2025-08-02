package priv.ubu.hibernate.tests;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="A")
public class A {
   private Long id;
   private String name;
   
   @Id @GeneratedValue(generator="hibseq")
   @org.hibernate.annotations.GenericGenerator(name="hibseq", strategy = "hilo", 
         parameters = {
         @org.hibernate.annotations.Parameter(name="table", value = "ENTITY_HI_VALUES"),
         @org.hibernate.annotations.Parameter(name="column", value = "HI"),
         @org.hibernate.annotations.Parameter(name="max_lo", value = "3")
   }
   )
   public final Long getId() {
      return id;
   }
   public final void setId(Long id) {
      this.id = id;
   }
   public final String getName() {
      return name;
   }
   public final void setName(String name) {
      this.name = name;
   }
}
