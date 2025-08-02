/*
 *  © 2019 RÖPERWEISE GmbH, Germany. All rights reserved.
 */

package de.roeperweise.compid;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

/**
 *
 * @author Stefan Röper, RÖPERWEISE GmbH.
 */
@Entity
public class Company {
  @Id
  @SequenceGenerator(name = "SGEN_Company", sequenceName = "COMP_SEQ",
      initialValue = 1, allocationSize = 50)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGEN_Company")
  private Integer id;
  private String compName;
  @Version
  private Integer version;

  public Company() {
  }

  public Company(String compName) {
    this.compName = compName;
  }

  public Integer getId() {
    return id;
  }

  public String getCompName() {
    return compName;
  }

  public void setCompName(String compName) {
    this.compName = compName;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "Company{" + "id=" + id + ", compName=" + compName + ", version=" + version + '}';
  }
}
