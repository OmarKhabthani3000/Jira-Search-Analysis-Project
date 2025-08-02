package testlob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class TestLob {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Lob
	private String bigString;

	public String getBigString() {
		return bigString;
	}

	public void setBigString(String bigString) {
		this.bigString = bigString;
	}

}
