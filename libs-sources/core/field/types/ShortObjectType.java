package ormlite.core.field.types;

import java.sql.SQLException;

import ormlite.core.field.FieldType;
import ormlite.core.field.SqlType;
import ormlite.core.support.DatabaseResults;

/**
 * Type that persists a Short object.
 * 
 * @author graywatson
 */
public class ShortObjectType extends BaseDataType {

	private static final ShortObjectType singleTon = new ShortObjectType();

	public static ShortObjectType getSingleton() {
		return singleTon;
	}

	private ShortObjectType() {
		super(SqlType.SHORT, new Class<?>[] { Short.class });
	}

	protected ShortObjectType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) {
		return Short.parseShort(defaultStr);
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return (Short) results.getShort(columnPos);
	}

	@Override
	public boolean isEscapedValue() {
		return false;
	}

	@Override
	public boolean isValidForVersion() {
		return true;
	}

	@Override
	public Object moveToNextValue(Object currentValue) {
		if (currentValue == null) {
			return (short) 1;
		} else {
			return (short) (((Short) currentValue) + 1);
		}
	}
}
