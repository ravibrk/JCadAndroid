package ormlite.core.stmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ormlite.core.Model;
import ormlite.core.dao.CloseableIterator;
import ormlite.core.dao.GenericRawResults;
import ormlite.core.dao.ObjectCache;
import ormlite.core.support.CompiledStatement;
import ormlite.core.support.ConnectionSource;
import ormlite.core.support.DatabaseConnection;

/**
 * Handler for our raw results objects which does the conversion for various different results: String[], Object[], and
 * user defined <T>.
 * 
 * @author graywatson
 */
@SuppressWarnings("unchecked")
public class RawResultsImpl<T, M extends Model> implements GenericRawResults<T> {

	private SelectIterator<M, Void> iterator;
	private final String[] columnNames;

	public RawResultsImpl(ConnectionSource connectionSource, DatabaseConnection connection, String query, Class<?> clazz, CompiledStatement compiledStmt, GenericRowMapper<T> rowMapper,
			ObjectCache objectCache) throws SQLException {
		iterator = new SelectIterator<M, Void>(clazz, null, (GenericRowMapper<M>) rowMapper, connectionSource, connection, compiledStmt, query, objectCache);
		/*
		 * NOTE: we _have_ to get these here before the results object is closed if there are no results
		 */
		this.columnNames = iterator.getRawResults().getColumnNames();
	}

	public int getNumberColumns() {
		return columnNames.length;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public List<T> getResults() throws SQLException {
		List<T> results = new ArrayList<T>();
		try {
			while (iterator.hasNext()) {
				results.add((T) iterator.next());
			}
			return results;
		} finally {
			iterator.close();
		}
	}

	public T getFirstResult() throws SQLException {
		try {
			if (iterator.hasNextThrow()) {
				return (T) iterator.nextThrow();
			} else {
				return null;
			}
		} finally {
			close();
		}
	}

	public CloseableIterator<T> iterator() {
		return (CloseableIterator<T>) iterator;
	}

	public CloseableIterator<T> closeableIterator() {
		return (CloseableIterator<T>) iterator;
	}

	public void close() throws SQLException {
		if (iterator != null) {
			iterator.close();
			iterator = null;
		}
	}
}
