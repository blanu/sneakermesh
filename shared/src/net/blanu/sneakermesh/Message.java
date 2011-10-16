package net.blanu.sneakermesh;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Message implements Comparable<Message>
{
	protected static final int MSG_TEXT=0;
		
	public int compareTo(Message obj)
	{
		return 0;
	}
	
	public static Message readMessage(DataInputStream is) throws IOException
	{
		int msgType=is.read();

		switch(msgType)
		{
		case -1:
			return null;
		case MSG_TEXT:
			return TextMessage.read(is);
		default:
			System.out.println("Unknown message: "+msgType);
			return null;
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
			int read;
			try {
				read = is.read(digest, offset, digest.length-offset);
				offset=offset+read;
				count=count+read;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return digest;
	}	
	
	public String getDigest()
	{
		try {
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			DataOutputStream dout=new DataOutputStream(out);
			write(dout);
			dout.close();
			
			byte[] digest=Skein.hash(out.toByteArray());
			return Util.asHex(digest);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	abstract public void write(DataOutputStream out) throws IOException;
}
