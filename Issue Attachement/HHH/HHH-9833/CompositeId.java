package org.hibernate.sample;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CompositeId implements Serializable {
    private Long key1;
    private Long key2;

    public CompositeId() {
    }

    public CompositeId(Long key1, Long key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    public Long getKey1() {
        return key1;
    }

    private void setKey1(Long key1) {
        this.key1 = key1;
    }

    public Long getKey2() {
        return key2;
    }

    private void setKey2(Long key2) {
        this.key2 = key2;
    }
}
