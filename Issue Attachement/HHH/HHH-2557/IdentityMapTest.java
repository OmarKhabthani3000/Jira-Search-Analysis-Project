package org.hibernate.util;

import junit.framework.TestCase;

/**
 * IdentityMap Tester.
 *
 */
public class IdentityMapTest extends TestCase
{
    public void testIdentityKeyEqualsForNull()
    {
        IdentityMap.IdentityKey key = new IdentityMap.IdentityKey("foo");

        assertFalse("never equal to null", key.equals(null));
    }
}
