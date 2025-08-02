package de.juplo.plugins.hibernate4;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * 
 */
@Embeddable
public class EmployeeId implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public EmployeeId() {
    }
    
    public EmployeeId(String address, String phone, String name, String birthday, String age) {
        this.address = address;
        this.phone = phone;
        this.name = name;
        this.birthday = birthday;
        this.age = age;
    }
    
    @Column(length = 20)
    public String age;

    @Column(length = 20)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    private  String birthday;

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + (address != null ? address.hashCode() : 0);
        hash = hash * 31 + (phone != null ? phone.hashCode() : 0);
        hash = hash * 31 + (name != null ? name.hashCode() : 0);
        hash = hash * 31 + (age != null ? age.hashCode() : 0);
        return hash * 31 + (birthday != null ? birthday.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof EmployeeId)) {
            return false;
        }
        EmployeeId that = (EmployeeId) obj;
        if (age != that.age) {
            return false;
        }
        if (birthday != that.birthday) {
            return false;
        }
        if (name != null && !name.equals(that.name)) {
            return false;
        }
        if (phone != null && !phone.equals(that.phone)) {
            return false;
        }
        if (phone != null && !phone.equals(that.phone)) {
            return false;
        }
        return true;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }
}
