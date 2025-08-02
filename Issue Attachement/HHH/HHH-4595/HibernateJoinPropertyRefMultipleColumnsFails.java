package org.wfp.rita.datafacade;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Hibernate fails to join one table to another's non-primary key fields
 * @author chris
 */
public class HibernateJoinPropertyRefMultipleColumnsFails extends TestCase
{
    @Entity
    @Table(name="project")
    public class Project 
    {
        @Id
        public Integer id;
        
        @OneToMany
        @JoinColumn(name="project_id")
        public Set<ProjectSite> projectSites = new HashSet<ProjectSite>(0);
        
        @Transient
        public Set<Site> sites = new HashSet<Site>(0);
    }
    
    @Entity
    @Table(name="site")
    public class Site
    {
        @Id
        public Integer id;
        
        @OneToMany
        @JoinColumn(name="site_id")
        public Set<ProjectSite> projectSites = new HashSet<ProjectSite>(0);
    }
    
    @Entity
    @Table(name="project_site", uniqueConstraints={
        @UniqueConstraint(columnNames={"project_id", "site_id"})
    })
    public class ProjectSite
    {
        @Id
        @Column(name="id")
        public Integer id;
        
        @ManyToOne
        @JoinColumn(name="project_id")
        public Project project;
        
        @ManyToOne
        @JoinColumn(name="site_id")
        public Site site;
        
        @Column(name="project_id", updatable=false, insertable=false)
        public Integer projectId;
        
        @Column(name="site_id", updatable=false, insertable=false)
        public Integer siteId;
        
        @Transient
        public Set<UserRole> userAuthoritiesByProject;
        
        @Transient
        public Set<UserRole> userAuthoritiesByProjectAndSite;
    }
    
    @Entity
    @Table(name="user_roles", uniqueConstraints={
        @UniqueConstraint(columnNames={"user_name", "project_id", "site_id",
            "role"})
    })
    public class UserRole
    {
        @Id
        @Column(name="id")
        public Integer id;

        @Column(name="user_name")
        public String user;
        
        public String role;
        
        @ManyToOne
        @JoinColumn(name="project_id", updatable=false, insertable=false)
        public Project project; // read only, use setProjectSite() instead 
        
        @ManyToOne
        @JoinColumn(name="site_id", updatable=false, insertable=false)
        public Site site; // read only, use setProjectSite() instead
        
        @ManyToOne
        @JoinColumns({
            @JoinColumn(name="project_id", nullable=false, updatable=false,
                insertable=false, referencedColumnName="project_id"),
            @JoinColumn(name="site_id", nullable=false, updatable=false,
                insertable=false, referencedColumnName="site_id")
        })
        public ProjectSite projectSite;
    }
    
    public void test()
    throws Exception
    {
        AnnotationConfiguration conf = new AnnotationConfiguration();

        conf.addAnnotatedClass(Project.class);
        conf.addAnnotatedClass(Site.class);
        conf.addAnnotatedClass(ProjectSite.class);
        conf.addAnnotatedClass(UserRole.class);

        conf.setProperty("hibernate.connection.driver_class",
            "org.apache.derby.jdbc.EmbeddedDriver");
        conf.setProperty("hibernate.connection.url",
            "jdbc:derby:/tmp/illegal-constraints.derby;create=true");
        conf.setProperty("hibernate.connection.username", "root");
        conf.setProperty("hibernate.connection.password", "");
        conf.setProperty("hibernate.dialect",
            "org.wfp.rita.db.FixedDerbyDialect");
        SessionFactory fact = conf.buildSessionFactory();
        
        SchemaExport exporter = new SchemaExport(conf,
            ((SessionFactoryImpl)fact).getSettings());

        exporter.setHaltOnError(false);
        exporter.execute(true, true, true, false);

        exporter.setHaltOnError(true);
        exporter.execute(true, true, false, true);

        for (Object e : exporter.getExceptions())
        {
            throw (Exception) e;
        }
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HibernateJoinPropertyRefMultipleColumnsFails.class);
    }
}
