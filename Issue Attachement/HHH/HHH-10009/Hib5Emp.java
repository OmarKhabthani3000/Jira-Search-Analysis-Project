package com.hibers5.ex;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="HIB5EMP")
public class Hib5Emp implements Serializable {
    private  int  empId;
    private  String  empName;
    private  Job  job;
    private  Dept deptName;
    private  LocalDate  hiredate;
    private  double  salary;
    
    
    @Id
    public int getEmpId() {
        return empId;
    }


    public void setEmpId(int empId) {
        this.empId = empId;
    }

    @Column
    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
    
    @Enumerated(EnumType.STRING)
    @Column
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Enumerated(EnumType.STRING)
    @Column
    public Dept getDeptName() {
        return deptName;
    }

    public void setDeptName(Dept deptName) {
        this.deptName = deptName;
    }


    @Column
    public LocalDate getHiredate() {
        return hiredate;
    }


    public void setHiredate(LocalDate hiredate) {
        this.hiredate = hiredate;
    }

    @Column
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Hib5Emp() {
        // TODO Auto-generated constructor stub
    }


    public Hib5Emp(int empId, String empName, Job job, double salary, LocalDate hiredate, Dept deptName) {
        super();
        this.empId = empId;
        this.empName = empName;
        this.job = job;
        this.salary = salary;
        this.hiredate = hiredate;
        this.deptName = deptName;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + empId;
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Hib5Emp other = (Hib5Emp) obj;
        if (empId != other.empId)
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "Hib5Emp [empId=" + empId + ", empName=" + empName + ", job=" + job + ", deptName=" + deptName
                + ", hiredate=" + hiredate + ", salary=" + salary + "]";
    }   

}
