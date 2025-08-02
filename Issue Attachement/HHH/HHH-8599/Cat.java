package com.company.product.domain;

import java.util.Map;
import java.util.Set;

public class Cat
{
    private Long id;

    private String name;

    private Set<Person> owners;

    private Map<Integer, Integer> map;

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

    public Map<Integer, Integer> getMap()
    {
        return map;
    }
    
    public void setMap(Map<Integer, Integer> aMap)
    {
        map = aMap;
    }
}
