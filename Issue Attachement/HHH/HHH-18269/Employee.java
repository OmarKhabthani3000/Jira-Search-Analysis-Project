package test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data // Lombok annotation to generate getters, setters, toString, etc.
@Entity // JPA annotation to make this object ready for storage in a JPA-based data store
public class Employee {

    @Id
    private Long id;

    private String name;

    private Timestamp lastModifiedAt;
}