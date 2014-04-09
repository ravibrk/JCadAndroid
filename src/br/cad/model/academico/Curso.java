package br.cad.model.academico;

import java.io.Serializable;

import br.cad.model.ModelEntity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Curso extends ModelEntity implements Serializable {

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
	private String nome;
	@DatabaseField
	private String duracao;
	@ForeignCollectionField(eager = false)
	private ForeignCollection<Disciplina> disciplinas;

	/*
	 * ******************************************************************************************************************
	 * **************************************************** Gets e Sets *************************************************
	 * ******************************************************************************************************************
	 */

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDuracao() {
		return duracao;
	}

	public void setDuracao(String duracao) {
		this.duracao = duracao;
	}

	public ForeignCollection<Disciplina> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(ForeignCollection<Disciplina> disciplinas) {
		this.disciplinas = disciplinas;
	}

	/*
	 * *******************************************************************************************************************
	 * ***************************************************** Metodos *****************************************************
	 * *******************************************************************************************************************
	 */
}
