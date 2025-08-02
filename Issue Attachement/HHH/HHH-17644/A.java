package cz.kula.example.testdemo.db.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("child")
public class A extends B<Void> {
}
