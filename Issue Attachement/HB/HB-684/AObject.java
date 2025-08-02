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
public class AObject
{
	private Integer id;
	private BObject oneToOne;
	private Set manyToMany;
	
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
	public BObject getOneToOne()
	{
		return oneToOne;
	}

	/**
	 * @param object
	 */
	public void setOneToOne(BObject object)
	{
		oneToOne = object;
	}

	/**
	 * @return
	 */
	public Set getManyToMany()
	{
		return manyToMany;
	}

	/**
	 * @param set
	 */
	public void setManyToMany(Set set)
	{
		manyToMany = set;
	}

}
