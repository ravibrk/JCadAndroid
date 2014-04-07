package br.cad.dao.sqlite;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;
import br.cad.dao.Dao;
import br.cad.model.Model;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public abstract class AbstractDaoSqLite<MODEL extends Model> implements Dao<MODEL> {
	
	/*
	 * *****************************************************************************************************************
	 * ************************************************** Atributos ****************************************************
	 * *****************************************************************************************************************
	 */

	private final String LOG_TAG = getClass().getSimpleName();
	protected String edPackageModel;
	private BaseDaoImpl<MODEL, Long> daoImpl;

	/*
	 * *****************************************************************************************************************
	 * ************************************************* Construtor ****************************************************
	 * *****************************************************************************************************************
	 */
	
	@SuppressWarnings("unchecked")
	protected AbstractDaoSqLite(ConnectionSource connectionSource, String edPackageModel) {
		super();
		this.edPackageModel = edPackageModel;
		try {
			this.daoImpl = (BaseDaoImpl<MODEL, Long>) DaoManager.createDao(connectionSource, getNewModel().getClass());
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
			String className = getEdPackageModel() + "." + getModelClassName();
			return (MODEL) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public BaseDaoImpl<MODEL, Long> getDaoImpl() {
		return daoImpl;
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
				return daoImpl.create(model);
			}
			else {
				return daoImpl.update(model);
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
			Log.i(LOG_TAG, "Objeto " + getModelClassName() + " removido ID: " + daoImpl.delete(model));
		} catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao remover objeto.");
			
			e.printStackTrace();
		}
	}

	@Override
	public MODEL find(String nameColumn, String value) {
		try {
			return daoImpl.queryForEq(nameColumn, value).get(0);
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
			return daoImpl.queryForId(id);
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
			return daoImpl.queryForAll();
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
			return daoImpl.queryForEq(nameColumn, value);
		} 
		catch (SQLException e) {
			Log.d(LOG_TAG, "Erro ao buscar por propriedade.");
			
			e.printStackTrace();
		}
		
		return null;
	}
}
