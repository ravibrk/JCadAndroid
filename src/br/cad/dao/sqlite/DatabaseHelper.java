package br.cad.dao.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.cad.model.academico.Curso;
import br.cad.model.academico.Disciplina;
import br.cad.model.academico.Matricula;
import br.cad.model.academico.Turma;
import br.cad.model.academico.TurmaAluno;
import br.cad.model.pessoa.Administrador;
import br.cad.model.pessoa.Aluno;
import br.cad.model.pessoa.Docente;
import br.cad.model.system.Resource;
import br.cad.model.system.User;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Helper class which creates/updates our database.
 * 
 * @author WilliamRodrigues
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	/*
	 * *****************************************************************************************************************
	 * ************************************************** Atributos ****************************************************
	 * *****************************************************************************************************************
	 */
	
	private static final String DATABASE_NAME = "controlacademic.db";
	private static final int DATABASE_VERSION = 1;
	private List<Class<?>> classTables = new ArrayList<Class<?>>();
	private final String LOG_NAME = getClass().getName();
	
	/*
	 * *****************************************************************************************************************
	 * ************************************************** Construtor ***************************************************
	 * *****************************************************************************************************************
	 */
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/*
	 * *****************************************************************************************************************
	 * ************************************************* Gets e Sets ***************************************************
	 * *****************************************************************************************************************
	 */

	public List<Class<?>> getClassTables() {
		// br.cad.model.system
		classTables.add(Resource.class);
		classTables.add(User.class);
		
		// br.cad.model.pessoa
		classTables.add(Administrador.class);
		classTables.add(Aluno.class);
		classTables.add(Docente.class);
		
		// br.cad.model.academico
		classTables.add(Disciplina.class);
		classTables.add(Curso.class);
		classTables.add(Matricula.class);
		classTables.add(Turma.class);
		classTables.add(TurmaAluno.class);
		
		return classTables;
	}
	
	/*
	 * *****************************************************************************************************************
	 * *************************************************** Metodos *****************************************************
	 * *****************************************************************************************************************
	 */
	
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		createTables(connectionSource);
		initInsert(sqLiteDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		dropTables(connectionSource);
		onCreate(sqLiteDatabase, connectionSource);
	}
	
	protected void createTables(ConnectionSource connectionSource) {
		try {
			for(Class<?> c : getClassTables()) {
				TableUtils.createTable(connectionSource, c);
			}
		}
		catch (SQLException e) {
			Log.e(LOG_NAME, "Could not create new table", e);
		}
	}
	
	protected void initInsert(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("INSERT INTO Resource (name, controllerActivity, objectClass, icon) VALUES ('Home', 'HomeFragment', 'Resource', '')");
		sqLiteDatabase.execSQL("INSERT INTO Resource (name, controllerActivity, objectClass, icon) VALUES ('Chamada', 'ChamadaFragment', 'Resource', '')");
	}
	
	protected void dropTables(ConnectionSource connectionSource) {
		try {
			for(Class<?> c : getClassTables()) {
				TableUtils.dropTable(connectionSource, c, true);
			}
		}
		catch (SQLException e) {
			Log.e(LOG_NAME, "Could not upgrade the table", e);
		}
	}
}
