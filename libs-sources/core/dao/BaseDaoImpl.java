package ormlite.core.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import ormlite.core.Model;
import ormlite.core.db.DatabaseType;
import ormlite.core.field.DataType;
import ormlite.core.field.DatabaseField;
import ormlite.core.field.FieldType;
import ormlite.core.misc.BaseDaoEnabled;
import ormlite.core.misc.SqlExceptionUtil;
import ormlite.core.stmt.DeleteBuilder;
import ormlite.core.stmt.GenericRowMapper;
import ormlite.core.stmt.PreparedDelete;
import ormlite.core.stmt.PreparedQuery;
import ormlite.core.stmt.PreparedUpdate;
import ormlite.core.stmt.QueryBuilder;
import ormlite.core.stmt.SelectArg;
import ormlite.core.stmt.SelectIterator;
import ormlite.core.stmt.StatementBuilder.StatementType;
import ormlite.core.stmt.StatementExecutor;
import ormlite.core.stmt.UpdateBuilder;
import ormlite.core.stmt.Where;
import ormlite.core.support.ConnectionSource;
import ormlite.core.support.DatabaseConnection;
import ormlite.core.support.DatabaseResults;
import ormlite.core.table.DatabaseTableConfig;
import ormlite.core.table.ObjectFactory;
import ormlite.core.table.TableInfo;
import android.util.Log;

/**
 * Base class for the Database Access Objects that handle the reading and writing a class from the database.
 * 
 * <p>
 * This class is also {@link Iterable} which means you can do a {@code for (T obj : dao)} type of loop code to iterate through the table of persisted objects. see
 * {@link #iterator()}.
 * </p>
 * 
 * <p>
 * <b> NOTE: </b> If you are using the Spring type wiring, {@link #initialize} should be called after all of the set methods. In Spring XML, init-method="initialize" should be
 * used.
 * </p>
 * 
 * @param <T>
 *            The class that the code will be operating on.
 * @param <Long>
 *            The class of the Long column associated with the class. The T class does not require an Long field. The class needs an Long parameter however so you can use Void or
 *            Object to satisfy the compiler.
 * @author graywatson
 */
public abstract class BaseDaoImpl<T extends Model> implements Dao<T> {

	private boolean initialized;

	protected StatementExecutor<T, Long> statementExecutor;
	protected DatabaseType databaseType;
	protected final Class<T> dataClass;
	protected DatabaseTableConfig<T> tableConfig;
	protected TableInfo<T> tableInfo;
	protected ConnectionSource connectionSource;
	protected CloseableIterator<T> lastIterator;
	protected ObjectFactory<T> objectFactory;

	private static final ThreadLocal<List<BaseDaoImpl<? extends Model>>> daoConfigLevelLocal = new ThreadLocal<List<BaseDaoImpl<? extends Model>>>() {
		@Override
		protected List<BaseDaoImpl<? extends Model>> initialValue() {
			return new ArrayList<BaseDaoImpl<? extends Model>>(10);
		}
	};
	private static ReferenceObjectCache defaultObjectCache;
	private ObjectCache objectCache;

	protected String modelClassName;
	protected String edPackageModel;
	private final String LOG_TAG = getClass().getSimpleName();

	/*
	 * ******************************************************************************************************************
	 * **************************************************** Gets e Sets *************************************************
	 * ******************************************************************************************************************
	 */
	
	public String getModelClassName() {
		String classname = this.getClass().getSimpleName();
		if (classname.endsWith("DaoSqLite")) {
			return classname.substring(0, classname.length() - 9);
		}
		return null;
	}

	public void setModelClassName(String modelClassName) {
		this.modelClassName = modelClassName;
	}

	public String getEdPackageModel() {
		return edPackageModel;
	}

