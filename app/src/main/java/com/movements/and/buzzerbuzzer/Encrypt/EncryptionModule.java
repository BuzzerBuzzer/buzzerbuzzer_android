package com.movements.and.buzzerbuzzer.Encrypt;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class EncryptionModule {

	private static EncryptionModule instance = new EncryptionModule();
	public static EncryptionModule getInstatnce(){
		return instance;
	}

	private String encryptedPassword;

	public EncryptionModule() {
		// TODO Auto-generated constructor stub
		this.encryptedPassword = "error";
	}

	public String running(String password, String key1, String key2){
		this.callEncrypt(password, key1, key2);
		return this.encryptedPassword;
	}

	private void callEncrypt(String password, String key1, String key2){
		Encrypt_1 obj1 = new Encrypt_1();
		Encrypt_2 obj2 = new Encrypt_2();

		try {
			this.encryptedPassword =obj2.running(obj1.running(password, key1), key2);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
