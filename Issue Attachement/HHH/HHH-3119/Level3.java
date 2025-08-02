

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "level_3")
public class Level3 implements java.io.Serializable {

	private Integer level3id;
	private String value;
	
	public Level3() {
	}

	public Level3(String value) {
		this.value = value;
	}

	@Id
	@GeneratedValue
	@Column(name = "level3ID", unique = true, nullable = false)
	public Integer getLevel3id() {
		return this.level3id;
	}

	public void setLevel3id(Integer level3id) {
		this.level3id = level3id;
	}

	@Column(name = "value", nullable = false, length = 45)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

    @Override
    public String toString() {
        return "Level3: (id=" + level3id + ", value=" + value + ")";
    }
	
	

}
