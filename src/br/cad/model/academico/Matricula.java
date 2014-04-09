package br.cad.model.academico;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.cad.model.ModelEntity;
import br.cad.model.pessoa.Aluno;

@DatabaseTable
public class Matricula extends ModelEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * *****************************************************************************************************************
	 * ************************************************** Atributos ****************************************************
	 * *****************************************************************************************************************
	 */
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id", columnName = "aluno")
	private Aluno aluno;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id", columnName = "curso")
	private Curso curso;

	/*
	 * ******************************************************************************************************************
	 * **************************************************** Gets e Sets *************************************************
	 * ******************************************************************************************************************
	 */

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/*
	 * *******************************************************************************************************************
	 * ***************************************************** Metodos *****************************************************
	 * *******************************************************************************************************************
	 */

}
