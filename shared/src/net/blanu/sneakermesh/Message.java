package net.blanu.sneakermesh;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Message implements Comparable<Message>
{
	protected static final int MSG_TEXT=0;
	protected static final int MSG_PHOTO=1;
	
	public int type;
	public long timestamp;
	public int size;	
		
	public int compareTo(Message m)
	{
		return new Long(timestamp).compareTo(new Long(m.timestamp));
	}
	
	public static Message readMessage(DataInputStream is) throws IOException
	{
		int msgType=is.read();

		long ts=is.readLong();
		System.out.println("timestamp: "+ts);
		int num=is.readInt();
		System.out.println("num: "+num);		
		
		switch(msgType)
		{
		case -1:
			return null;
		case MSG_TEXT:
			return new TextMessage(ts, num, is);
		default:
			System.out.println("Unknown message: "+msgType);
			return null;
		}
	}
	
	static public Message readMessage(File f) throws IOException
	{		
		long ts=f.lastModified();
		System.out.println("timestamp: "+ts);
		int num=(int)f.length();
		InputStream is=new FileInputStream(f);
		
		if(f.getAbsolutePath().contains("Pictures"))
		{
			return new PhotoMessage(ts, num, is);			
		}
		else
		{
			return new TextMessage(ts, num, is);
		}
	}	
	
	public Message(int t, long ts, int num)
	{
		type=t;
		timestamp=ts;
		size=num;
	}
	
	public void write(DataOutputStream out) throws IOException
	{
		out.write(type);
		out.writeLong(timestamp);
		out.writeInt(size);
		writeData(out);
	}
	
	public void write(File f) throws IOException
	{
		OutputStream out=new FileOutputStream(f);
		writeData(out);
		out.close();
		f.setLastModified(timestamp);
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
	
	public String toString()
	{
		return "[Message: "+timestamp+"]";
	}	
	
	abstract public void writeData(OutputStream out) throws IOException;
}
