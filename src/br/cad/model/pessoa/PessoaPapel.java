package br.cad.model.pessoa;

import java.io.Serializable;

import br.cad.model.ModelEntity;
import br.cad.model.system.User;

import com.j256.ormlite.field.DatabaseField;

/**
 * Classe responsavel pelo papel de cada pessoa tipo Aluno, professor e etc...
 * 
 * @author WilliamRodrigues <br>
 *         william.rodrigues@live.fae.edu
 * 
 */

public abstract class PessoaPapel extends ModelEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * *****************************************************************************************************************
	 * ************************************************** Atributos ****************************************************
	 * *****************************************************************************************************************
	 */
	@DatabaseField
	private String nomeCompleto;
	
	/**
	 * Usuario do Sistema
	 */
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id", columnName = "usuario")
	private User usuario;

	/*
	 * ******************************************************************************************************************
	 * **************************************************** Gets e Sets *************************************************
	 * ******************************************************************************************************************
	 */

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	/*
	 * *******************************************************************************************************************
	 * ***************************************************** Metodos *****************************************************
	 * *******************************************************************************************************************
	 */
}
