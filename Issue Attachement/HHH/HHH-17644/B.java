package cz.kula.example.testdemo.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import static jakarta.persistence.InheritanceType.SINGLE_TABLE;
import static org.hibernate.type.SqlTypes.JSON;

@Entity
@Table(name = "table")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "demo_type")
public abstract class B<T> {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Nullable
    @Column(name = "payload")
    @JdbcTypeCode(JSON)
    private T payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public T getPayload() {
        return payload;
    }

    public void setPayload(@Nullable T payload) {
        this.payload = payload;
    }
}
