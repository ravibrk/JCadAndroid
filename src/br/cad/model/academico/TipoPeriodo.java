package br.cad.model.academico;

import java.util.Arrays;
import java.util.List;

public enum TipoPeriodo {

	PERIODO(1, "Período"), 
	MODULO(2, "Módulo"), 
	MES(3, "Mês"), 
	BIMESTRE(4, "Bimestre"), 
	TRIMESTRE(5, "Trimestre"), 
	SEMESTRE(6, "Semestre"), 
	ANO(7, "Ano");

	private TipoPeriodo(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	private Integer id;
	private String nome;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public static TipoPeriodo getByTipo(String tipo) {
		for (TipoPeriodo t : values()) {
			if (t.nome.equals(tipo))
				return t;
		}
		return null;
	}
	
	public static List<TipoPeriodo> getList() {
		return Arrays.asList(TipoPeriodo.values());
	}
}
