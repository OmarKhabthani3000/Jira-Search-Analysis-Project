package com.example.model;

import com.example.model.MyTestEmbeddable;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;
import org.hibernate.bytecode.enhance.internal.tracker.SimpleCollectionTracker;
import org.hibernate.bytecode.enhance.internal.tracker.SimpleFieldTracker;
import org.hibernate.bytecode.enhance.spi.CollectionTracker;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
import org.hibernate.engine.spi.CompositeOwner;
import org.hibernate.engine.spi.CompositeTracker;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.internal.util.compare.EqualsHelper;

@Entity
@Table(name="my_test_entity")
public class MyTestEntity
implements Serializable,
ManagedEntity,
PersistentAttributeInterceptable,
SelfDirtinessTracker,
CompositeOwner {
    private static final long serialVersionUID = 1;
    private Long id;
    private Integer myTestInteger;
    private MyTestEmbeddable myTestEmbeddable;
    @Transient
    private transient EntityEntry $$_hibernate_entityEntryHolder;
    @Transient
    private transient ManagedEntity $$_hibernate_previousManagedEntity;
    @Transient
    private transient ManagedEntity $$_hibernate_nextManagedEntity;
    @Transient
    private transient PersistentAttributeInterceptor $$_hibernate_attributeInterceptor;
    @Transient
    private transient DirtyTracker $$_hibernate_tracker;
    @Transient
    private transient CollectionTracker $$_hibernate_collectionTracker;

    public MyTestEntity() {
    }

    public MyTestEntity(Long id, Integer myTestInteger, MyTestEmbeddable myTestEmbeddable) {
        this.$$_hibernate_write_id(id);
        this.$$_hibernate_write_myTestInteger(myTestInteger);
        this.$$_hibernate_write_myTestEmbeddable(myTestEmbeddable);
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=1, nullable=0, updatable=0)
    public Long getId() {
        return this.$$_hibernate_read_id();
    }

    public void setId(Long id) {
        this.$$_hibernate_write_id(id);
    }

    @Column(name="my_test_integer", nullable=1)
    public Integer getMyTestInteger() {
        return this.$$_hibernate_read_myTestInteger();
    }

    public void setMyTestInteger(Integer myTestInteger) {
        this.$$_hibernate_write_myTestInteger(myTestInteger);
    }

    @Embedded
    public MyTestEmbeddable getMyTestEmbeddable() {
        return this.$$_hibernate_read_myTestEmbeddable();
    }

    public void setMyTestEmbeddable(MyTestEmbeddable myTestEmbeddable) {
        this.$$_hibernate_write_myTestEmbeddable(myTestEmbeddable);
    }

    public Object $$_hibernate_getEntityInstance() {
        return this;
    }

    public EntityEntry $$_hibernate_getEntityEntry() {
        return this.$$_hibernate_entityEntryHolder;
    }

    public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
        this.$$_hibernate_entityEntryHolder = entityEntry;
    }

    public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
        return this.$$_hibernate_previousManagedEntity;
    }

    public void $$_hibernate_setPreviousManagedEntity(ManagedEntity managedEntity) {
        this.$$_hibernate_previousManagedEntity = managedEntity;
    }

    public ManagedEntity $$_hibernate_getNextManagedEntity() {
        return this.$$_hibernate_nextManagedEntity;
    }

    public void $$_hibernate_setNextManagedEntity(ManagedEntity managedEntity) {
        this.$$_hibernate_nextManagedEntity = managedEntity;
    }

    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return this.$$_hibernate_attributeInterceptor;
    }

    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.$$_hibernate_attributeInterceptor = persistentAttributeInterceptor;
    }

    public void $$_hibernate_trackChange(String string) {
        if (this.$$_hibernate_tracker == null) {
            this.$$_hibernate_tracker = new SimpleFieldTracker();
        }
        this.$$_hibernate_tracker.add(string);
    }

    private boolean $$_hibernate_areCollectionFieldsDirty() {
        if (this.$$_hibernate_collectionTracker == null) {
            return false;
        }
        return false;
    }

    private void $$_hibernate_getCollectionFieldDirtyNames(DirtyTracker dirtyTracker) {
        if (this.$$_hibernate_collectionTracker == null) {
            return;
        }
    }

    private void $$_hibernate_clearDirtyCollectionNames() {
        if (this.$$_hibernate_collectionTracker == null) {
            this.$$_hibernate_collectionTracker = new SimpleCollectionTracker();
        }
        LazyAttributeLoader lazyAttributeLoader = null;
        if (this.$$_hibernate_attributeInterceptor != null && this.$$_hibernate_attributeInterceptor instanceof LazyAttributeLoader) {
            lazyAttributeLoader = (LazyAttributeLoader)this.$$_hibernate_attributeInterceptor;
        }
    }

    public String[] $$_hibernate_getDirtyAttributes() {
        if (this.$$_hibernate_collectionTracker == null) {
            return this.$$_hibernate_tracker == null ? new String[]{} : this.$$_hibernate_tracker.get();
        }
        if (this.$$_hibernate_tracker == null) {
            this.$$_hibernate_tracker = new SimpleFieldTracker();
        }
        this.$$_hibernate_getCollectionFieldDirtyNames(this.$$_hibernate_tracker);
        return this.$$_hibernate_tracker.get();
    }

    public boolean $$_hibernate_hasDirtyAttributes() {
        return this.$$_hibernate_tracker != null && !this.$$_hibernate_tracker.isEmpty() || this.$$_hibernate_areCollectionFieldsDirty();
    }

    public void $$_hibernate_clearDirtyAttributes() {
        if (this.$$_hibernate_tracker != null) {
            this.$$_hibernate_tracker.clear();
        }
        this.$$_hibernate_clearDirtyCollectionNames();
    }

    public void $$_hibernate_suspendDirtyTracking(boolean bl) {
        if (this.$$_hibernate_tracker == null) {
            this.$$_hibernate_tracker = new SimpleFieldTracker();
        }
        this.$$_hibernate_tracker.suspend(bl);
    }

    public CollectionTracker $$_hibernate_getCollectionTracker() {
        return this.$$_hibernate_collectionTracker;
    }

    public Long $$_hibernate_read_id() {
        if (this.$$_hibernate_getInterceptor() != null) {
            this.id = (Long)this.$$_hibernate_getInterceptor().readObject((Object)this, "id", (Object)this.id);
        }
        return this.id;
    }

    public void $$_hibernate_write_id(Long l) {
        Long l2 = l;
        if (this.$$_hibernate_getInterceptor() != null) {
            l2 = (Long)this.$$_hibernate_getInterceptor().writeObject((Object)this, "id", (Object)this.id, (Object)l);
        }
        this.id = l2;
    }

    public Integer $$_hibernate_read_myTestInteger() {
        if (this.$$_hibernate_getInterceptor() != null) {
            this.myTestInteger = (Integer)this.$$_hibernate_getInterceptor().readObject((Object)this, "myTestInteger", (Object)this.myTestInteger);
        }
        return this.myTestInteger;
    }

    public void $$_hibernate_write_myTestInteger(Integer n) {
        if (!EqualsHelper.areEqual((Object)this.myTestInteger, (Object)n)) {
            this.$$_hibernate_trackChange("myTestInteger");
        }
        Integer n2 = n;
        if (this.$$_hibernate_getInterceptor() != null) {
            n2 = (Integer)this.$$_hibernate_getInterceptor().writeObject((Object)this, "myTestInteger", (Object)this.myTestInteger, (Object)n);
        }
        this.myTestInteger = n2;
    }

    public MyTestEmbeddable $$_hibernate_read_myTestEmbeddable() {
        if (this.$$_hibernate_getInterceptor() != null) {
            this.myTestEmbeddable = (MyTestEmbeddable)this.$$_hibernate_getInterceptor().readObject((Object)this, "myTestEmbeddable", (Object)this.myTestEmbeddable);
        }
        return this.myTestEmbeddable;
    }

    public void $$_hibernate_write_myTestEmbeddable(MyTestEmbeddable myTestEmbeddable) {
        if (this.myTestEmbeddable != null) {
            ((CompositeTracker)this.myTestEmbeddable).$$_hibernate_clearOwner("myTestEmbeddable");
        }
        if (!EqualsHelper.areEqual((Object)this.myTestEmbeddable, (Object)myTestEmbeddable)) {
            this.$$_hibernate_trackChange("myTestEmbeddable");
        }
        MyTestEmbeddable myTestEmbeddable2 = myTestEmbeddable;
        if (this.$$_hibernate_getInterceptor() != null) {
            myTestEmbeddable2 = (MyTestEmbeddable)this.$$_hibernate_getInterceptor().writeObject((Object)this, "myTestEmbeddable", (Object)this.myTestEmbeddable, (Object)myTestEmbeddable);
        }
        this.myTestEmbeddable = myTestEmbeddable2;
        Object var4_3 = null;
        ((CompositeTracker)this.myTestEmbeddable).$$_hibernate_setOwner("myTestEmbeddable", (CompositeOwner)this);
        this.$$_hibernate_trackChange("myTestEmbeddable");
    }
}
