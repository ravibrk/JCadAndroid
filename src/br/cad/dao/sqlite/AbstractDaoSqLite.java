package br.cad.dao.sqlite;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;
import br.cad.dao.Dao;
import br.cad.model.Model;

import com.j256.ormlite.dao.BaseDaoImpl;

public abstract class AbstractDaoSqLite<MODEL extends Model> extends BaseDaoImpl<MODEL, Long> implements Dao<MODEL> {
	
	/*
	 * *****************************************************************************************************************
	 * ************************************************** Atributos ****************************************************
	 * *****************************************************************************************************************
	 */

	private final String LOG_TAG = getClass().getSimpleName();
	protected String modelClassName;
	protected String edPackageModel;

	/*
	 * *****************************************************************************************************************
	 * ************************************************* Construtor ****************************************************
	 * *****************************************************************************************************************
	 */
	
	protected AbstractDaoSqLite(Class<MODEL> dataClass) throws SQLException {
		super(dataClass);
	}
		
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
	public MODEL getNewModel() {
		try {
			String className = getEdPackageModel() + getModelClassName();
			return (MODEL) Class.forName(className).newInstance();
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
	public Integer save(MODEL model) {
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
	public void remove(MODEL model) {
		try {
			Log.i(LOG_TAG, "Objeto " + modelClassName + " removido ID: " + delete(model));
		} catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao remover objeto.");
			
			e.printStackTrace();
		}
	}

	@Override
	public MODEL find(String nameColumn, String value) {
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
	public MODEL find(Long id) {
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
	public List<MODEL> findAll() {
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
	public List<MODEL> findAll(String nameColumn, String value) {
		try {
			return queryForEq(nameColumn, value);
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao buscar por propriedade.");
			
			e.printStackTrace();
		}
		
		return null;
	}
}
