//$Id: BatchSize.java,v 1.2 2005/02/26 00:57:38 epbernard Exp $
package org.hibernate.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Place class description here.
 *
 * @author		Inger
 */

@Target({TYPE, METHOD}) @Retention(RUNTIME)
public @interface Filters {
    public Filter[] value();
}
