package de.laliluna.bibabook.util;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.MappingException;

/**
 * Author: Sebastian Hennebrueder, http://www.laliluna.de
 * Date: 24.07.2009
 */
public class PostgreSQLExtendedDialect extends PostgreSQLDialect {

	/**
	 * Does this dialect support "pooled" sequences.  Not aware of a better
	 * name for this.  Essentially can we specify the initial and increment values?
	 *
	 * @return True if such "pooled" sequences are supported; false otherwise.
	 * @see #getCreateSequenceStrings(String, int, int)
	 * @see #getCreateSequenceString(String, int, int)
	 */
	@Override
	public boolean supportsPooledSequences() {
		return true;
	}


	/**
	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
	 * taking the initial value and increment size to be applied to the sequence
	 * definition.
	 * </p>
	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
	 * with the string: " start with {initialValue} increment by {incrementSize}" where
	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
	 * dialects should only need to override this method if different key phrases
	 * are used to apply the allocation information.
	 *
	 * @param sequenceName	The name of the sequence
	 * @param initialValue	The initial value to apply to 'create sequence' statement
	 * @param incrementSize The increment value to apply to 'create sequence' statement
	 * @return The sequence creation command
	 * @throws org.hibernate.MappingException If sequences are not supported.
	 */
	@Override
	protected String getCreateSequenceString(String sequenceName, int initialValue,
																					 int incrementSize) throws MappingException {
		return getCreateSequenceString(sequenceName) + " minvalue " + initialValue + " increment " + incrementSize;
	}
}
