package com.movements.and.buzzerbuzzer.Encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Encrypt_1 {
	private String AddKey = "";
	private String genPW;

	public Encrypt_1(){
		this.genPW = "error";
	}

	public String running(String password, String key1) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		this.AddKey = key1;
		this.genPW = this.SHA256Util(password);
		return this.genPW;
	}

	private String SHA256Util(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String buzzer_password = password+AddKey;
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(buzzer_password.getBytes("UTF-8"));
		byte byteData[] = md.digest();

		StringBuffer hexString = new StringBuffer();
		for(int i=0; i<byteData.length;i++){
			String hex = Integer.toHexString(0xff & byteData[i]);
			if(hex.length() == 1){
				hexString.append('0');
			}
			hexString.append(hex);
		}

		return hexString.toString();
	}
}
