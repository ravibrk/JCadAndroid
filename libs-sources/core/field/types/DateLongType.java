package ormlite.core.field.types;

import java.sql.SQLException;
import java.util.Date;

import ormlite.core.field.FieldType;
import ormlite.core.field.SqlType;
import ormlite.core.misc.SqlExceptionUtil;
import ormlite.core.support.DatabaseResults;

/**
 * Persists the {@link java.util.Date} Java class as long milliseconds since epoch.
 * 
 * <p>
 * NOTE: This is <i>not</i> the same as the {@link java.sql.Date} class.
 * </p>
 * 
 * @author graywatson
 */
public class DateLongType extends BaseDateType {

	private static final DateLongType singleTon = new DateLongType();

	public static DateLongType getSingleton() {
		return singleTon;
	}

	private DateLongType() {
		super(SqlType.LONG, new Class<?>[0]);
	}

	/**
	 * Here for others to subclass.
	 */
	protected DateLongType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		try {
			return Long.parseLong(defaultStr);
		} catch (NumberFormatException e) {
			throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default date-long value: "
					+ defaultStr, e);
		}
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getLong(columnPos);
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
		return new Date((Long) sqlArg);
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object obj) {
		Date date = (Date) obj;
		return (Long) date.getTime();
	}

	@Override
	public boolean isEscapedValue() {
		return false;
	}

	@Override
	public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) {
		return sqlArgToJava(fieldType, Long.parseLong(stringValue), columnPos);
	}

	@Override
	public Class<?> getPrimaryClass() {
		return Date.class;
	}
}