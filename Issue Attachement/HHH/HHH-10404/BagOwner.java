/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.bag.version;

import java.util.List;

/**
 * {@inheritDoc}
 *
 * @author Steve Ebersole
 */
public class BagOwner {

	private Integer id;
	private Integer rv;
	private String name;
	private BagOwner parent;
	private List<BagOwner> children;


	public BagOwner() {
	}


	public Integer getId() {
		return this.id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getRv() {
		return this.rv;
	}


	public void setRv(Integer rv) {
		this.rv = rv;
	}


	public BagOwner(String name) {
		this.name = name;
	}


	public String getName() {
		return this.name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public BagOwner getParent() {
		return this.parent;
	}


	public void setParent(BagOwner parent) {
		this.parent = parent;
	}


	public List<BagOwner> getChildren() {
		return this.children;
	}


	public void setChildren(List<BagOwner> children) {
		this.children = children;
	}
}
