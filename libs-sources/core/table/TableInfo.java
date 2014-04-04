package ormlite.core.table;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ormlite.core.Model;
import ormlite.core.dao.BaseDaoImpl;
import ormlite.core.dao.Dao;
import ormlite.core.dao.ForeignCollection;
import ormlite.core.db.DatabaseType;
import ormlite.core.field.DatabaseField;
import ormlite.core.field.FieldType;
import ormlite.core.misc.BaseDaoEnabled;
import ormlite.core.misc.SqlExceptionUtil;
import ormlite.core.support.ConnectionSource;
import android.annotation.SuppressLint;

/**
 * Information about a database table including the associated tableName, class, constructor, and the included fields.
 * 
 * @param <T>
 *            The class that the code will be operating on.
 * @param <ID>
 *            The class of the ID column associated with the class. The T class does not require an ID field. The class
 *            needs an ID parameter however so you can use Void or Object to satisfy the compiler.
 * @author graywatson
 */
@SuppressLint("DefaultLocale")
public class TableInfo<MODEL extends Model> {

	private static final FieldType[] NO_FOREIGN_COLLECTIONS = new FieldType[0];

	private final BaseDaoImpl<MODEL> baseDaoImpl;
	private final Class<MODEL> dataClass;
	private final String tableName;
	private final FieldType[] fieldTypes;
	private final FieldType[] foreignCollections;
	private final FieldType idField;
	private final Constructor<MODEL> constructor;
	private final boolean foreignAutoCreate;
	private Map<String, FieldType> fieldNameMap;

	/**
	 * Creates a holder of information about a table/class.
	 * 
	 * @param connectionSource
	 *            Source of our database connections.
	 * @param baseDaoImpl
	 *            Associated BaseDaoImpl.
	 * @param dataClass
	 *            Class that we are holding information about.
	 */
	public TableInfo(ConnectionSource connectionSource, BaseDaoImpl<MODEL> baseDaoImpl, Class<MODEL> dataClass) throws SQLException {
		this(connectionSource.getDatabaseType(), baseDaoImpl, DatabaseTableConfig.fromClass(connectionSource, dataClass));
	}

	/**
	 * Creates a holder of information about a table/class.
	 * 
	 * @param databaseType
	 *            Database type we are storing the class in.
	 * @param baseDaoImpl
	 *            Associated BaseDaoImpl.
	 * @param tableConfig
	 *            Configuration for our table.
	 */
	public TableInfo(DatabaseType databaseType, BaseDaoImpl<MODEL> baseDaoImpl, DatabaseTableConfig<MODEL> tableConfig)
			throws SQLException {
		this.baseDaoImpl = baseDaoImpl;
		this.dataClass = tableConfig.getDataClass();
		this.tableName = tableConfig.getTableName();
		this.fieldTypes = tableConfig.getFieldTypes(databaseType);
		// find the id field
		FieldType findIdFieldType = null;
		boolean foreignAutoCreate = false;
		int foreignCollectionCount = 0;
		for (FieldType fieldType : fieldTypes) {
			if (fieldType.isId() || fieldType.isGeneratedId() || fieldType.isGeneratedIdSequence()) {
				if (findIdFieldType != null) {
					throw new SQLException("More than 1 idField configured for class " + dataClass + " ("
							+ findIdFieldType + "," + fieldType + ")");
				}
				findIdFieldType = fieldType;
			}
			if (fieldType.isForeignAutoCreate()) {
				foreignAutoCreate = true;
			}
			if (fieldType.isForeignCollection()) {
				foreignCollectionCount++;
			}
		}
		// can be null if there is no id field
		this.idField = findIdFieldType;
		this.constructor = tableConfig.getConstructor();
		this.foreignAutoCreate = foreignAutoCreate;
		if (foreignCollectionCount == 0) {
			this.foreignCollections = NO_FOREIGN_COLLECTIONS;
		} else {
			this.foreignCollections = new FieldType[foreignCollectionCount];
			foreignCollectionCount = 0;
			for (FieldType fieldType : fieldTypes) {
				if (fieldType.isForeignCollection()) {
					this.foreignCollections[foreignCollectionCount] = fieldType;
					foreignCollectionCount++;
				}
			}
		}
	}

