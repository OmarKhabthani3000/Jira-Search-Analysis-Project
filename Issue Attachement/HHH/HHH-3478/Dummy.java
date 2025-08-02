package com.autodesk.lbs.cs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SequenceGenerator (name="DUMMY_SEQ", sequenceName="DUMMY_SEQ")
public class Dummy {

    private Long id;
    private String name;

    public Dummy(String name) {
        setName(name);
    }

    @Id
    @GeneratedValue(generator = "DUMMY_SEQ")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NaturalId(mutable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
