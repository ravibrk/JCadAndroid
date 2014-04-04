package ormlite.core.stmt;

import java.sql.SQLException;

import ormlite.core.Model;
import ormlite.core.dao.Dao;
import ormlite.core.dao.RawRowMapper;
import ormlite.core.field.FieldType;
import ormlite.core.table.TableInfo;

/**
 * Default row mapper when you are using the {@link Dao#queryRaw(String, RawRowMapper, String...)}.
 * 
 * @author graywatson
 */
public class RawRowMapperImpl<T extends Model, ID> implements RawRowMapper<T> {

	private final TableInfo<T> tableInfo;

	public RawRowMapperImpl(TableInfo<T> tableInfo) {
		this.tableInfo = tableInfo;
	}

	public T mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
		// create our object
		T rowObj = tableInfo.createObject();
		for (int i = 0; i < columnNames.length; i++) {
			// sanity check, prolly will never happen but let's be careful out there
			if (i >= resultColumns.length) {
				continue;
			}
			// run through and convert each field
			FieldType fieldType = tableInfo.getFieldTypeByColumnName(columnNames[i]);
			Object fieldObj = fieldType.convertStringToJavaField(resultColumns[i], i);
			// assign it to the row object
			fieldType.assignField(rowObj, fieldObj, false, null);
		}
		return rowObj;
	}
}