	/**
	 * Return the class associated with this object-info.
	 */
	public Class<MODEL> getDataClass() {
		return dataClass;
	}

	/**
	 * Return the name of the table associated with the object.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Return the array of field types associated with the object.
	 */
	public FieldType[] getFieldTypes() {
		return fieldTypes;
	}

	/**
	 * Return the {@link FieldType} associated with the columnName.
	 */
	@SuppressLint("DefaultLocale")
	public FieldType getFieldTypeByColumnName(String columnName) {
		if (fieldNameMap == null) {
			// build our alias map if we need it
			Map<String, FieldType> map = new HashMap<String, FieldType>();
			for (FieldType fieldType : fieldTypes) {
				map.put(fieldType.getColumnName().toLowerCase(), fieldType);
			}
			fieldNameMap = map;
		}
		FieldType fieldType = fieldNameMap.get(columnName.toLowerCase());
		// if column name is found, return it
		if (fieldType != null) {
			return fieldType;
		}
		// look to see if someone is using the field-name instead of column-name
		for (FieldType fieldType2 : fieldTypes) {
			if (fieldType2.getFieldName().equals(columnName)) {
				throw new IllegalArgumentException("You should use columnName '" + fieldType2.getColumnName()
						+ "' for table " + tableName + " instead of fieldName '" + fieldType2.getFieldName() + "'");
			}
		}
		throw new IllegalArgumentException("Unknown column name '" + columnName + "' in table " + tableName);
	}

	/**
	 * Return the id-field associated with the object.
	 */
	public FieldType getIdField() {
		return idField;
	}

	public Constructor<MODEL> getConstructor() {
		return constructor;
	}

	/**
	 * Return a string representation of the object.
	 */
	public String objectToString(MODEL object) {
		StringBuilder sb = new StringBuilder(64);
		sb.append(object.getClass().getSimpleName());
		for (FieldType fieldType : fieldTypes) {
			sb.append(' ').append(fieldType.getColumnName()).append("=");
			try {
				sb.append(fieldType.extractJavaFieldValue(object));
			} catch (Exception e) {
				throw new IllegalStateException("Could not generate toString of field " + fieldType, e);
			}
		}
		return sb.toString();
	}

	/**
	 * Create and return an object of this type using our reflection constructor.
	 */
	public MODEL createObject() throws SQLException {
		try {
			MODEL instance;
			ObjectFactory<MODEL> factory = null;
			if (baseDaoImpl != null) {
				factory = (ObjectFactory<MODEL>) baseDaoImpl.getObjectFactory();
			}
			if (factory == null) {
				instance = constructor.newInstance();
			} else {
				instance = factory.createObject(constructor, baseDaoImpl.getDataClass());
			}
			wireNewInstance(baseDaoImpl, instance);
			return instance;
		} catch (Exception e) {
			throw SqlExceptionUtil.create("Could not create object for " + constructor.getDeclaringClass(), e);
		}
	}

	/**
	 * Return true if we can update this object via its ID.
	 */
	public boolean isUpdatable() {
		// to update we must have an id field and there must be more than just the id field
		return (idField != null && fieldTypes.length > 1);
	}

	/**
	 * Return true if one of the fields has {@link DatabaseField#foreignAutoCreate()} enabled.
	 */
	public boolean isForeignAutoCreate() {
		return foreignAutoCreate;
	}

	/**
	 * Return an array with the fields that are {@link ForeignCollection}s or a blank array if none.
	 */
	public FieldType[] getForeignCollections() {
		return foreignCollections;
	}

	/**
	 * Return true if this table information has a field with this columnName as set by
	 * {@link DatabaseField#columnName()} or the field name if not set.
	 */
	public boolean hasColumnName(String columnName) {
		for (FieldType fieldType : fieldTypes) {
			if (fieldType.getColumnName().equals(columnName)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static <MODEL extends Model> void wireNewInstance(BaseDaoImpl<MODEL> baseDaoImpl, MODEL instance) {
		if (instance instanceof BaseDaoEnabled) {
			BaseDaoEnabled<MODEL, Long> daoEnabled = (BaseDaoEnabled<MODEL, Long>) instance;
			daoEnabled.setDao((Dao<MODEL>) baseDaoImpl);
		}
	}
}