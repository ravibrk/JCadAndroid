package ormlite.core.stmt.mapped;

import java.sql.SQLException;

import ormlite.core.dao.ObjectCache;
import ormlite.core.support.DatabaseConnection;
import ormlite.core.table.TableInfo;
import ormlite.core.Model;

import ormlite.core.db.DatabaseType;
import ormlite.core.field.FieldType;
import ormlite.core.misc.SqlExceptionUtil;

/**
 * A mapped statement for deleting an object.
 * 
 * @author graywatson
 */
public class MappedDelete<T extends Model, ID> extends BaseMappedStatement<T, ID> {

	private MappedDelete(TableInfo<T> tableInfo, String statement, FieldType[] argFieldTypes) {
		super(tableInfo, statement, argFieldTypes);
	}

	public static <T extends Model, ID> MappedDelete<T, ID> build(DatabaseType databaseType, TableInfo<T> tableInfo)
			throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot delete from " + tableInfo.getDataClass()
					+ " because it doesn't have an id field");
		}
		StringBuilder sb = new StringBuilder(64);
		appendTableName(databaseType, sb, "DELETE FROM ", tableInfo.getTableName());
		appendWhereFieldEq(databaseType, idField, sb, null);
		return new MappedDelete<T, ID>(tableInfo, sb.toString(), new FieldType[] { idField });
	}

	/**
	 * Delete the object from the database.
	 */
	public int delete(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
		try {
			Object[] args = getFieldObjects(data);
			int rowC = databaseConnection.delete(statement, args, argFieldTypes);
			logger.debug("delete data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("delete arguments: {}", (Object) args);
			}
			if (rowC > 0 && objectCache != null) {
				Object id = idField.extractJavaFieldToSqlArgValue(data);
				objectCache.remove(clazz, id);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run delete stmt on object " + data + ": " + statement, e);
		}
	}

	/**
	 * Delete the object from the database.
	 */
	public int deleteById(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
		try {
			Object[] args = new Object[] { convertIdToFieldObject(id) };
			int rowC = databaseConnection.delete(statement, args, argFieldTypes);
			logger.debug("delete data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("delete arguments: {}", (Object) args);
			}
			if (rowC > 0 && objectCache != null) {
				objectCache.remove(clazz, id);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run deleteById stmt on id " + id + ": " + statement, e);
		}
	}
}
