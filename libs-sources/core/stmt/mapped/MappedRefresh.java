package ormlite.core.stmt.mapped;

import java.sql.SQLException;

import ormlite.core.dao.ObjectCache;
import ormlite.core.support.DatabaseConnection;
import ormlite.core.table.TableInfo;
import ormlite.core.Model;

import ormlite.core.db.DatabaseType;
import ormlite.core.field.FieldType;

/**
 * Mapped statement for refreshing the fields in an object.
 * 
 * @author graywatson
 */
public class MappedRefresh<T extends Model, ID> extends MappedQueryForId<T, ID> {

	private MappedRefresh(TableInfo<T> tableInfo, String statement, FieldType[] argFieldTypes,
			FieldType[] resultFieldTypes) {
		super(tableInfo, statement, argFieldTypes, resultFieldTypes, "refresh");
	}

	/**
	 * Execute our refresh query statement and then update all of the fields in data with the fields from the result.
	 * 
	 * @return 1 if we found the object in the table by id or 0 if not.
	 */
	public int executeRefresh(DatabaseConnection databaseConnection, T data, ObjectCache objectCache)
			throws SQLException {
		@SuppressWarnings("unchecked")
		ID id = (ID) idField.extractJavaFieldValue(data);
		// we don't care about the cache here
		T result = super.execute(databaseConnection, id, null);
		if (result == null) {
			return 0;
		}
		// copy each field from the result into the passed in object
		for (FieldType fieldType : resultsFieldTypes) {
			if (fieldType != idField) {
				fieldType.assignField(data, fieldType.extractJavaFieldValue(result), false, (ormlite.core.dao.ObjectCache) objectCache);
			}
		}
		return 1;
	}

	public static <T extends Model, ID> MappedRefresh<T, ID> build(DatabaseType databaseType, TableInfo<T> tableInfo)
			throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot refresh " + tableInfo.getDataClass()
					+ " because it doesn't have an id field");
		}
		String statement = buildStatement(databaseType, tableInfo, idField);
		return new MappedRefresh<T, ID>(tableInfo, statement, new FieldType[] { tableInfo.getIdField() },
				tableInfo.getFieldTypes());
	}
}
