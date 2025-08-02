package test.hibernate.bug1;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="Employee")
//@SecondaryTable(name="EmployeeDetail")
public class Employee {

	private int employeeID;
	private String employeeName; 
	
	
	@Id
	@Column(name="EmployeeID")
	@GeneratedValue(strategy=GenerationType.TABLE, generator="empId")
	@TableGenerator(name="empId",table="empTable",pkColumnName="empKey",
	pkColumnValue="empValue",
	allocationSize=1)
	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}
	
	@Column(name="EmployeeName")
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	
}
	

