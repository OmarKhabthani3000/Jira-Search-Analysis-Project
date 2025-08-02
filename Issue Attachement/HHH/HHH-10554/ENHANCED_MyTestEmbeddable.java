package com.example.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import org.hibernate.bytecode.enhance.internal.tracker.CompositeOwnerTracker;
import org.hibernate.engine.spi.CompositeOwner;
import org.hibernate.engine.spi.CompositeTracker;
import org.hibernate.engine.spi.ManagedComposite;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

@Embeddable
public class MyTestEmbeddable
implements Serializable,
ManagedComposite,
PersistentAttributeInterceptable,
CompositeTracker {
    private static final long serialVersionUID = 1;
    private String myTestString;
    @Transient
    private transient PersistentAttributeInterceptor $$_hibernate_attributeInterceptor;
    @Transient
    private transient CompositeOwnerTracker $$_hibernate_compositeOwners;

    @Column(name="my_test_string")
    public String getMyTestString() {
        return this.$$_hibernate_read_myTestString();
    }

    public void setMyTestString(String myTestString) {
        this.$$_hibernate_write_myTestString(myTestString);
    }

    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return this.$$_hibernate_attributeInterceptor;
    }

    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.$$_hibernate_attributeInterceptor = persistentAttributeInterceptor;
    }

    public void $$_hibernate_setOwner(String string, CompositeOwner compositeOwner) {
        if (this.$$_hibernate_compositeOwners == null) {
            this.$$_hibernate_compositeOwners = new CompositeOwnerTracker();
        }
        this.$$_hibernate_compositeOwners.add(string, compositeOwner);
    }

    public void $$_hibernate_clearOwner(String string) {
        if (this.$$_hibernate_compositeOwners != null) {
            this.$$_hibernate_compositeOwners.removeOwner(string);
        }
    }

    public String $$_hibernate_read_myTestString() {
        if (this.$$_hibernate_getInterceptor() != null) {
            this.myTestString = (String)this.$$_hibernate_getInterceptor().readObject((Object)this, "myTestString", (Object)this.myTestString);
        }
        return this.myTestString;
    }

    public void $$_hibernate_write_myTestString(String string) {
        if (this.$$_hibernate_compositeOwners != null) {
            this.$$_hibernate_compositeOwners.callOwner("");
        }
        String string2 = string;
        if (this.$$_hibernate_getInterceptor() != null) {
            string2 = (String)this.$$_hibernate_getInterceptor().writeObject((Object)this, "myTestString", (Object)this.myTestString, (Object)string);
        }
        this.myTestString = string2;
    }
}
