package com.hibers5.ex;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

public class Hiber5Test {

    static SessionFactory sessFactory;
    static {
        StandardServiceRegistry registry = null;
        try{ 
            System.out.println("HHHH");  
            
            registry = new StandardServiceRegistryBuilder()
                   .configure()
                   .applySetting(AvailableSettings.SHOW_SQL, true)
                   .applySetting(AvailableSettings.FORMAT_SQL, true)                   
                   .build();
            System.out.println("HHHH"+registry);  
               
               
         sessFactory = new MetadataSources(registry)
                       .addAnnotatedClass(Hib5Emp.class)
                       .buildMetadata()  // will give Metadata
                       .buildSessionFactory();
         System.out.println(sessFactory);
        }catch(Exception ex){
            StandardServiceRegistryBuilder.destroy(registry);      
         }         
       }
    public Session getSession() {
        return sessFactory.openSession();
    }       
    
    
    public boolean saveEmployee(int empId, String name, String jb, double sal, String hDate,String dName) {        
      boolean ret = false;
      Job job = Job.valueOf(jb.toUpperCase());
      Dept dept = Dept.valueOf(dName.toUpperCase());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate hiredate = LocalDate.parse(hDate, formatter);
      Hib5Emp emp = new Hib5Emp(empId, name, job, sal, hiredate, dept);
      Session session = this.getSession();
      Transaction trans = session.beginTransaction();
      try{
         Serializable id = session.save(emp);
         if(id != null){
           ret = true;
           trans.commit();   
         }
      }catch(Exception ex){
          ex.printStackTrace();
          trans.rollback();
      }
      session.close();
      return ret;
    }
    
    public void close(){
       sessFactory.close();
    }
    
    public static void main(String[] args) {
      Hiber5Test hibTest = new Hiber5Test();
      boolean boo = hibTest.saveEmployee(101, "santosh kumar", "tech_lead", 34500.5, "2011-03-23", "production");
      System.out.println("Record inserted is " + boo); 
      hibTest.close();
    }

}
