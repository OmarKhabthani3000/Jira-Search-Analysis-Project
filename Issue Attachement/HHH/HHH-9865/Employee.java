package de.juplo.plugins.hibernate4;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ForeignKey;

/**
 *
 * 
 */
@Entity
public class Employee {

    @ManyToOne(optional = true)
    @ForeignKey(name = "none")
    private Employee manager;

    @EmbeddedId
    private EmployeeId pk;

    @OneToMany(cascade = { CascadeType.REMOVE }, mappedBy = "manager")
    @ForeignKey(name = "none")
    private List<Employee> directReports;

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public Employee getManager() {
        return manager;
    }

    public void setDirectReports(List<Employee> directReports) {
        this.directReports = directReports;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void setPk(EmployeeId pk) {
        this.pk = pk;
    }

    public EmployeeId getPK() {
        return pk;
    }
}
