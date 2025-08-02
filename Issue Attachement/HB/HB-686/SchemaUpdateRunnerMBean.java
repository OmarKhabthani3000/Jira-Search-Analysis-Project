package net.sf.hibernate.jmx;

import javax.management.ObjectName;

public interface SchemaUpdateRunnerMBean {

  void start();

  void setHibernateService(ObjectName service);

}
