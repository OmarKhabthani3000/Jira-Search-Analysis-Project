package test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class Country {

	private Long id;

	private String name;

	private List<NameElement> nameList=new ArrayList<>();

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="nameListJSON")
	@Convert(converter=NameListConverter.class)
	public List<NameElement> getNameList() {
//		nameList=new ArrayList<>(nameList); // with this hack, it would work!
		return nameList;
	}

	public void setNameList(List<NameElement> newList) {
		nameList=newList;
	}

}
