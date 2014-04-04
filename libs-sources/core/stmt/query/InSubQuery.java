package ormlite.core.stmt.query;

import java.sql.SQLException;
import java.util.List;

import ormlite.core.stmt.QueryBuilder.InternalQueryBuilderWrapper;
import ormlite.core.stmt.Where;

import ormlite.core.db.DatabaseType;
import ormlite.core.field.FieldType;
import ormlite.core.stmt.ArgumentHolder;

/**
 * Internal class handling the SQL 'in' query part. Used by {@link Where#in}.
 * 
 * @author graywatson
 */
public class InSubQuery extends BaseComparison {

	private final InternalQueryBuilderWrapper subQueryBuilder;
	private final boolean in;

	public InSubQuery(String columnName, FieldType fieldType, InternalQueryBuilderWrapper subQueryBuilder, boolean in)
			throws SQLException {
		super(columnName, fieldType, null, true);
		this.subQueryBuilder = subQueryBuilder;
		this.in = in;
	}

	@Override
	public void appendOperation(StringBuilder sb) {
		if (in) {
			sb.append("IN ");
		} else {
			sb.append("NOT IN ");
		}
	}

	@Override
	public void appendValue(DatabaseType databaseType, StringBuilder sb, List<ArgumentHolder> argList)
			throws SQLException {
		sb.append('(');
		subQueryBuilder.appendStatementString(sb, argList);
		FieldType[] resultFieldTypes = subQueryBuilder.getResultFieldTypes();
		if (resultFieldTypes == null) {
			// we assume that if someone is doing a raw select, they know what they are doing
		} else if (resultFieldTypes.length != 1) {
			throw new SQLException("There must be only 1 result column in sub-query but we found "
					+ resultFieldTypes.length);
		} else if (fieldType.getSqlType() != resultFieldTypes[0].getSqlType()) {
			throw new SQLException("Outer column " + fieldType + " is not the same type as inner column "
					+ resultFieldTypes[0]);
		}
		sb.append(") ");
	}
}