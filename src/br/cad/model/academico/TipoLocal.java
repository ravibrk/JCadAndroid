package br.cad.model.academico;

import java.util.Arrays;
import java.util.List;

public enum TipoLocal {

	SALA("Sala"), 
	LABORATORIO("Laboratório"), 
	TEATRO("Teatro"), 
	QUADRA("Quadra");

	private TipoLocal(String nome) {
		this.nome = nome;
	}

	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public static TipoLocal getByTipo(String tipo) {
		for (TipoLocal t : values()) {
			if (t.nome.equals(tipo))
				return t;
		}
		return null;
	}
	
	public static List<TipoLocal> getList() {
		return Arrays.asList(TipoLocal.values());
	}
}
