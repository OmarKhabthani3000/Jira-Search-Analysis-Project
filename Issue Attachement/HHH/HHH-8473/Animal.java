package com.company.product.domain;

abstract public class Animal
{
    private Long id;

    private String name;

    private Integer age;

    public Long getId()
    {
        return id;
    }

    public void setId(Long anId)
    {
        id = anId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String aName)
    {
        name = aName;
    }
    
    public Integer getAge()
    {
        return age;
    }
    
    public void setAge(Integer anAge)
    {
        age = anAge;
    }
}
