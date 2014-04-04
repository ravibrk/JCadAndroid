package ormlite.core.stmt.query;

import java.sql.SQLException;
import java.util.List;

import ormlite.core.stmt.QueryBuilder.InternalQueryBuilderWrapper;
import ormlite.core.stmt.Where;

import ormlite.core.db.DatabaseType;
import ormlite.core.stmt.ArgumentHolder;

/**
 * Internal class handling the SQL 'EXISTS' query part. Used by {@link Where#exists}.
 * 
 * @author graywatson
 */
public class Exists implements Clause {

	private final InternalQueryBuilderWrapper subQueryBuilder;

	public Exists(InternalQueryBuilderWrapper subQueryBuilder) {
		this.subQueryBuilder = subQueryBuilder;
	}

	public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> argList)
			throws SQLException {
		sb.append("EXISTS (");
		subQueryBuilder.appendStatementString(sb, argList);
		sb.append(") ");
	}
}
