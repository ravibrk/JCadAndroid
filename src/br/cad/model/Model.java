package br.cad.model;

import java.io.Serializable;

/**
 * 
 * @author WilliamRodrigues
 *
 */
public interface Model extends Serializable, Cloneable {
	public Long getId();
	public void setId(Long id);
	public Object clone() throws CloneNotSupportedException;
}
