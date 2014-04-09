package br.cad.model.system;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import br.cad.model.ModelEntity;
import br.cad.util.MD5;

/**
 * 
 * @author WilliamRodrigues
 */

@DatabaseTable
public class User extends ModelEntity implements Serializable {
	/**
	 * SerialVersion
	 */
	private static final long serialVersionUID = 1L;

	/* 
	 ******************************************************************************************************************
	 *************************************************** Atributos ****************************************************
	 ******************************************************************************************************************
	 */
	
	@DatabaseField
	private String name;
	@DatabaseField
	private String email;
	@DatabaseField
	private String keyUser;
	@DatabaseField
	private String password;
	@DatabaseField
	private Boolean active;

	/*
	 *******************************************************************************************************************
	 ***************************************************** Gets e Sets *************************************************
	 *******************************************************************************************************************
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getKeyUser() {
		return keyUser;
	}

	public void setKeyUser(String keyUser) {
		this.keyUser = keyUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordKeyUser(String password) {
		if (password != null && password.trim().length() > 0)
			try {
				setPassword(MD5.encrypt(password));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public String getPasswordKeyUser() {
		return null;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	/*
	 ********************************************************************************************************************
	 ****************************************************** Metodos *****************************************************
	 ********************************************************************************************************************
	 */

	/**
	 * Verifica se a chave key e valida de acordo com o atributo codigo
	 * 
	 * @return true se for valida e false caso contrario.
	 */
	public boolean checkValidKey(String key) {
		try {
			return key.equals(getUserKey());
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Gera uma chave unica para o usuario. Utilizada para entrada automatica no sistema.
	 * 
	 * @return
	 */
	public String getUserKey() {
		try {
			char[] md5 = MD5.encrypt(keyUser.toLowerCase()).toLowerCase().toCharArray();
			changePosition(md5, 5, 4);
			changePosition(md5, 10, 2);
			changePosition(md5, 3, 12);
			changePosition(md5, 15, 7);
			changePosition(md5, 1, 8);
			changePosition(md5, 20, 8);
			changePosition(md5, 21, 3);
			changePosition(md5, 31, 6);
			changePosition(md5, 30, 28);
			changePosition(md5, 28, 18);
			changePosition(md5, 25, 13);
			return new String(md5);
		} catch (Exception e) {
		}
		return null;
	}

	private void changePosition(char[] ch, int p1, int p2) {
		char aux = ch[p1];
		ch[p1] = ch[p2];
		ch[p2] = aux;
	}
}
