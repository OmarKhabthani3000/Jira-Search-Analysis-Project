package com.example.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "my_test_entity")
public class MyTestEntity implements Serializable
{

    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer myTestInteger;
    private MyTestEmbeddable myTestEmbeddable;

    public MyTestEntity()
    {
    }

    public MyTestEntity(final Long id, final Integer myTestInteger, final MyTestEmbeddable myTestEmbeddable)
    {
        this.id = id;
        this.myTestInteger = myTestInteger;
        this.myTestEmbeddable = myTestEmbeddable;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    public Long getId()
    {
        return this.id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    @Column(name = "my_test_integer", nullable = true)
    public Integer getMyTestInteger()
    {
        return this.myTestInteger;
    }

    public void setMyTestInteger(final Integer myTestInteger)
    {
        this.myTestInteger = myTestInteger;
    }

    @Embedded
    public MyTestEmbeddable getMyTestEmbeddable()
    {
        return myTestEmbeddable;
    }

    public void setMyTestEmbeddable(final MyTestEmbeddable myTestEmbeddable)
    {
        this.myTestEmbeddable = myTestEmbeddable;
    }

}
