package edu.upmc.ccweb.dosimetry.hibtest;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.NamedQuery;

@MappedSuperclass
@NamedQuery(name="User.findByLoginName", query="FROM User WHERE loginName = :loginName")
public class BaseUser {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String loginName;


	public String getLoginName() {
		return this.loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}



	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
