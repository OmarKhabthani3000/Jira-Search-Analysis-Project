package com.example.demo;

import java.io.Serializable;
import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "DATA")
public class TestData implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private Long              id;

    private Integer           type;

    private String            expTimest;

    @Lob
    private Blob              data;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String getExpTimest()
    {
        return expTimest;
    }

    public void setExpTimest(String expTimest)
    {
        this.expTimest = expTimest;
    }

    public Blob getData()
    {
        return data;
    }

    public void setData(Blob data)
    {
        this.data = data;
    }

}
