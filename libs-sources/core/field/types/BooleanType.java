package ormlite.core.field.types;

import ormlite.core.field.SqlType;

/**
 * Type that persists a boolean primitive.
 * 
 * @author graywatson
 */
public class BooleanType extends BooleanObjectType {

	private static final BooleanType singleTon = new BooleanType();

	public static BooleanType getSingleton() {
		return singleTon;
	}

	private BooleanType() {
		super(SqlType.BOOLEAN, new Class<?>[] { boolean.class });
	}

	/**
	 * Here for others to subclass.
	 */
	protected BooleanType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
}
