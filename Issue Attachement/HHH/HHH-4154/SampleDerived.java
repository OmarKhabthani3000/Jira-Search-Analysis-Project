package org.frecklepuppy.bb.model;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;


@Entity
public class SampleDerived extends SampleBase
{
	private SampleDerived parent;
	private Collection<SampleDerived> children = new LinkedList<SampleDerived>();
	
	
	@ManyToOne
	public SampleDerived getParent()
    {
    	return parent;
    }
	
	
	public void setParent(SampleDerived parent)
    {
    	this.parent = parent;
    }
	
	@OneToMany(mappedBy="parent")
	@OrderBy("timestamp")
	public Collection<SampleDerived> getChildren()
    {
    	return children;
    }
	
	
	public void setChildren(Collection<SampleDerived> children)
    {
    	this.children = children;
    }
	
	
	
}
