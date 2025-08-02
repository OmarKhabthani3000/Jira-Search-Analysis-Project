package core.model.test;

import javax.jdo.annotations.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;


@Entity
// http://books.google.com/books?id=SbEAAQAAQBAJ&pg=PA322&lpg=PA322&dq=jpa+subgraph+embedded&source=bl&ots=5kgpiAY7DC&sig=0kOJ-rhxYjUB46_v4Q0lnPDCzxY&hl=en&sa=X&ei=TtqpU-_xIvPisATPz4LoCg&ved=0CFAQ6AEwBw#v=onepage&q=jpa%20subgraph%20embedded&f=false
@NamedEntityGraph(
		name = "graph.Country.name",
		attributeNodes = @NamedAttributeNode(value = "name", subgraph = "name-subgraph"),
		subgraphs = {
			@NamedSubgraph(name = "name-subgraph", attributeNodes = @NamedAttributeNode("value"))
		}
)
public class Country {

	@Id
	private String code;

	@Embedded
	private LocalizedName name;

	public Country(String code) {
		this.code = code;
	}

	protected Country() {
	}

	public String getCode() {
		return code;
	}

	public LocalizedName getName() {
		return name;
	}

	public void setName(LocalizedName name) {
		this.name = name;
	}

}
