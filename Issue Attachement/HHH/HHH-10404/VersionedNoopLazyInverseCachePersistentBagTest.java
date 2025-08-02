/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.bag.version;

/**
 * Tests related to operations on a PersistentBag.
 *
 * @author Steve Ebersole
 */
public class VersionedNoopLazyInverseCachePersistentBagTest extends AbstractVersionedNoopLazyInversePersistentBagTest {

	@Override
	public String[] getMappings() {
		return new String[]{"collection/bag/version/VersionedNoopLazyInverseBagWithCacheMappings.hbm.xml"};
	}
}
