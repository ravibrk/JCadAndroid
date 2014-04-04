package br.cad.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

/**
 * Classe base para modelos de negocio Implementa Mapeamento para Id utilizando
 * a estrategia Identity Implementa os metodos toString, clone e equals.
 * 
 * @author WilliamRodrigues
 * 
 */

public class ModelEntity implements Model, Serializable {
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		final ModelEntity other = (ModelEntity) obj;
		if (getId() == null || other.getId() == null)
			return super.equals(obj);

		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (id != null)
			return id.toString() + ":" + getClass().getName();
		return super.toString();
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
