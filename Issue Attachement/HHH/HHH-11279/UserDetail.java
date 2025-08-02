package testcase;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({
	@NamedQuery(name="test1", query="select ud from UserDetail ud where ud.user.id = 1")
})
public class UserDetail implements Serializable {
	
	//JPA 2.0 Spec 2.4.1.3 Example 4 Case (b) BEGIN
	//This version works
	
//	@Id
//	int id;
//	
//	@OneToOne
//	@JoinColumn(name = "userId")
//	@MapsId
//	UserEntity user;
//	
//
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
	
  //JPA 2.0 Spec 2.4.1.3 Example 4 Case (b) END
	
	
  //JPA 2.0 Spec 2.4.1.3 Example 4 Case (a) BEGIN
	//This version fails

	@Id
	@OneToOne
	@JoinColumn(name = "userId")
	private UserEntity user;
	
  //JPA 2.0 Spec 2.4.1.3 Example 4 Case (a) END
	
	private String detail;


	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		UserDetail other = (UserDetail) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}


}
