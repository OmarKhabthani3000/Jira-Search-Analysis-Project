package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the start index value for a list index as stored on the database.  This base is subtracted from the
 * incoming database value on reads to determine the List position; it is added to the List position index when
 * writing to the database.
 *
 * By default list indexes are stored starting at zero.
 *
 * Generally used in conjunction with {@link javax.persistence.OrderColumn}.
 *
 * @see javax.persistence.OrderColumn
 *
 * @author Steve Ebersole
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ListIndexBase {
	/**
	 * The list index base.  Default is 0.
	 */
	int value() default 0;
}