package br.cad.dao.sqlite;

import java.sql.SQLException;

import ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.cad.model.Model;
import br.cad.model.system.Resource;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Helper class which creates/updates our database and provides the DAOs.
 * 
 * @author WilliamRodrigues
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "controlacademic.db";
	private static final int DATABASE_VERSION = 1;
	private final String LOG_NAME = getClass().getName();

	private BaseDaoImpl<? extends Model, Long> daoImpl;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Resource.class);
		} catch (SQLException e) {
			Log.e(LOG_NAME, "Could not create new table for Thing", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Resource.class, true);
			onCreate(sqLiteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(LOG_NAME, "Could not upgrade the table for Thing", e);
		}
	}

	public Object getDaoImpl(Class<? extends Model> model) throws SQLException {
		if (daoImpl == null) {
			daoImpl = getDao(model);
		}
		
		return daoImpl;
	}
}
