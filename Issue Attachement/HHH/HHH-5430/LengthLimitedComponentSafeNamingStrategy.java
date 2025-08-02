
package org.hibernate.cfg;


import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

/**
 * An implementation of {@link GenericLengthLimitedNamingStrategy} using {@link DefaultComponentSafeNamingStrategy}
 * @author Martin Cerny
 */
public class LengthLimitedComponentSafeNamingStrategy extends GenericLengthLimitedNamingStrategy {

    public LengthLimitedComponentSafeNamingStrategy() {
        super(new DefaultComponentSafeNamingStrategy());
    }

}
