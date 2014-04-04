package ormlite.core.stmt.mapped;

import java.sql.SQLException;

import ormlite.core.Model;
import ormlite.core.dao.ObjectCache;
import ormlite.core.db.DatabaseType;
import ormlite.core.field.FieldType;
import ormlite.core.support.DatabaseConnection;
import ormlite.core.table.TableInfo;

/**
 * Mapped statement for querying for an object by its ID.
 * 
 * @author graywatson
 */
public class MappedQueryForId<T extends Model, ID> extends BaseMappedQuery<T, ID> {

	private final String label;

	protected MappedQueryForId(TableInfo<T> tableInfo, String statement, FieldType[] argFieldTypes,
			FieldType[] resultsFieldTypes, String label) {
		super(tableInfo, statement, argFieldTypes, resultsFieldTypes);
		this.label = label;
	}

	/**
	 * Query for an object in the database which matches the id argument.
	 */
	public T execute(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
		if (objectCache != null) {
			T result = objectCache.get(clazz, id);
			if (result != null) {
				return result;
			}
		}
		Object[] args = new Object[] { convertIdToFieldObject(id) };
		// @SuppressWarnings("unchecked")
		Object result = databaseConnection.queryForOne(statement, args, argFieldTypes, this, objectCache);
		if (result == null) {
			logger.debug("{} using '{}' and {} args, got no results", label, statement, args.length);
		} else if (result == DatabaseConnection.MORE_THAN_ONE) {
			logger.error("{} using '{}' and {} args, got >1 results", label, statement, args.length);
			logArgs(args);
			throw new SQLException(label + " got more than 1 result: " + statement);
		} else {
			logger.debug("{} using '{}' and {} args, got 1 result", label, statement, args.length);
		}
		logArgs(args);
		@SuppressWarnings("unchecked")
		T castResult = (T) result;
		return castResult;
	}

	public static <T extends Model, ID> MappedQueryForId<T, ID> build(DatabaseType databaseType, TableInfo<T> tableInfo,
			FieldType idFieldType) throws SQLException {
		if (idFieldType == null) {
			idFieldType = tableInfo.getIdField();
			if (idFieldType == null) {
				throw new SQLException("Cannot query-for-id with " + tableInfo.getDataClass()
						+ " because it doesn't have an id field");
			}
		}
		String statement = buildStatement(databaseType, tableInfo, idFieldType);
		return new MappedQueryForId<T, ID>(tableInfo, statement, new FieldType[] { idFieldType },
				tableInfo.getFieldTypes(), "query-for-id");
	}

	protected static <T extends Model, ID> String buildStatement(DatabaseType databaseType, TableInfo<T> tableInfo,
			FieldType idFieldType) {
		// build the select statement by hand
		StringBuilder sb = new StringBuilder(64);
		appendTableName(databaseType, sb, "SELECT * FROM ", tableInfo.getTableName());
		appendWhereFieldEq(databaseType, idFieldType, sb, null);
		return sb.toString();
	}

	private void logArgs(Object[] args) {
		if (args.length > 0) {
			// need to do the (Object) cast to force args to be a single object and not an array
			logger.trace("{} arguments: {}", label, (Object) args);
		}
	}
}
