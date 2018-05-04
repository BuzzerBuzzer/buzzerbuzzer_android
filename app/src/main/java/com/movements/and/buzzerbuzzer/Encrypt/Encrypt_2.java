package com.movements.and.buzzerbuzzer.Encrypt;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encrypt_2 {

	private String iv;
	private Key keySpec;
	private String genPW;

	public Encrypt_2(){
	}

	public String running(String password, String key2) throws UnsupportedEncodingException, NoSuchAlgorithmException, GeneralSecurityException{
		this.iv = key2;

		byte[] keyBytes = new byte[16];
		byte[] b = key2.getBytes("UTF-8");
		int len = b.length;

		if(len > keyBytes.length){
			len = keyBytes.length;
		}

		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		this.keySpec = keySpec;

		this.genPW = this.AES256Util(password);
		return this.getGenPW();
	}

	private String AES256Util(String password) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
		byte[] encrypted = c.doFinal(password.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));
		return enStr;
	}

	private String ASE256Decode(String str) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

		byte[] byteStr = Base64.decodeBase64(str.getBytes());

		return new String(c.doFinal(byteStr),"UTF-8");
	}

	public String getGenPW() {
		return genPW;
	}
}