	public void setEdPackageModel(String edPackageModel) {
		this.edPackageModel = edPackageModel;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getNewModel() {
		try {
			String className = getEdPackageModel() + getModelClassName();
			return (T) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/*
	 * *******************************************************************************************************************
	 * ***************************************************** Metodos *****************************************************
	 * *******************************************************************************************************************
	 */
	
	@Override
	public Integer save(T model) {
		try {
			if(model.getId() == null) {
				return create(model);
			}
			else {
				return update(model);
			}
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao salvar o objeto");
			
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void remove(T model) {
		try {
			Log.i(LOG_TAG, "Objeto " + modelClassName + " removido ID: " + delete(model));
		} catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao remover objeto.");
			
			e.printStackTrace();
		}
	}

	@Override
	public T find(String nameColumn, String value) {
		try {
			return queryForEq(nameColumn, value).get(0);
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao buscar por propriedade.");
			
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public T find(Long id) {
		try {
			return queryForId(id);
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao buscar por ID.");
			
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<T> findAll() {
		try {
			return queryForAll();
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao buscar todos os objetos");
			
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<T> findAll(String nameColumn, String value) {
		try {
			return queryForEq(nameColumn, value);
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao buscar por propriedade.");
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Construct our base DAO using Spring type wiring. The {@link ConnectionSource} must be set with the {@link #setConnectionSource} method afterwards and then the
	 * {@link #initialize()} method must be called. The dataClass provided must have its fields marked with {@link DatabaseField} annotations or the {@link #setTableConfig} method
	 * must be called before the {@link #initialize()} method is called.
	 * 
	 * <p>
	 * If you are using Spring then your should use: init-method="initialize"
	 * </p>
	 * 
	 * @param dataClass
	 *            Class associated with this Dao. This must match the T class parameter.
	 */
	protected BaseDaoImpl(Class<T> dataClass) throws SQLException {
		this(null, dataClass, null);
	}

	/**
	 * Construct our base DAO class. The dataClass provided must have its fields marked with {@link DatabaseField} or javax.persistance annotations.
	 * 
	 * @param connectionSource
	 *            Source of our database connections.
	 * @param dataClass
	 *            Class associated with this Dao. This must match the T class parameter.
	 */
	protected BaseDaoImpl(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
		this(connectionSource, dataClass, null);
	}

	/**
	 * Construct our base DAO class.
	 * 
	 * @param connectionSource
	 *            Source of our database connections.
	 * @param tableConfig
	 *            Hand or Spring wired table configuration information.
	 */
	protected BaseDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
		this(connectionSource, tableConfig.getDataClass(), tableConfig);
	}

	private BaseDaoImpl(ConnectionSource connectionSource, Class<T> dataClass, DatabaseTableConfig<T> tableConfig) throws SQLException {
		this.dataClass = dataClass;
		this.tableConfig = tableConfig;
		if (connectionSource != null) {
			this.connectionSource = connectionSource;
			initialize();
		}
	}

	/**
	 * Initialize the various DAO configurations after the various setters have been called.
	 */
	public void initialize() throws SQLException {
		if (initialized) {
			// just skip it if already initialized
			return;
		}

		if (connectionSource == null) {
			throw new IllegalStateException("connectionSource was never set on " + getClass().getSimpleName());
		}

		databaseType = connectionSource.getDatabaseType();
		if (databaseType == null) {
			throw new IllegalStateException("connectionSource is getting a null DatabaseType in " + getClass().getSimpleName());
		}
		if (tableConfig == null) {
			tableInfo = new TableInfo<T>(connectionSource, this, dataClass);
		} else {
			tableConfig.extractFieldTypes(connectionSource);
			tableInfo = new TableInfo<T>(databaseType, this, tableConfig);
		}
		statementExecutor = new StatementExecutor<T, Long>(databaseType, tableInfo, this);

		/*
		 * This is a bit complex. Initially, when we were configuring the field types, external DAO information would be configured for auto-refresh, foreign BaseDaoEnabled
		 * classes, and foreign-collections. This would cause the system to go recursive and for class loops, a stack overflow.
		 * 
		 * Then we fixed this by putting a level counter in the FieldType constructor that would stop the configurations when we reach some recursion level. But this created some
		 * bad problems because we were using the DaoManager to cache the created DAOs that had been constructed already limited by the level.
		 * 
		 * What we do now is have a 2 phase initialization. The constructor initializes most of the fields but then we go back and call FieldType.configDaoInformation() after we
		 * are done. So for every DAO that is initialized here, we have to see if it is the top DAO. If not we save it for dao configuration later.
		 */
		List<BaseDaoImpl<?>> daoConfigList = daoConfigLevelLocal.get();
		daoConfigList.add(this);
		if (daoConfigList.size() > 1) {
			// if we have recursed then just save the dao for later configuration
			return;
		}

		try {
			/*
			 * WARNING: We do _not_ use an iterator here because we may be adding to the list as we process it and we'll get exceptions otherwise. This is an ArrayList so the
			 * get(i) should be efficient.
			 * 
			 * Also, do _not_ get a copy of daoConfigLevel.doArray because that array may be replaced by another, larger one during the recursion.
			 */
			for (int i = 0; i < daoConfigList.size(); i++) {
				BaseDaoImpl<?> dao = daoConfigList.get(i);

				/*
				 * Here's another complex bit. The first DAO that is being constructed as part of a DAO chain is not yet in the DaoManager cache. If we continue onward we might
				 * come back around and try to configure this DAO again. Forcing it into the cache here makes sure it won't be configured twice. This will cause it to be stuck in
				 * twice when it returns out to the DaoManager but that's a small price to pay. This also applies to self-referencing classes.
				 */
				DaoManager.registerDao(connectionSource, dao);

				try {
					// config our fields which may go recursive
					for (FieldType fieldType : dao.getTableInfo().getFieldTypes()) {
						fieldType.configDaoInformation(connectionSource, dao.getDataClass());
					}
				} catch (SQLException e) {
					// unregister the DAO we just pre-registered
					DaoManager.unregisterDao(connectionSource, dao);
					throw e;
				}

				// it's now been fully initialized
				dao.initialized = true;
			}
		} finally {
			// if we throw we want to clear our class hierarchy here
			daoConfigList.clear();
			daoConfigLevelLocal.remove();
		}
	}

	public T queryForId(Long id) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForId(connection, id, objectCache);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public T queryForFirst(PreparedQuery<T> preparedQuery) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForFirst(connection, preparedQuery, objectCache);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public List<T> queryForAll() throws SQLException {
		checkForInitialized();
		return statementExecutor.queryForAll(connectionSource, objectCache);
	}

	public List<T> queryForEq(String fieldName, Object value) throws SQLException {
		return queryBuilder().where().eq(fieldName, value).query();
	}

	public QueryBuilder<T, Long> queryBuilder() {
		checkForInitialized();
		return new QueryBuilder<T, Long>(databaseType, tableInfo, this);
	}

	public UpdateBuilder<T, Long> updateBuilder() {
		checkForInitialized();
		return new UpdateBuilder<T, Long>(databaseType, tableInfo, this);
	}

	public DeleteBuilder<T, Long> deleteBuilder() {
		checkForInitialized();
		return new DeleteBuilder<T, Long>(databaseType, tableInfo, this);
	}

	public List<T> query(PreparedQuery<T> preparedQuery) throws SQLException {
		checkForInitialized();
		return statementExecutor.query(connectionSource, preparedQuery, objectCache);
	}

	public List<T> queryForMatching(T matchObj) throws SQLException {
		return queryForMatching(matchObj, false);
	}

	public List<T> queryForMatchingArgs(T matchObj) throws SQLException {
		return queryForMatching(matchObj, true);
	}

	public List<T> queryForFieldValues(Map<String, Object> fieldValues) throws SQLException {
		return queryForFieldValues(fieldValues, false);
	}

	public List<T> queryForFieldValuesArgs(Map<String, Object> fieldValues) throws SQLException {
		return queryForFieldValues(fieldValues, true);
	}

	public T queryForSameId(T data) throws SQLException {
		checkForInitialized();
		if (data == null) {
			return null;
		}
		Long id = extractId(data);
		if (id == null) {
			return null;
		} else {
			return queryForId(id);
		}
	}

	public int create(T data) throws SQLException {
		checkForInitialized();
		// ignore creating a null object
		if (data == null) {
			return 0;
		}
		if (data instanceof BaseDaoEnabled) {
			@SuppressWarnings("unchecked")
			BaseDaoEnabled<T, Long> daoEnabled = (BaseDaoEnabled<T, Long>) data;
			daoEnabled.setDao(this);
		}
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return statementExecutor.create(connection, data, objectCache);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public T createIfNotExists(T data) throws SQLException {
		if (data == null) {
			return null;
		}
		T existing = queryForSameId(data);
		if (existing == null) {
			create(data);
			return data;
		} else {
			return existing;
		}
	}

	public CreateOrUpdateStatus createOrUpdate(T data) throws SQLException {
		if (data == null) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
		Long id = extractId(data);
		// assume we need to create it if there is no id
		if (id == null || !idExists(id)) {
			int numRows = create(data);
			return new CreateOrUpdateStatus(true, false, numRows);
		} else {
			int numRows = update(data);
			return new CreateOrUpdateStatus(false, true, numRows);
		}
	}

	public int update(T data) throws SQLException {
		checkForInitialized();
		// ignore updating a null object
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.update(connection, data, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int updateId(T data, Long newId) throws SQLException {
		checkForInitialized();
		// ignore updating a null object
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.updateId(connection, data, newId, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int update(PreparedUpdate<T> preparedUpdate) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return statementExecutor.update(connection, preparedUpdate);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public int refresh(T data) throws SQLException {
		checkForInitialized();
		// ignore refreshing a null object
		if (data == null) {
			return 0;
		} else {
			if (data instanceof BaseDaoEnabled) {
				@SuppressWarnings("unchecked")
				BaseDaoEnabled<T, Long> daoEnabled = (BaseDaoEnabled<T, Long>) data;
				daoEnabled.setDao(this);
			}
			DatabaseConnection connection = connectionSource.getReadOnlyConnection();
			try {
				return statementExecutor.refresh(connection, data, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int delete(T data) throws SQLException {
		checkForInitialized();
		// ignore deleting a null object
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.delete(connection, data, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int deleteById(Long id) throws SQLException {
		checkForInitialized();
		// ignore deleting a null id
		if (id == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.deleteById(connection, id, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int delete(Collection<T> datas) throws SQLException {
		checkForInitialized();
		// ignore deleting a null object
		if (datas == null || datas.isEmpty()) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.deleteObjects(connection, datas, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int deleteIds(Collection<Long> ids) throws SQLException {
		checkForInitialized();
		// ignore deleting a null object
		if (ids == null || ids.isEmpty()) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.deleteIds(connection, ids, objectCache);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return statementExecutor.delete(connection, preparedDelete);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public CloseableIterator<T> iterator() {
		return iterator(DatabaseConnection.DEFAULT_RESULT_FLAGS);
	}

	public CloseableIterator<T> closeableIterator() {
		return iterator(DatabaseConnection.DEFAULT_RESULT_FLAGS);
	}

	public CloseableIterator<T> iterator(int resultFlags) {
		checkForInitialized();
		lastIterator = createIterator(resultFlags);
		return lastIterator;
	}

	public CloseableWrappedIterable<T> getWrappedIterable() {
		checkForInitialized();
		return new CloseableWrappedIterableImpl<T>(new CloseableIterable<T>() {
			public Iterator<T> iterator() {
				return closeableIterator();
			}

			public CloseableIterator<T> closeableIterator() {
				try {
					return BaseDaoImpl.this.createIterator(DatabaseConnection.DEFAULT_RESULT_FLAGS);
				} catch (Exception e) {
					throw new IllegalStateException("Could not build iterator for " + dataClass, e);
				}
			}
		});
	}

	public CloseableWrappedIterable<T> getWrappedIterable(final PreparedQuery<T> preparedQuery) {
		checkForInitialized();
		return new CloseableWrappedIterableImpl<T>(new CloseableIterable<T>() {
			public Iterator<T> iterator() {
				return closeableIterator();
			}

			public CloseableIterator<T> closeableIterator() {
				try {
					return BaseDaoImpl.this.createIterator(preparedQuery, DatabaseConnection.DEFAULT_RESULT_FLAGS);
				} catch (Exception e) {
					throw new IllegalStateException("Could not build prepared-query iterator for " + dataClass, e);
				}
			}
		});
	}

	public void closeLastIterator() throws SQLException {
		if (lastIterator != null) {
			lastIterator.close();
			lastIterator = null;
		}
	}

	public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery) throws SQLException {
		return iterator(preparedQuery, DatabaseConnection.DEFAULT_RESULT_FLAGS);
	}

	public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery, int resultFlags) throws SQLException {
		checkForInitialized();
		lastIterator = createIterator(preparedQuery, resultFlags);
		return lastIterator;
	}

	public GenericRawResults<String[]> queryRaw(String query, String... arguments) throws SQLException {
		checkForInitialized();
		try {
			return statementExecutor.queryRaw(connectionSource, query, arguments, objectCache);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not perform raw query for " + query, e);
		}
	}

	public <GR> GenericRawResults<GR> queryRaw(String query, RawRowMapper<GR> mapper, String... arguments) throws SQLException {
		checkForInitialized();
		try {
			return statementExecutor.queryRaw(connectionSource, query, mapper, arguments, objectCache);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not perform raw query for " + query, e);
		}
	}

	public <UO> GenericRawResults<UO> queryRaw(String query, DataType[] columnTypes, RawRowObjectMapper<UO> mapper, String... arguments) throws SQLException {
		checkForInitialized();
		try {
			return statementExecutor.queryRaw(connectionSource, query, columnTypes, mapper, arguments, objectCache);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not perform raw query for " + query, e);
		}
	}

	public GenericRawResults<Object[]> queryRaw(String query, DataType[] columnTypes, String... arguments) throws SQLException {
		checkForInitialized();
		try {
			return statementExecutor.queryRaw(connectionSource, query, columnTypes, arguments, objectCache);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not perform raw query for " + query, e);
		}
	}

	public long queryRawValue(String query, String... arguments) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForLong(connection, query, arguments);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not perform raw value query for " + query, e);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public int executeRaw(String statement, String... arguments) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return statementExecutor.executeRaw(connection, statement, arguments);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not run raw execute statement " + statement, e);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public int executeRawNoArgs(String statement) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return statementExecutor.executeRawNoArgs(connection, statement);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not run raw execute statement " + statement, e);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public int updateRaw(String statement, String... arguments) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return statementExecutor.updateRaw(connection, statement, arguments);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not run raw update statement " + statement, e);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public <CT> CT callBatchTasks(Callable<CT> callable) throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			/*
			 * We need to save the connection because we are going to be disabling auto-commit on it and we don't want pooled connection factories to give us another connection
			 * where auto-commit might still be enabled.
			 */
			boolean saved = connectionSource.saveSpecialConnection(connection);
			return statementExecutor.callBatchTasks(connection, saved, callable);
		} finally {
			connectionSource.clearSpecialConnection(connection);
			connectionSource.releaseConnection(connection);
		}
	}

	public String objectToString(T data) {
		checkForInitialized();
		return tableInfo.objectToString(data);
	}

	public boolean objectsEqual(T data1, T data2) throws SQLException {
		checkForInitialized();
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			Object fieldObj1 = fieldType.extractJavaFieldValue(data1);
			Object fieldObj2 = fieldType.extractJavaFieldValue(data2);
			// we can't just do fieldObj1.equals(fieldObj2) because of byte[].equals()
			if (!fieldType.getDataPersister().dataIsEqual(fieldObj1, fieldObj2)) {
				return false;
			}
		}
		return true;
	}

	public Long extractId(T data) throws SQLException {
		checkForInitialized();
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Class " + dataClass + " does not have an id field");
		}
		Long id = (Long) idField.extractJavaFieldValue(data);
		return id;
	}

	public Class<T> getDataClass() {
		return dataClass;
	}

	public FieldType findForeignFieldType(Class<?> clazz) {
		checkForInitialized();
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			if (fieldType.getType() == clazz) {
				return fieldType;
			}
		}
		return null;
	}

	public boolean isUpdatable() {
		return tableInfo.isUpdatable();
	}

	public boolean isTableExists() throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return connection.isTableExists(tableInfo.getTableName());
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public long countOf() throws SQLException {
		checkForInitialized();
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForCountStar(connection);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public long countOf(PreparedQuery<T> preparedQuery) throws SQLException {
		checkForInitialized();
		if (preparedQuery.getType() != StatementType.SELECT_LONG) {
			throw new IllegalArgumentException("Prepared query is not of type " + StatementType.SELECT_LONG + ", did you call QueryBuilder.setCountOf(true)?");
		}
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForLong(connection, preparedQuery);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public void assignEmptyForeignCollection(T parent, String fieldName) throws SQLException {
		makeEmptyForeignCollection(parent, fieldName);
	}

	public <FT> ForeignCollection<FT> getEmptyForeignCollection(String fieldName) throws SQLException {
		return makeEmptyForeignCollection(null, fieldName);
	}

	public void setObjectCache(boolean enabled) throws SQLException {
		if (enabled) {
			if (objectCache == null) {
				if (tableInfo.getIdField() == null) {
					throw new SQLException("Class " + dataClass + " must have an id field to enable the object cache");
				}
				synchronized (BaseDaoImpl.class) {
					if (defaultObjectCache == null) {
						// only make one
						defaultObjectCache = ReferenceObjectCache.makeWeakCache();
					}
					objectCache = defaultObjectCache;
				}
				objectCache.registerClass(dataClass);
			}
		} else {
			if (objectCache != null) {
				objectCache.clear(dataClass);
				objectCache = null;
			}
		}
	}

	public void setObjectCache(ObjectCache objectCache) throws SQLException {
		if (objectCache == null) {
			if (this.objectCache != null) {
				// help with GC-ing
				this.objectCache.clear(dataClass);
				this.objectCache = null;
			}
		} else {
			if (this.objectCache != null && this.objectCache != objectCache) {
				// help with GC-ing
				this.objectCache.clear(dataClass);
			}
			if (tableInfo.getIdField() == null) {
				throw new SQLException("Class " + dataClass + " must have an id field to enable the object cache");
			}
			this.objectCache = objectCache;
			this.objectCache.registerClass(dataClass);
		}
	}

	public ObjectCache getObjectCache() {
		return objectCache;
	}

	public void clearObjectCache() {
		if (objectCache != null) {
			objectCache.clear(dataClass);
		}
	}

	/**
	 * Special call mostly used in testing to clear the internal object caches so we can reset state.
	 */
	public synchronized static void clearAllInternalObjectCaches() {
		if (defaultObjectCache != null) {
			defaultObjectCache.clearAll();
			defaultObjectCache = null;
		}
	}

	public T mapSelectStarRow(DatabaseResults results) throws SQLException {
		return statementExecutor.getSelectStarRowMapper().mapRow(results);
	}

	public GenericRowMapper<T> getSelectStarRowMapper() throws SQLException {
		return statementExecutor.getSelectStarRowMapper();
	}

	public RawRowMapper<T> getRawRowMapper() {
		return statementExecutor.getRawRowMapper();
	}

	public boolean idExists(Long id) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.ifExists(connection, id);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public DatabaseConnection startThreadConnection() throws SQLException {
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		connectionSource.saveSpecialConnection(connection);
		return connection;
	}

	public void endThreadConnection(DatabaseConnection connection) throws SQLException {
		connectionSource.clearSpecialConnection(connection);
		connectionSource.releaseConnection(connection);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			setAutoCommit(connection, autoCommit);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public void setAutoCommit(DatabaseConnection connection, boolean autoCommit) throws SQLException {
		connection.setAutoCommit(autoCommit);
	}

	public boolean isAutoCommit() throws SQLException {
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			return isAutoCommit(connection);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public boolean isAutoCommit(DatabaseConnection connection) throws SQLException {
		return connection.isAutoCommit();
	}

	public void commit(DatabaseConnection connection) throws SQLException {
		connection.commit(null);
	}

	public void rollBack(DatabaseConnection connection) throws SQLException {
		connection.rollback(null);
	}

	public ObjectFactory<T> getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(ObjectFactory<T> objectFactory) {
		checkForInitialized();
		this.objectFactory = objectFactory;
	}

	/**
	 * Returns the table configuration information associated with the Dao's class or null if none.
	 */
	public DatabaseTableConfig<T> getTableConfig() {
		return tableConfig;
	}

	/**
	 * Used by internal classes to get the table information structure for the Dao's class.
	 */
	public TableInfo<T> getTableInfo() {
		return tableInfo;
	}

	public ConnectionSource getConnectionSource() {
		return connectionSource;
	}

	public void setConnectionSource(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

	/**
	 * Used if you want to configure the class for the Dao by hand or with spring instead of using the {@link DatabaseField} annotation in the class. This must be called
	 * <i>before</i> {@link #initialize}.
	 */
	public void setTableConfig(DatabaseTableConfig<T> tableConfig) {
		this.tableConfig = tableConfig;
	}

	/**
	 * Helper method to create a Dao object without having to define a class. Dao classes are supposed to be convenient but if you have a lot of classes, they can seem to be a
	 * pain.
	 * 
	 * <p>
	 * <b>NOTE:</b> You should use {@link DaoManager#createDao(ConnectionSource, DatabaseTableConfig)} instead of this method so you won't have to create the DAO multiple times.
	 * </p>
	 */
	static <T extends Model, ID> Dao<T> createDao(ConnectionSource connectionSource, Class<T> clazz) throws SQLException {
		return new BaseDaoImpl<T>(connectionSource, clazz) {
		};
	}

	/**
	 * Helper method to create a Dao object used by some internal methods that already have the {@link TableInfo}.
	 * 
	 * <p>
	 * <b>NOTE:</b> You should use {@link DaoManager#createDao(ConnectionSource, DatabaseTableConfig)} instead of this method so you won't have to create the DAO multiple times.
	 * </p>
	 */
	static <T extends Model, ID> Dao<T> createDao(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
		return new BaseDaoImpl<T>(connectionSource, tableConfig) {
		};
	}

	protected void checkForInitialized() {
		if (!initialized) {
			throw new IllegalStateException("you must call initialize() before you can use the dao");
		}
	}

	private <FT> ForeignCollection<FT> makeEmptyForeignCollection(T parent, String fieldName) throws SQLException {
		checkForInitialized();
		Long id;
		if (parent == null) {
			id = null;
		} else {
			id = extractId(parent);
		}
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			if (fieldType.getColumnName().equals(fieldName)) {
				@SuppressWarnings("unchecked")
				ForeignCollection<FT> collection = (ForeignCollection<FT>) fieldType.buildForeignCollection(parent, id);
				if (parent != null) {
					fieldType.assignField(parent, collection, true, null);
				}
				return collection;
			}
		}
		throw new IllegalArgumentException("Could not find a field named " + fieldName);
	}

	private CloseableIterator<T> createIterator(int resultFlags) {
		try {
			SelectIterator<T, Long> iterator = statementExecutor.buildIterator(this, connectionSource, resultFlags, objectCache);
			return iterator;
		} catch (Exception e) {
			throw new IllegalStateException("Could not build iterator for " + dataClass, e);
		}
	}

	private CloseableIterator<T> createIterator(PreparedQuery<T> preparedQuery, int resultFlags) throws SQLException {
		try {
			SelectIterator<T, Long> iterator = statementExecutor.buildIterator(this, connectionSource, preparedQuery, objectCache, resultFlags);
			return iterator;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not build prepared-query iterator for " + dataClass, e);
		}
	}

	private List<T> queryForMatching(T matchObj, boolean useArgs) throws SQLException {
		checkForInitialized();
		QueryBuilder<T, Long> qb = queryBuilder();
		Where<T, Long> where = qb.where();
		int fieldC = 0;
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			Object fieldValue = fieldType.getFieldValueIfNotDefault(matchObj);
			if (fieldValue != null) {
				if (useArgs) {
					fieldValue = new SelectArg(fieldValue);
				}
				where.eq(fieldType.getColumnName(), fieldValue);
				fieldC++;
			}
		}
		if (fieldC == 0) {
			return Collections.emptyList();
		} else {
			where.and(fieldC);
			return qb.query();
		}
	}

	private List<T> queryForFieldValues(Map<String, Object> fieldValues, boolean useArgs) throws SQLException {
		checkForInitialized();
		QueryBuilder<T, Long> qb = queryBuilder();
		Where<T, Long> where = qb.where();
		for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
			Object fieldValue = entry.getValue();
			if (useArgs) {
				fieldValue = new SelectArg(fieldValue);
			}
			where.eq(entry.getKey(), fieldValue);
		}
		if (fieldValues.size() == 0) {
			return Collections.emptyList();
		} else {
			where.and(fieldValues.size());
			return qb.query();
		}
	}
}
