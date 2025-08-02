/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.hibernate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationImpl;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;

/**
 * @author Vlad Mihalcea, Magnus Hagström
 */
public class SequenceNameExtractorMariaDBImpl extends SequenceInformationExtractorLegacyImpl {
	/**
	 * Singleton access
	 */
	public static final SequenceNameExtractorMariaDBImpl INSTANCE = new SequenceNameExtractorMariaDBImpl();

	// SQL to get metadata from induvidual sequence
	private static final String SQL_SEQUENCE_QUERY = "SELECT %s , next_not_cached_value, minimum_value, maximum_value, start_value, increment, cache_size FROM %s";

	@Override
	public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
		final String lookupSql = extractionContext.getJdbcEnvironment().getDialect().getQuerySequencesString();

		// *should* never happen, but to be safe in the interest of performance...
		if (lookupSql == null) {
			return SequenceInformationExtractorNoOpImpl.INSTANCE.extractMetadata(extractionContext);
		}

		final IdentifierHelper identifierHelper = extractionContext.getJdbcEnvironment().getIdentifierHelper();
		final Statement statement = extractionContext.getJdbcConnection().createStatement();
		final Statement statement2 = extractionContext.getJdbcConnection().createStatement();
		try {
			final ResultSet resultSet = statement.executeQuery(lookupSql);
			ResultSet resultSet2 = null;
			try {
				final List<SequenceInformation> sequenceInformationList = new ArrayList<>();
				while (resultSet.next()) {

					resultSet2 = statement2.executeQuery(String.format(SQL_SEQUENCE_QUERY, "'" + resultSetSequenceName(resultSet) + "'", resultSetSequenceName(resultSet)));
					resultSet2.next(); // Should get a hit , because its based on first query.
					sequenceInformationList
							.add(new SequenceInformationImpl(new QualifiedSequenceName(identifierHelper.toIdentifier(resultSetCatalogName(resultSet)), identifierHelper.toIdentifier(resultSetSchemaName(resultSet)), identifierHelper.toIdentifier(resultSetSequenceName(resultSet))), resultSetStartValueSize(resultSet2), resultSetMinValue(resultSet2), resultSetMaxValue(resultSet2), resultSetIncrementValue(resultSet2)));
				}
				return sequenceInformationList;
			} finally {
				try {
					resultSet.close();
					resultSet2.close();
				} catch (SQLException ignore) {
				}
			}
		} finally {
			try {
				statement.close();
				statement2.close();
			} catch (SQLException ignore) {
			}
		}
	}

	protected String resultSetSequenceName(ResultSet resultSet) throws SQLException {
		return resultSet.getString(1);
	}

	@Override
	protected String sequenceCatalogColumn() {
		return null;
	}

	@Override
	protected String sequenceSchemaColumn() {
		return null;
	}

}
