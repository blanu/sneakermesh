package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Util
{
	static protected String readDigest(InputStream is)
	{
		byte[] digest=fillBuffer(is, (512/8)*2);
		return new String(digest);
	}    

	static protected byte[] fillBuffer(InputStream is, int size)
	{
		byte[] digest=new byte[size];
		int offset=0;
		int count=0;
		while(count<digest.length)
		{
			int read;
			try {
				read = is.read(digest, offset, digest.length-offset);
				offset=offset+read;
				count=count+read;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return digest;
	}	
	
	public static String asHex(byte buf[])
	{
		StringBuffer strbuf = new StringBuffer(buf.length * 2);

		for(int i=0; i< buf.length; i++)
		{
			if(((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}	
	
	public static byte[] encrypt(String password, String plaintext) throws Exception {
		return crypt(password, plaintext, Cipher.ENCRYPT_MODE);
	}
	
	public static byte[] decrypt(String password, String plaintext) throws Exception {
		return crypt(password, plaintext, Cipher.DECRYPT_MODE);
	}
	
	public static byte[] crypt(String password, String plaintext, int mode) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom secrand = SecureRandom.getInstance("SHA1PRNG");
        secrand.setSeed(password.getBytes());

        keygen.init(128, secrand);
        SecretKey seckey = keygen.generateKey();
        byte[] rawKey = seckey.getEncoded();

        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(mode, skeySpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());

        return encrypted;
    }		
}
