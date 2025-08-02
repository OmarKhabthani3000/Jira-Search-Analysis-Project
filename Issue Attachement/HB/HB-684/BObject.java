/*
 * Created on 29-Jan-04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

import java.util.Set;

/**
 * @author gcoghlan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BObject
{
	private Integer id;
	private Set oneToMany;
	
	/**
	 * @return
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * @param integer
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}
	/**
	 * @return
	 */
	public Set getOneToMany()
	{
		return oneToMany;
	}

	/**
	 * @param set
	 */
	public void setOneToMany(Set set)
	{
		oneToMany = set;
	}

}
