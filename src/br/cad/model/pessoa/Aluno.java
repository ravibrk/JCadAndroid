package br.cad.model.pessoa;

import java.io.Serializable;

import br.cad.model.academico.TurmaAluno;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Aluno extends PessoaPapel implements Serializable {

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
	private String ra;
	@ForeignCollectionField(eager = false)
	private ForeignCollection<TurmaAluno> turmas;

	/*
	 * ******************************************************************************************************************
	 * **************************************************** Gets e Sets *************************************************
	 * ******************************************************************************************************************
	 */

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public ForeignCollection<TurmaAluno> getTurmas() {
		return turmas;
	}

	public void setTurmas(ForeignCollection<TurmaAluno> turmas) {
		this.turmas = turmas;
	}
	
	/*
	 * *******************************************************************************************************************
	 * ***************************************************** Metodos *****************************************************
	 * *******************************************************************************************************************
	 */
}
