package org.hibernate.bugs;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class MyIdGenerator implements IdentifierGenerator {

  @Override
  public String generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o)
      throws HibernateException {
    return UUID.randomUUID().toString();
  }
}
