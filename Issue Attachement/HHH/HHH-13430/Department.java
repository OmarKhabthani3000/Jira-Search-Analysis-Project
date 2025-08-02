/*
 *  © 2019 RÖPERWEISE GmbH, Germany. All rights reserved.
 */

package de.roeperweise.compid;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Stefan Röper, RÖPERWEISE GmbH.
 */
@Entity
public class Department {
  @Id
  @SequenceGenerator(name = "SGEN_A", sequenceName = "A_SEQ",
      initialValue = 1, allocationSize = 50)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGEN_A")
  private Integer id;
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "COMPANY_ID", nullable = false)
  @NotNull
  private Company company;
  private String depName;
  private short nbOfEmployees;
  @Version
  private Integer version;

  public Department() {
    nbOfEmployees = 0;
  }

  public Department(Company company, String depName) {
    this();
    this.company = company;
    this.depName = depName;
  }

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getDepName() {
    return depName;
  }

  public void setDepName(String depName) {
    this.depName = depName;
  }

  public short getNbOfEmployees() {
    return nbOfEmployees;
  }
  
  public short incAndGetNbOfEmployees() {
    nbOfEmployees++;
    return nbOfEmployees;
  }

  public Integer getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return "Department{" + "id=" + id + ", company=" + company + ", depName=" + depName + ", nbOfEmployees=" + nbOfEmployees + ", version=" + version + '}';
  }
}
