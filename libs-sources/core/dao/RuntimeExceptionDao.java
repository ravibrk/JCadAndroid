package ormlite.core.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import ormlite.core.Model;
import ormlite.core.dao.Dao.CreateOrUpdateStatus;
import ormlite.core.field.DataType;
import ormlite.core.field.FieldType;
import ormlite.core.logger.Log.Level;
import ormlite.core.logger.Logger;
import ormlite.core.logger.LoggerFactory;
import ormlite.core.stmt.DeleteBuilder;
import ormlite.core.stmt.GenericRowMapper;
import ormlite.core.stmt.PreparedDelete;
import ormlite.core.stmt.PreparedQuery;
import ormlite.core.stmt.PreparedUpdate;
import ormlite.core.stmt.QueryBuilder;
import ormlite.core.stmt.UpdateBuilder;
import ormlite.core.support.ConnectionSource;
import ormlite.core.support.DatabaseConnection;
import ormlite.core.support.DatabaseResults;
import ormlite.core.table.DatabaseTableConfig;
import ormlite.core.table.ObjectFactory;

/**
 * Proxy to a {@link Dao} that wraps each Exception and rethrows it as RuntimeException. You can use this if your usage
 * pattern is to ignore all exceptions. That's not a pattern that I like so it's not the default.
 * 
 * <p>
 * 
 * <pre>
 * RuntimeExceptionDao&lt;Account, String&gt; accountDao = RuntimeExceptionDao.createDao(connectionSource, Account.class);
 * </pre>
 * 
 * </p>
 * 
 * @author graywatson
 */
public class RuntimeExceptionDao<MODEL extends Model, DAO extends Dao<MODEL>> implements CloseableIterable<MODEL> {

	/*
	 * We use debug here because we don't want these messages to be logged by default. The user will need to turn on
	 * logging for this class to DEBUG to see the messages.
	 */
	private static final Level LOG_LEVEL = Level.DEBUG;

	private DAO dao;
	private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

	public RuntimeExceptionDao(DAO dao) {
		this.dao = dao;
	}

