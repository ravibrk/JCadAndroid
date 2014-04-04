package ormlite.core.stmt.query;

import java.sql.SQLException;
import java.util.List;

import ormlite.core.stmt.Where;

import ormlite.core.db.DatabaseType;
import ormlite.core.field.FieldType;
import ormlite.core.stmt.ArgumentHolder;

/**
 * Internal class handling the SQL 'IS NOT NULL' comparison query part. Used by {@link Where#isNull}.
 * 
 * @author graywatson
 */
public class IsNotNull extends BaseComparison {

	public IsNotNull(String columnName, FieldType fieldType) throws SQLException {
		super(columnName, fieldType, null, true);
	}

	@Override
	public void appendOperation(StringBuilder sb) {
		sb.append("IS NOT NULL ");
	}

	@Override
	public void appendValue(DatabaseType databaseType, StringBuilder sb, List<ArgumentHolder> argList) {
		// there is no value
	}
}