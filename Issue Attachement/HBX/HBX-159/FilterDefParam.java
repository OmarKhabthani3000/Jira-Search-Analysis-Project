//$Id: Entity.java,v 1.2 2005/02/28 21:42:17 epbernard Exp $
package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Place class description here.
 *
 * @author		Inger
 */

@Target({}) @Retention(RUNTIME)
public @interface FilterDefParam {
    public String name();
    public String type();
}
