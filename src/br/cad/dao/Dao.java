package br.cad.dao;

import java.util.List;

import br.cad.model.Model;

public interface Dao<MODEL extends Model> {
	
	/**
	 * Criar Novo Mode para tipo proprio
	 * 
	 * @return Model
	 */
	public MODEL getNewModel();

	/**
	 * Salva o Model. Inserir e alterar
	 * 
	 * @param model
	 */
	public Integer save(MODEL model);

	/**
	 * Remove o model
	 * 
	 * @param model
	 */
	public void remove(MODEL model);

	/**
	 * Procura um model por id
	 * 
	 * @param nameColumn
	 * @param value
	 * @return Model encontrado
	 */
	public MODEL find(String nameColumn, String value);
	
	/**
	 * Procura um model por id
	 * 
	 * @param id
	 * @return Model encontrado
	 */
	public MODEL find(Long id);

	/**
	 * Busca todos os models
	 * 
	 * @return Model
	 */
	public List<MODEL> findAll();
	
	/**
	 * Procura todos os models
	 * 
	 * @param nameColumn
	 * @param value
	 * @return List<Model> encontrados
	 */
	public List<MODEL> findAll(String nameColumn, String value);
	
}
