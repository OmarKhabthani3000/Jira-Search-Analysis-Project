@Entity
public class Publication
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Long id;
	private Boolean active;

	@Id
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Boolean getActive()
	{
		return this.active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	@Transient
	public boolean isActive()
	{
		return this.active == null ? false : this.active.booleanValue();
	}
}