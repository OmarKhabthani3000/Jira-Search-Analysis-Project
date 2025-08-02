

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "level_1")
public class Level1 implements java.io.Serializable {

	private Integer level1id;
	private Level2 level2;

	public Level1() {
	}

	public Level1(Level2 level2) {
		this.level2 = level2;
	}

	@Id
	@GeneratedValue
	@Column(name = "level1ID", unique = true, nullable = false)
	public Integer getLevel1id() {
		return this.level1id;
	}

	public void setLevel1id(Integer level1id) {
		this.level1id = level1id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "level2ID", nullable = false)
	public Level2 getLevel2() {
		return this.level2;
	}

	public void setLevel2(Level2 level2) {
		this.level2 = level2;
	}

    @Override
    public String toString() {
        return "Level1: (id=" + level1id + ", level2=" + level2 + ")";
    }

}
