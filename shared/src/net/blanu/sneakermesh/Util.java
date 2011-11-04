package net.blanu.sneakermesh;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Util
{
	static protected Logger logger=null;
	
	static public void setLogger(Logger l)
	{
		logger=l;
	}
	
	static void log(String s)
	{
		if(logger==null)
		{
			System.out.println(s);
		}
		else
		{
			logger.log(s);			
		}
	}
	
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
			try {
			int read;
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
	
	public static String hash(File f) throws IOException
	{
		try
		{
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");		

			FileInputStream     fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DigestInputStream   dis = new DigestInputStream(bis, sha1);

			while (dis.read() != -1);

			byte[] hash = sha1.digest();

			return asHex(hash);	
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}	

	public static String pump(InputStream in, OutputStream out, long maxlen)
	{
		int buffsize=1024;
		byte[] buff=new byte[buffsize];
		int count=0;

		MessageDigest sha1=null;
		try
		{			
			sha1 = MessageDigest.getInstance("SHA1");		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		BufferedInputStream bis = new BufferedInputStream(in);
		DigestInputStream is = new DigestInputStream(bis, sha1);		
		
		try {
			int toread;
			if(count+buffsize>maxlen)
			{
				toread=(int)(buffsize-((count+buffsize)-maxlen));
			}
			else
			{
				toread=buffsize;
			}
			
			int read=is.read(buff, 0, toread);
			while(read!=-1 && count<maxlen)
			{	
				out.write(buff, 0, read);
				count=count+read;
				if(count+buffsize>maxlen)
				{
					toread=(int)(buffsize-((count+buffsize)-maxlen));
				}
				else
				{
					toread=buffsize;
				}
				read=is.read(buff, 0, toread);
			}
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		log("pump read "+count+" of "+maxlen);

		if(sha1==null)
		{
			return null;
		}
		else
		{
			byte[] hash = sha1.digest();
			return asHex(hash);	
		}
	}
	
    public static String asHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }	
		
    public static byte[] asBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
	public static InputStream encrypt(String password, InputStream plaintext) throws Exception {
		return crypt(password, plaintext, Cipher.ENCRYPT_MODE);
	}
	
	public static InputStream decrypt(String password, InputStream ciphertext) throws Exception {
		return crypt(password, ciphertext, Cipher.DECRYPT_MODE);
	}
	
	public static InputStream crypt(String password, InputStream text, int mode) throws Exception
	{
        return new CipherInputStream(text, makeCipher(password, mode));
    }		

	public static OutputStream encrypt(String password, OutputStream plaintext) throws Exception {
		return crypt(password, plaintext, Cipher.ENCRYPT_MODE);
	}
	
	public static OutputStream decrypt(String password, OutputStream ciphertext) throws Exception {
		return crypt(password, ciphertext, Cipher.DECRYPT_MODE);
	}
	
	public static OutputStream crypt(String password, OutputStream text, int mode) throws Exception
	{
        return new CipherOutputStream(text, makeCipher(password, mode));
    }			
	
	public static Cipher makeCipher(String password, int mode) throws Exception
	{
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom secrand = SecureRandom.getInstance("SHA1PRNG");
        secrand.setSeed(password.getBytes());

        keygen.init(128, secrand);
        SecretKey seckey = keygen.generateKey();
        byte[] rawKey = seckey.getEncoded();

        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, skeySpec);
        return cipher;
	}	    	
	
	public static String join(String[] s, String delim)
	{
		if(s.length==0)
		{
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<s.length-1; i++)
		{
			buffer.append(s[i]);
			buffer.append(delim);
		}
		buffer.append(s[s.length-1]);

		return buffer.toString();
	}	
}
