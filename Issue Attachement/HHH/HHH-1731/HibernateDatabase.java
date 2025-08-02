package com.implior.stargate;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.SessionFactory;

import javax.servlet.ServletContext;

// Stores all stuff related to a Hibernate database

public class HibernateDatabase {

   public final SessionFactory sessionFactory;
   // Use the magic ThreadLocal to implement tread-local session/trans objects
   public final ThreadLocal session = new ThreadLocal();
   public final ThreadLocal transaction = new ThreadLocal();

   public HibernateDatabase(ServletContext ctx, String cfgFileName)
   {
      Configuration configuration = new Configuration()
                                    .configure(cfgFileName);

      // Get the JDBC parameters by using ServletContext.getInitParameter(),
      // not ServletConfig.getInitParameter(). Then the parameter can
      // be overridden in stargate.xml of each virtual host, without
      // changing each copy of WEB-INF/web.xml. This is practical
      // in general, and especially when/if we use virtual hosts.

      String dbUrl    = translateAndSet(ctx, configuration, "connection.url");
      String userName = translateAndSet(ctx, configuration, "connection.username");
      String password = translateAndSet(ctx, configuration, "connection.password");

      boolean dbExisted = Utils.ensureDb(dbUrl, userName, password);
      if (!dbExisted)
         new SchemaExport(configuration).create(false,true);

      // Place population SQL here

       sessionFactory = configuration.buildSessionFactory();
   }

   private String translateAndSet(ServletContext ctx,
                                  Configuration configuration,
                                  String hibernateParam)
   {
      // Translate e.g. "STARGATE-JDBC-URL"
      // to "jdbc:mysql://localhost/stargate"

      String s1;
      String s2;

      s1 = configuration.getProperty(hibernateParam);
      // s2 = ctx.getInitParameter(s1); // This is what I *really* want to do
      s2 = s1;
      if (s1.equals("jdbc:mysql://localhost/stargate")) // bug test
         s2 = "jdbc:mysql://localhost/stargate_foo";

      configuration.setProperty(hibernateParam, s2);

      String result = configuration.getProperty(hibernateParam);
      return result;
   }
}
