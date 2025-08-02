import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MyEntity
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column
   private String name;

   public int getId()
   {
      return id;
   }

   public MyEntity setId(int id)
   {
      this.id = id;
      return this;
   }

   public String getName()
   {
      return name;
   }

   public MyEntity setName(String name)
   {
      this.name = name;
      return this;
   }
}
