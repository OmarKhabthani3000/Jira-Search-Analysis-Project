package org.raju.yadav.entity;

import java.io.Serializable;

public interface Entity<T extends Serializable> extends Serializable {

    /**
     * need to be overridden in order to specify {@link GeneratedValue} and
     * column id name
     *
     * @return id
     */
    T getId();
}