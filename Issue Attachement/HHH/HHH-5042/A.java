import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="A")
public class A {
   private Long id;
   private String name;
   
   /**
    * @return the id
    */
   @Id @GeneratedValue(generator="hibseq")
   @org.hibernate.annotations.GenericGenerator(name="hibseq", strategy = "hilo", 
         parameters = {
         @org.hibernate.annotations.Parameter(name="table", value = "ENTITY_HI_VALUES"),
         @org.hibernate.annotations.Parameter(name="column", value = "HI"),
         @org.hibernate.annotations.Parameter(name="max_lo", value = "5")
   }
   )
   public final Long getId() {
      return id;
   }
   /**
    * @param id the id to set
    */
   public final void setId(Long id) {
      this.id = id;
   }
   /**
    * @return the name
    */
   public final String getName() {
      return name;
   }
   /**
    * @param name the name to set
    */
   public final void setName(String name) {
      this.name = name;
   }
}
