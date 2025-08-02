/*
 *  © 2019 RÖPERWEISE GmbH, Germany. All rights reserved.
 */

package de.roeperweise.compid;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Stefan Röper, RÖPERWEISE GmbH.
 */
public class EmployeeId implements Serializable {
  private short id;
  private Integer department;

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public Integer getDepartment() {
    return department;
  }

  public void setDepartment(Integer department) {
    this.department = department;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 29 * hash + Objects.hashCode(this.id);
    hash = 29 * hash + Objects.hashCode(this.department);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final EmployeeId other = (EmployeeId) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return Objects.equals(this.department, other.department);
  }

}
