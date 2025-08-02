/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querycache;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
public class OneToManyWithEmbeddedIdKey implements Serializable {
	private Integer id;

	public OneToManyWithEmbeddedIdKey() {}
	public OneToManyWithEmbeddedIdKey(Integer id) {this.id = id; }

	@Column(name = "id")
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {this.id = id; }
}
