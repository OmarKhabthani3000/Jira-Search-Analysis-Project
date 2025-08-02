package de.huthmann;

import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.impl.StatelessSessionImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for
 * User: ralf
 * Date: 16.04.14
 * Time: 09:20
 * To change this template use File | Settings | File Templates.
 */
public class StatelessSessionIsLobCreationContextTest_3_6_X {
    @Test
    public void testIsLobCreationContext() {
        Assert.assertTrue("StatelessSessionImpl should implement LobCreationContext", LobCreationContext.class.isAssignableFrom(StatelessSessionImpl.class));
    }
}
