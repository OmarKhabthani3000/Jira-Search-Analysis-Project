package com.company.product.domain;

public class Cat extends Animal
{
    private String color;

    private Person owner;

    public String getColor()
    {
        return color;
    }

    public void setColor(String aColor)
    {
        color = aColor;
    }

    public Person getOwner()
    {
        return owner;
    }

    public void setOwner(Person anOwner)
    {
        owner = anOwner;
    }
}
