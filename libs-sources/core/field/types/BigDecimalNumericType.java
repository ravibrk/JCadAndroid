package ormlite.core.field.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

import ormlite.core.field.FieldType;
import ormlite.core.field.SqlType;
import ormlite.core.misc.SqlExceptionUtil;
import ormlite.core.support.DatabaseResults;

/**
 * Type that persists a {@link BigInteger} object as a NUMERIC SQL database field.
 * 
 * @author graywatson
 */
public class BigDecimalNumericType extends BaseDataType {

	private static final BigDecimalNumericType singleTon = new BigDecimalNumericType();

	public static BigDecimalNumericType getSingleton() {
		return singleTon;
	}

	private BigDecimalNumericType() {
		// this has no classes because {@link BigDecimalString} is the default
		super(SqlType.BIG_DECIMAL, new Class<?>[0]);
	}

	/**
	 * Here for others to subclass.
	 */
	protected BigDecimalNumericType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		try {
			return new BigDecimal(defaultStr);
		} catch (IllegalArgumentException e) {
			throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default BigDecimal string '"
					+ defaultStr + "'", e);
		}
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getBigDecimal(columnPos);
	}

	@Override
	public boolean isAppropriateId() {
		return false;
	}

	@Override
	public boolean isEscapedValue() {
		return false;
	}

	@Override
	public Class<?> getPrimaryClass() {
		return BigDecimal.class;
	}
}
