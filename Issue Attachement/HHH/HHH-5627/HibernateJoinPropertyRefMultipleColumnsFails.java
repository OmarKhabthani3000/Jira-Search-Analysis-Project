package org.wfp.rita.test.hibernate;

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

/**
 * Hibernate Annotations sometimes fails to map multiple columns to a
 * non-primary unique key in another table, when that table is defined
 * in an HBM file rather than an annotated class. This is a test case
 * for the problem. The resulting error is:
 * 
 * org.hibernate.AnnotationException:
 * referencedColumnNames(project_id, site_id) of UserRole.projectSite
 * referencing ProjectSite not mapped to a single property
 * 
 * https://forum.hibernate.org/viewtopic.php?f=1&t=1001083
 * 
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
    
    public class ProjectSite
    {
        public Integer id;
        public Project project;
        public Site site;
        public Integer projectId;
        public Integer siteId;
        public Set<UserRole> userAuthoritiesByProject;
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
        conf.addClass(ProjectSite.class);
        conf.addAnnotatedClass(UserRole.class);
        SessionFactory fact = conf.buildSessionFactory();
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HibernateJoinPropertyRefMultipleColumnsFails.class);
    }
}
