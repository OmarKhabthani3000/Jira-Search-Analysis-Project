package ru.arptek.arpsite.data.jpa.single;

import javax.persistence.Table;

import ru.arptek.arpsite.data.jpa.AbstractJPAEntity;
import ru.arptek.arpsite.data.jpa.Database;

@javax.persistence.Entity
@Database("test")
@Table(name = "entities")
public class Entity extends AbstractJPAEntity {
    public String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
