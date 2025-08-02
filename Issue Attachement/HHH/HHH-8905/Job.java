package foo.core.data.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Job implements Serializable
{
	private static final long serialVersionUID = -8934278875696026801L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@NotNull
	@Column(nullable = false)
	private Boolean active;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar execTime;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar execDate;

	// @formatter:off
	
	@ElementCollection
	@CollectionTable( 
		name = "job_task_user" 
		,joinColumns = {@JoinColumn(name = "job_id")}
	)
	@MapKeyJoinColumn(name = "task_id", nullable = false)
	@Column(name="user_id", nullable = false)
	private Map<Task, User> userTasks;
	
	// @formatter:on

	public Calendar getExecTime()
	{
		return execTime;
	}

	public void setExecTime(Calendar execTime)
	{
		this.execTime = execTime;
	}

	public Calendar getExecDate()
	{
		return execDate;
	}

	public void setExecDate(Calendar execDate)
	{
		this.execDate = execDate;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public Map<Task, User> getUserTasks()
	{
		return userTasks;
	}

	public void setUserTasks(Map<Task, User> userTasks)
	{
		this.userTasks = userTasks;
	}
}