	/**
	 * Call through to {@link DaoManager#createDao(ConnectionSource, Class)} with the returned DAO wrapped in a
	 * RuntimeExceptionDao.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <MODEL extends Model, DAO extends Dao<MODEL>> RuntimeExceptionDao<MODEL, DAO> createDao(ConnectionSource connectionSource, Class<MODEL> clazz) throws SQLException {
		DAO castDao = (DAO) DaoManager.createDao(connectionSource, clazz);
		return (RuntimeExceptionDao<MODEL, DAO>) new RuntimeExceptionDao((DAO) castDao);
	}

	/**
	 * Call through to {@link DaoManager#createDao(ConnectionSource, DatabaseTableConfig)} with the returned DAO wrapped
	 * in a RuntimeExceptionDao.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <MODEL extends Model, DAO extends Dao<MODEL>> RuntimeExceptionDao<MODEL, DAO> createDao(ConnectionSource connectionSource, DatabaseTableConfig<MODEL> tableConfig) throws SQLException {
		DAO castDao = (DAO) DaoManager.createDao(connectionSource, tableConfig);
		return (RuntimeExceptionDao<MODEL, DAO>) new RuntimeExceptionDao((DAO) castDao);
	}

	/**
	 * @see Dao#queryForId(Object)
	 */
	public MODEL queryForId(Long id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			logMessage(e, "queryForId threw exception on: " + id);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForFirst(PreparedQuery)
	 */
	public MODEL queryForFirst(PreparedQuery<MODEL> preparedQuery) {
		try {
			return dao.queryForFirst(preparedQuery);
		} catch (SQLException e) {
			logMessage(e, "queryForFirst threw exception on: " + preparedQuery);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForAll()
	 */
	public List<MODEL> queryForAll() {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			logMessage(e, "queryForAll threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForEq(String, Object)
	 */
	public List<MODEL> queryForEq(String fieldName, Object value) {
		try {
			return dao.queryForEq(fieldName, value);
		} catch (SQLException e) {
			logMessage(e, "queryForEq threw exception on: " + fieldName);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForMatching(Object)
	 */
	public List<MODEL> queryForMatching(MODEL matchObj) {
		try {
			return dao.queryForMatching(matchObj);
		} catch (SQLException e) {
			logMessage(e, "queryForMatching threw exception on: " + matchObj);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForMatchingArgs(Object)
	 */
	public List<MODEL> queryForMatchingArgs(MODEL matchObj) {
		try {
			return dao.queryForMatchingArgs(matchObj);
		} catch (SQLException e) {
			logMessage(e, "queryForMatchingArgs threw exception on: " + matchObj);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForFieldValues(Map)
	 */
	public List<MODEL> queryForFieldValues(Map<String, Object> fieldValues) {
		try {
			return dao.queryForFieldValues(fieldValues);
		} catch (SQLException e) {
			logMessage(e, "queryForFieldValues threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForFieldValuesArgs(Map)
	 */
	public List<MODEL> queryForFieldValuesArgs(Map<String, Object> fieldValues) {
		try {
			return dao.queryForFieldValuesArgs(fieldValues);
		} catch (SQLException e) {
			logMessage(e, "queryForFieldValuesArgs threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForSameId(Object)
	 */
	public MODEL queryForSameId(MODEL data) {
		try {
			return dao.queryForSameId(data);
		} catch (SQLException e) {
			logMessage(e, "queryForSameId threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryBuilder()
	 */
	public QueryBuilder<MODEL, Long> queryBuilder() {
		return dao.queryBuilder();
	}

	/**
	 * @see Dao#updateBuilder()
	 */
	public UpdateBuilder<MODEL, Long> updateBuilder() {
		return dao.updateBuilder();
	}

	/**
	 * @see Dao#deleteBuilder()
	 */
	public DeleteBuilder<MODEL, Long> deleteBuilder() {
		return dao.deleteBuilder();
	}

	/**
	 * @see Dao#query(PreparedQuery)
	 */
	public List<MODEL> query(PreparedQuery<MODEL> preparedQuery) {
		try {
			return dao.query(preparedQuery);
		} catch (SQLException e) {
			logMessage(e, "query threw exception on: " + preparedQuery);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#create(Object)
	 */
	public int create(MODEL data) {
		try {
			return dao.create(data);
		} catch (SQLException e) {
			logMessage(e, "create threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#createIfNotExists(Object)
	 */
	public MODEL createIfNotExists(MODEL data) {
		try {
			return dao.createIfNotExists(data);
		} catch (SQLException e) {
			logMessage(e, "createIfNotExists threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#createOrUpdate(Object)
	 */
	public CreateOrUpdateStatus createOrUpdate(MODEL data) {
		try {
			return dao.createOrUpdate(data);
		} catch (SQLException e) {
			logMessage(e, "createOrUpdate threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#update(Object)
	 */
	public int update(MODEL data) {
		try {
			return dao.update(data);
		} catch (SQLException e) {
			logMessage(e, "update threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#updateId(Object, Object)
	 */
	public int updateId(MODEL data, Long newId) {
		try {
			return dao.updateId(data, newId);
		} catch (SQLException e) {
			logMessage(e, "updateId threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#update(PreparedUpdate)
	 */
	public int update(PreparedUpdate<MODEL> preparedUpdate) {
		try {
			return dao.update(preparedUpdate);
		} catch (SQLException e) {
			logMessage(e, "update threw exception on: " + preparedUpdate);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#refresh(Object)
	 */
	public int refresh(MODEL data) {
		try {
			return dao.refresh(data);
		} catch (SQLException e) {
			logMessage(e, "refresh threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#delete(Object)
	 */
	public int delete(MODEL data) {
		try {
			return dao.delete(data);
		} catch (SQLException e) {
			logMessage(e, "delete threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#deleteById(Object)
	 */
	public int deleteById(Long id) {
		try {
			return dao.deleteById(id);
		} catch (SQLException e) {
			logMessage(e, "deleteById threw exception on: " + id);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#delete(Collection)
	 */
	public int delete(Collection<MODEL> datas) {
		try {
			return dao.delete(datas);
		} catch (SQLException e) {
			logMessage(e, "delete threw exception on: " + datas);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#deleteIds(Collection)
	 */
	public int deleteIds(Collection<Long> ids) {
		try {
			return dao.deleteIds(ids);
		} catch (SQLException e) {
			logMessage(e, "deleteIds threw exception on: " + ids);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#delete(PreparedDelete)
	 */
	public int delete(PreparedDelete<MODEL> preparedDelete) {
		try {
			return dao.delete(preparedDelete);
		} catch (SQLException e) {
			logMessage(e, "delete threw exception on: " + preparedDelete);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#iterator()
	 */
	public CloseableIterator<MODEL> iterator() {
		return (CloseableIterator<MODEL>) dao.iterator();
	}

	public CloseableIterator<MODEL> closeableIterator() {
		return (CloseableIterator<MODEL>) dao.closeableIterator();
	}

	/**
	 * @see Dao#iterator(int)
	 */
	public CloseableIterator<MODEL> iterator(int resultFlags) {
		return (CloseableIterator<MODEL>) dao.iterator(resultFlags);
	}

	/**
	 * @see Dao#getWrappedIterable()
	 */
	public CloseableWrappedIterable<MODEL> getWrappedIterable() {
		return (CloseableWrappedIterable<MODEL>) dao.getWrappedIterable();
	}

	/**
	 * @see Dao#getWrappedIterable(PreparedQuery)
	 */
	public CloseableWrappedIterable<MODEL> getWrappedIterable(PreparedQuery<MODEL> preparedQuery) {
		return (CloseableWrappedIterable<MODEL>) dao.getWrappedIterable(preparedQuery);
	}

	/**
	 * @see Dao#closeLastIterator()
	 */
	public void closeLastIterator() {
		try {
			dao.closeLastIterator();
		} catch (SQLException e) {
			logMessage(e, "closeLastIterator threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#iterator(PreparedQuery)
	 */
	public CloseableIterator<MODEL> iterator(PreparedQuery<MODEL> preparedQuery) {
		try {
			return (CloseableIterator<MODEL>) dao.iterator(preparedQuery);
		} catch (SQLException e) {
			logMessage(e, "iterator threw exception on: " + preparedQuery);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#iterator(PreparedQuery, int)
	 */
	public CloseableIterator<MODEL> iterator(PreparedQuery<MODEL> preparedQuery, int resultFlags) {
		try {
			return (CloseableIterator<MODEL>) dao.iterator(preparedQuery, resultFlags);
		} catch (SQLException e) {
			logMessage(e, "iterator threw exception on: " + preparedQuery);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryRaw(String, String...)
	 */
	public GenericRawResults<String[]> queryRaw(String query, String... arguments) {
		try {
			return (GenericRawResults<String[]>) dao.queryRaw(query, arguments);
		} catch (SQLException e) {
			logMessage(e, "queryRaw threw exception on: " + query);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryRawValue(String, String...)
	 */
	public long queryRawValue(String query, String... arguments) {
		try {
			return dao.queryRawValue(query, arguments);
		} catch (SQLException e) {
			logMessage(e, "queryRawValue threw exception on: " + query);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryRaw(String, RawRowMapper, String...)
	 */
	public <UO> GenericRawResults<UO> queryRaw(String query, RawRowMapper<UO> mapper, String... arguments) {
		try {
			return (GenericRawResults<UO>) dao.queryRaw(query, (ormlite.core.dao.RawRowMapper<UO>) mapper, arguments);
		} catch (SQLException e) {
			logMessage(e, "queryRaw threw exception on: " + query);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryRaw(String, DataType[], RawRowObjectMapper, String...)
	 */
	public <UO> GenericRawResults<UO> queryRaw(String query, DataType[] columnTypes, RawRowObjectMapper<UO> mapper,
			String... arguments) {
		try {
			return (GenericRawResults<UO>) dao.queryRaw(query, columnTypes, (ormlite.core.dao.RawRowObjectMapper<UO>) mapper, arguments);
		} catch (SQLException e) {
			logMessage(e, "queryRaw threw exception on: " + query);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryRaw(String, DataType[], String...)
	 */
	public GenericRawResults<Object[]> queryRaw(String query, DataType[] columnTypes, String... arguments) {
		try {
			return (GenericRawResults<Object[]>) dao.queryRaw(query, columnTypes, arguments);
		} catch (SQLException e) {
			logMessage(e, "queryRaw threw exception on: " + query);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#executeRaw(String, String...)
	 */
	public int executeRaw(String statement, String... arguments) {
		try {
			return dao.executeRaw(statement, arguments);
		} catch (SQLException e) {
			logMessage(e, "executeRaw threw exception on: " + statement);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#executeRawNoArgs(String)
	 */
	public int executeRawNoArgs(String statement) {
		try {
			return dao.executeRawNoArgs(statement);
		} catch (SQLException e) {
			logMessage(e, "executeRawNoArgs threw exception on: " + statement);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#updateRaw(String, String...)
	 */
	public int updateRaw(String statement, String... arguments) {
		try {
			return dao.updateRaw(statement, arguments);
		} catch (SQLException e) {
			logMessage(e, "updateRaw threw exception on: " + statement);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#callBatchTasks(Callable)
	 */
	public <CT> CT callBatchTasks(Callable<CT> callable) {
		try {
			return dao.callBatchTasks(callable);
		} catch (Exception e) {
			logMessage(e, "callBatchTasks threw exception on: " + callable);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#objectToString(Object)
	 */
	public String objectToString(MODEL data) {
		return dao.objectToString(data);
	}

	/**
	 * @see Dao#objectsEqual(Object, Object)
	 */
	public boolean objectsEqual(MODEL data1, MODEL data2) {
		try {
			return dao.objectsEqual(data1, data2);
		} catch (SQLException e) {
			logMessage(e, "objectsEqual threw exception on: " + data1 + " and " + data2);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#extractId(Object)
	 */
	public Long extractId(MODEL data) {
		try {
			return dao.extractId(data);
		} catch (SQLException e) {
			logMessage(e, "extractId threw exception on: " + data);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#getDataClass()
	 */
	public Class<MODEL> getDataClass() {
		return dao.getDataClass();
	}

	/**
	 * @see Dao#findForeignFieldType(Class)
	 */
	public FieldType findForeignFieldType(Class<?> clazz) {
		return dao.findForeignFieldType(clazz);
	}

	/**
	 * @see Dao#isUpdatable()
	 */
	public boolean isUpdatable() {
		return dao.isUpdatable();
	}

	/**
	 * @see Dao#isTableExists()
	 */
	public boolean isTableExists() {
		try {
			return dao.isTableExists();
		} catch (SQLException e) {
			logMessage(e, "isTableExists threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#countOf()
	 */
	public long countOf() {
		try {
			return dao.countOf();
		} catch (SQLException e) {
			logMessage(e, "countOf threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#countOf(PreparedQuery)
	 */
	public long countOf(PreparedQuery<MODEL> preparedQuery) {
		try {
			return dao.countOf(preparedQuery);
		} catch (SQLException e) {
			logMessage(e, "countOf threw exception on " + preparedQuery);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#assignEmptyForeignCollection(Object, String)
	 */
	public void assignEmptyForeignCollection(MODEL parent, String fieldName) {
		try {
			dao.assignEmptyForeignCollection(parent, fieldName);
		} catch (SQLException e) {
			logMessage(e, "assignEmptyForeignCollection threw exception on " + fieldName);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#getEmptyForeignCollection(String)
	 */
	@SuppressWarnings("unchecked")
	public <FT> ForeignCollection<FT> getEmptyForeignCollection(String fieldName) {
		try {
			return (ForeignCollection<FT>) dao.getEmptyForeignCollection(fieldName);
		} catch (SQLException e) {
			logMessage(e, "getEmptyForeignCollection threw exception on " + fieldName);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#setObjectCache(boolean)
	 */
	public void setObjectCache(boolean enabled) {
		try {
			dao.setObjectCache(enabled);
		} catch (SQLException e) {
			logMessage(e, "setObjectCache(" + enabled + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#getObjectCache()
	 */
	public ObjectCache getObjectCache() {
		return (ObjectCache) dao.getObjectCache();
	}

	/**
	 * @see Dao#setObjectCache(ObjectCache)
	 */
	@SuppressWarnings("unchecked")
	public void setObjectCache(ObjectCache objectCache) {
		dao.setObjectFactory((ObjectFactory<MODEL>) objectCache);
	}

	/**
	 * @see Dao#clearObjectCache()
	 */
	public void clearObjectCache() {
		dao.clearObjectCache();
	}

	/**
	 * @see Dao#mapSelectStarRow(DatabaseResults)
	 */
	public MODEL mapSelectStarRow(DatabaseResults results) {
		try {
			return dao.mapSelectStarRow(results);
		} catch (SQLException e) {
			logMessage(e, "mapSelectStarRow threw exception on results");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#getSelectStarRowMapper()
	 */
	public GenericRowMapper<MODEL> getSelectStarRowMapper() {
		try {
			return dao.getSelectStarRowMapper();
		} catch (SQLException e) {
			logMessage(e, "getSelectStarRowMapper threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#idExists(Object)
	 */
	public boolean idExists(Long id) {
		try {
			return dao.idExists(id);
		} catch (SQLException e) {
			logMessage(e, "idExists threw exception on " + id);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#startThreadConnection()
	 */
	public DatabaseConnection startThreadConnection() {
		try {
			return dao.startThreadConnection();
		} catch (SQLException e) {
			logMessage(e, "startThreadConnection() threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#endThreadConnection(DatabaseConnection)
	 */
	public void endThreadConnection(DatabaseConnection connection) {
		try {
			dao.endThreadConnection(connection);
		} catch (SQLException e) {
			logMessage(e, "endThreadConnection(" + connection + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#setAutoCommit(boolean)
	 */
	@Deprecated
	public void setAutoCommit(boolean autoCommit) {
		try {
			dao.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			logMessage(e, "setAutoCommit(" + autoCommit + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#setAutoCommit(DatabaseConnection, boolean)
	 */
	public void setAutoCommit(DatabaseConnection connection, boolean autoCommit) {
		try {
			dao.setAutoCommit(connection, autoCommit);
		} catch (SQLException e) {
			logMessage(e, "setAutoCommit(" + connection + "," + autoCommit + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#isAutoCommit()
	 */
	@Deprecated
	public boolean isAutoCommit() {
		try {
			return dao.isAutoCommit();
		} catch (SQLException e) {
			logMessage(e, "isAutoCommit() threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#isAutoCommit(DatabaseConnection)
	 */
	public boolean isAutoCommit(DatabaseConnection connection) {
		try {
			return dao.isAutoCommit(connection);
		} catch (SQLException e) {
			logMessage(e, "isAutoCommit(" + connection + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#commit(DatabaseConnection)
	 */
	public void commit(DatabaseConnection connection) {
		try {
			dao.commit(connection);
		} catch (SQLException e) {
			logMessage(e, "commit(" + connection + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#rollBack(DatabaseConnection)
	 */
	public void rollBack(DatabaseConnection connection) {
		try {
			dao.rollBack(connection);
		} catch (SQLException e) {
			logMessage(e, "rollBack(" + connection + ") threw exception");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#setObjectFactory(ObjectFactory)
	 */
	public void setObjectFactory(ObjectFactory<MODEL> objectFactory) {
		dao.setObjectFactory(objectFactory);
	}

	/**
	 * @see Dao#getRawRowMapper()
	 */
	public RawRowMapper<MODEL> getRawRowMapper() {
		return (RawRowMapper<MODEL>) dao.getRawRowMapper();
	}

	/**
	 * @see Dao#getConnectionSource()
	 */
	public ConnectionSource getConnectionSource() {
		return dao.getConnectionSource();
	}

	private void logMessage(Exception e, String message) {
		logger.log(LOG_LEVEL, e, message);
	}
}
