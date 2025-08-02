/*
 *  © 2019 RÖPERWEISE GmbH, Germany. All rights reserved.
 */

package de.roeperweise.compid;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

/**
 *
 * @author Stefan Röper, RÖPERWEISE GmbH.
 */
@Entity
@IdClass(EmployeeId.class)
public class Employee {
  @Id
  private short id;
  
  @Id
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_Employee_Department"), nullable = false)
  private Department department;
  private String empName;
  @Version
  private Integer version;

  public Employee() {
  }

  public Employee(String empName, Department department) {
    this.id = department.incAndGetNbOfEmployees();
    this.empName = empName;
    this.department = department;
  }

  public short getId() {
    return id;
  }

  public String getEmpName() {
    return empName;
  }

  public void setEmpName(String empName) {
    this.empName = empName;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Integer getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return "Employee{" + "id=" + id + ", department=" + department + ", empName=" + empName + ", version=" + version + '}';
  }
}
