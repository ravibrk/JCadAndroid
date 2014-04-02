package br.cad.dao.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides the DAOs used by the other classes.
 */
@SuppressWarnings({ "rawtypes" })
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "controlacademic.db";
	private static final int DATABASE_VERSION = 1;
//	private final String LOG_NAME = getClass().getName();

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		createTables(connectionSource);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		dropTables(connectionSource);

		onCreate(sqLiteDatabase, connectionSource);
	}
	
	public List<Class> findClassXML() throws FileNotFoundException, ClassNotFoundException {
		File file = new File("src/br/car/model");
		File afile[] = file.listFiles();
		for(File f : afile) {
			System.out.println(f.getName());
		}
		
//		JAXBContext.newInstance(XmlModel.class);
		

		List<Class> classes = new ArrayList<Class>();
//		while (reader.hasNext()) {
//			if (reader.hasText()) {
//				String obj = reader.getText();
//				
//				if (obj.contains("br.cad.model")) {
//					classes.add(Class.forName(obj));
//				}
//			}
//			
//			reader.next();
//		}

		return classes;
	}
	
	public void createTables(ConnectionSource connectionSource) {
		logger.info("=========================================== CRIANDO TABELAS ===========================================");
//		try {
//			for(Class c : findClassXML()) {
//				TableUtils.createTable(connectionSource, c);
//				logger.info(c.getSimpleName());
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			Log.e(LOG_NAME, "Could not create new table for Thing", e);
//			
//			e.printStackTrace();
//		}
		logger.info("================================================= END =================================================");
	}
	
	public void dropTables(ConnectionSource connectionSource) {
		logger.info("========================================== EXCLUINDO TABELAS ==========================================");
//		try {
//			for(Class c : findClassXML()) {
//				TableUtils.dropTable(connectionSource, c, true);
//				logger.info(c.getSimpleName());
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			Log.e(LOG_NAME, "Could not upgrade the table for Thing", e);
//			
//			e.printStackTrace();
//		}
		logger.info("================================================= END =================================================");
	}
}
