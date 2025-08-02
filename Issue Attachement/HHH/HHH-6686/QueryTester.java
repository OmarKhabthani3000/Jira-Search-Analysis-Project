package de.gedoplan.buch.eedemos.entity;

import de.gedoplan.buch.eedemos.util.JpaUtil;

import javax.persistence.EntityManager;

public class QueryTester
{
  public static void main(String[] args)
  {
    EntityManager em = JpaUtil.createEntityManager();

    StringBuilder jpql = new StringBuilder();
    for (String arg : args)
    {
      jpql.append(arg).append(' ');
    }

    for (Object result : em.createQuery(jpql.toString()).getResultList())
    {
      if (result instanceof Object[])
      {
        char delim = '[';
        for (Object entry : (Object[]) result)
        {
          System.out.print(delim);
          System.out.print(entry + "(" + entry.getClass() + ")");
          delim = ',';
        }
        System.out.println("]");
      }
      else
      {
        System.out.println(result + "(" + result.getClass() + ")");
      }
    }

    em.close();

  }
}
