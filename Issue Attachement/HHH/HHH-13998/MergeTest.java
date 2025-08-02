package org.hibernate.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.hibernate.test.entity.Owned;
import org.hibernate.test.entity.Owner;
import org.junit.Before;
import org.junit.Test;

public class MergeTest {
  private EntityManager entityManager;

  @Before
  public void setUp() throws Exception {
    entityManager = Persistence.createEntityManagerFactory("Test").createEntityManager();
  }

  @Test
  public void test() throws Exception {
    Owner owner = new Owner();
    owner.getOwnedList().add(new Owned());
    owner.getOwnedList().add(new Owned());
    owner.getOwnedList().get(0).setName("Update");
    owner.getOwnedList().get(1).setName("Delete");

    owner = merge(owner);

    assertEquals(2, entityManager.find(Owner.class, owner.getId()).getOwnedList().size());
    assertEquals(
        "Update", entityManager.find(Owner.class, owner.getId()).getOwnedList().get(0).getName());
    assertEquals(
        "Delete", entityManager.find(Owner.class, owner.getId()).getOwnedList().get(1).getName());

    Owner ownerSnapshot = entityManager.find(Owner.class, owner.getId());
    entityManager.detach(ownerSnapshot);

    owner.getOwnedList().get(0).setName("Updated");
    owner.getOwnedList().remove(1);
    owner.getOwnedList().add(new Owned());
    owner.getOwnedList().get(1).setName("Inserted");

    owner = merge(owner);

    assertEquals(2, entityManager.find(Owner.class, owner.getId()).getOwnedList().size());
    assertEquals(
        "Updated", entityManager.find(Owner.class, owner.getId()).getOwnedList().get(0).getName());
    assertEquals(
        "Inserted", entityManager.find(Owner.class, owner.getId()).getOwnedList().get(1).getName());

    // Restore snapshot
    owner = merge(ownerSnapshot);

    assertEquals(2, entityManager.find(Owner.class, owner.getId()).getOwnedList().size());
    assertEquals(
        "Update", entityManager.find(Owner.class, owner.getId()).getOwnedList().get(0).getName());
    assertEquals(
        "Delete", entityManager.find(Owner.class, owner.getId()).getOwnedList().get(1).getName());
  }

  private <T> T merge(T entity) {
    entityManager.getTransaction().begin();

    try {
      entity = entityManager.merge(entity);
      entityManager.getTransaction().commit();
      entityManager.detach(entity);

      return entity;
    } catch (RuntimeException e) {
      entityManager.getTransaction().rollback();

      throw e;
    }
  }
}
