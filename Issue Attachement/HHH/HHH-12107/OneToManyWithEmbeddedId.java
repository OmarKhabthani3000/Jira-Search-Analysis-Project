/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querycache;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class OneToManyWithEmbeddedId {

	private OneToManyWithEmbeddedIdKey id;

	private Set<OneToManyWithEmbeddedIdChild> items;

	public OneToManyWithEmbeddedId() {
	}

	public OneToManyWithEmbeddedId(OneToManyWithEmbeddedIdKey id) {
		this.id = id;
	}

	@EmbeddedId
	public OneToManyWithEmbeddedIdKey getId() {
		return id;
	}
	public void setId(OneToManyWithEmbeddedIdKey id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = OneToManyWithEmbeddedIdChild.class, orphanRemoval = true)
	@JoinColumn(name = "parent_id")
	public Set<OneToManyWithEmbeddedIdChild> getItems() {return items;}
	public void setItems(Set<OneToManyWithEmbeddedIdChild> items) { this.items = items; }
}
