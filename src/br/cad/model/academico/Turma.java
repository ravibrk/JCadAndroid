package br.cad.model.academico;

import java.io.Serializable;

import br.cad.model.ModelEntity;
import br.cad.model.pessoa.Docente;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Turma extends ModelEntity implements Serializable {

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
	private String codigo;
	@DatabaseField
	private String Local;
	@DatabaseField
	private Integer horasAula;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id", columnName = "disciplina")
	private Disciplina disciplina;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id", columnName = "docente")
	private Docente docente;
	@ForeignCollectionField(eager = false)
	private ForeignCollection<TurmaAluno> alunos;

	/*
	 * ******************************************************************************************************************
	 * **************************************************** Gets e Sets *************************************************
	 * ******************************************************************************************************************
	 */

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getLocal() {
		return Local;
	}

	public void setLocal(String local) {
		Local = local;
	}

	public Integer getHorasAula() {
		return horasAula;
	}

	public void setHorasAula(Integer horasAula) {
		this.horasAula = horasAula;
	}

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

	public ForeignCollection<TurmaAluno> getAlunos() {
		return alunos;
	}

	public void setAlunos(ForeignCollection<TurmaAluno> alunos) {
		this.alunos = alunos;
	}

	/*
	 * *******************************************************************************************************************
	 * ***************************************************** Metodos *****************************************************
	 * *******************************************************************************************************************
	 */

}
