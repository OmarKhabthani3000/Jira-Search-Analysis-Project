package com.example.querytest;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MyEntity {

    @Id
    Long id;

    MyEnum something;

    public enum MyEnum {
        A, B, C
    }
}
