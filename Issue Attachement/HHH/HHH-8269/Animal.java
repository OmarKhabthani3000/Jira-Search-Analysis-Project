package com.company.product.domain;

abstract public class Animal
{
    private Long id;

    private Person owner;

    public Long getId()
    {
        return id;
    }

    public void setId(Long anId)
    {
        id = anId;
    }

    public Person getOwner()
    {
        return owner;
    }

    public void setOwner(Person aPerson)
    {
        owner = aPerson;
    }
}
