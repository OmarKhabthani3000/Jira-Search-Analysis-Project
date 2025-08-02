

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "level_2")
public class Level2 implements java.io.Serializable {

	private Integer level2id;
	private Level3 level3;
	
	public Level2() {
	}

	public Level2(Level3 level3) {
		this.level3 = level3;
	}

	@Id
	@GeneratedValue
	@Column(name = "level2ID", unique = true, nullable = false)
	public Integer getLevel2id() {
		return this.level2id;
	}

	public void setLevel2id(Integer level2id) {
		this.level2id = level2id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "level3ID", nullable = false)
	public Level3 getLevel3() {
		return this.level3;
	}

	public void setLevel3(Level3 level3) {
		this.level3 = level3;
	}

    @Override
    public String toString() {
        return "Level3: (id=" + level2id + ", level3=" + level3 + ")";
    }	

}
