package br.cad.util;

import java.security.MessageDigest;

public class MD5 
{
	public static String encrypt(String mess)throws Exception {
		StringBuffer buffer = new StringBuffer();
		int i, n;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] cifrado = digest.digest(mess.getBytes());
			for (i = 0; i < cifrado.length; i++) {
				n = cifrado[i] & 255;
				if (n < 16) {
					buffer.append("0");
				}
				buffer.append(Integer.toHexString(n).toUpperCase());
			}
		}catch(Exception e){
		}
		return buffer.toString();		
	}
	
	public static void main(String args[]) throws Exception {
		
		System.out.println(encrypt("123456"));
	}
}