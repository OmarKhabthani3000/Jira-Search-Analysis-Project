package com.test.hibernate.model;


import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class User implements Serializable {
	@EmbeddedId
	private UserId codeObject = new UserId();
    private String name;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({
			@JoinColumn(name = "car_identifier_", referencedColumnName = "identifier_", unique = false, nullable = false) })
	@MapsId("car")

	private Car car;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
