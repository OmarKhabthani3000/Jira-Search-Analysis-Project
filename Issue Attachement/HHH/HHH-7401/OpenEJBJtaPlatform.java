package org.hibernate.service.jta.platform.internal;

// depends on openejb-core
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.Assembler;

public final class OpenEJBJtaPlatform extends ResinJtaPlatform {
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction) jndiService().locate( UT_NAME );
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        return SystemInstance.get().getComponent(Assembler.class).getTransactionManager();
    }
}

