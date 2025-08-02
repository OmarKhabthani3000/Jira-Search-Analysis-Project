package foo.bar.data.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Task implements Serializable
{
	private static final long serialVersionUID = -8947912179390521790L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@NotNull
	@Column(nullable = false)
	private String name;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "query_id", nullable = false) //, foreignKey = @ForeignKey(name = "fk_task_query"))
	private Query query;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "variable_id", nullable = false)
	private Variable variable;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Query getQuery()
	{
		return query;
	}

	public void setQuery(Query query)
	{
		this.query = query;
	}

	public Variable getVariable()
	{
		return variable;
	}

	public void setVariable(Variable variable)
	{
		this.variable = variable;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
