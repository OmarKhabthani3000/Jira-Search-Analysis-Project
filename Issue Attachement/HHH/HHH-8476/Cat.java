package com.company.product.domain;

import java.util.Set;

public class Cat
{
    private Long id;

    private String name;

    private Set<Person> owners;

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

    public Set<Person> getOwners()
    {
        return owners;
    }

    public void setOwners(Set<Person> aOwners)
    {
        owners = aOwners;
    }
}
