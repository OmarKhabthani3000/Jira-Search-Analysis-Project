package com.attensa.core.entity;


import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Proxy;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.Log4jConfigurer;


public class OneToOneLoadTest {
	
  private static final Logger logger = Logger.getLogger(OneToOneLoadTest.class);
  
  
  public static final Long ID_OWNER_ENTITY = 1111L;
  public static final String UNEXPECTED_QUERY = 
        "select owned0_.id as id1_0_ from Owned owned0_ where owned0_.id=?";
  
  
  @Before
  public void setup() {
	  try {
		Log4jConfigurer.initLogging("classpath:log4j.properties");
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
  }
  
  @Test
  public void testOneToOneById() throws Exception {
    SessionFactory sessionFactory = new AnnotationConfiguration()
    .addAnnotatedClass(Owner.class)
    .addAnnotatedClass(Owned.class)
    .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
    .buildSessionFactory();
    
    final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
    final ResultSet resultSet = Mockito.mock(ResultSet.class);

    final Connection connection = Mockito.mock(Connection.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenAnswer(new Answer<PreparedStatement>() {
      @Override
      public PreparedStatement answer(InvocationOnMock invocation) throws Throwable {
        if (UNEXPECTED_QUERY.equals(invocation.getArguments()[0])) {
          System.out.println("Unexpected SQL: " + invocation.getArguments()[0]);
          throw new RuntimeException("Owned entity is should not be eagerly loaded.");
        } else {
          System.out.println("Expected SQL: " + invocation.getArguments()[0]);
          return preparedStatement;
        }
      }
    });
    Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
    Mockito.when(resultSet.next()).thenReturn(true, false);
    Mockito.when(resultSet.getLong(0)).thenReturn(1l);
    Mockito.when(resultSet.getLong("id0_0_")).thenReturn(1l);

    
    Session session = sessionFactory.openSession(connection);
    
    Owner owner = (Owner) session.load(Owner.class, ID_OWNER_ENTITY);
    
    owner.hashCode();
    owner.getOwned().hashCode();

    Mockito.verify(connection).prepareStatement(Mockito.anyString());
    Mockito.verify(preparedStatement).setLong(1, ID_OWNER_ENTITY.longValue());
    Mockito.verify(connection, new Times(2)).isClosed();
    Mockito.verify(connection, new Times(2)).getAutoCommit();
    Mockito.verifyZeroInteractions(connection);
  }
}



@Entity
@Proxy(lazy=false)
class Owner {
	
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public java.lang.Long getId() {
    return this.id;
  }
  
  public void setId(java.lang.Long newValue) {
    this.id = newValue;
  }
  
  private java.lang.Long id;
  
  @OneToOne(optional=false, cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch=FetchType.LAZY)
  @LazyToOne(LazyToOneOption.PROXY)
  @javax.persistence.PrimaryKeyJoinColumn
  public Owned getOwned() {
    return this.owned;
  }
  
  public void setOwned(Owned owned) {
    this.owned = owned;
    if (owned != null) {
      owned.setOwner(this);
    }
  }
  
  private Owned owned;
}


@Entity
@Proxy(lazy=false)
class Owned {
  private java.lang.Long id;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public java.lang.Long getId() {
    return this.id;
  }
  
  public void setId(java.lang.Long newValue) {
    this.id = newValue;
  }
  
  @OneToOne(mappedBy="owned", fetch=FetchType.LAZY)
  @LazyToOne(LazyToOneOption.PROXY)
  public Owner getOwner() {
    return this.owner;
  }
  
  public void setOwner(Owner owner) {
    this.owner = owner;
  }
  
  private Owner owner;
}


