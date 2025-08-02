package com.example.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MyTestEmbeddable implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String myTestString;

    public MyTestEmbeddable()
    {
    }

    @Column(name = "my_test_string")
    public String getMyTestString()
    {
        return this.myTestString;
    }

    public void setMyTestString(final String myTestString)
    {
        this.myTestString = myTestString;
    }

}
