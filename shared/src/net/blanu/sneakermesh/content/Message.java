package net.blanu.sneakermesh.content;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import net.blanu.sneakermesh.Logger;
import net.blanu.sneakermesh.Util;

public abstract class Message implements Comparable<Message>
{
	public static final int MSG_TEXT=0;
	public static final int MSG_PHOTO=1;
	
	protected static Random random=new Random();
	protected static File tmp;
	
	public static Logger logger=null;
	
	public int type;
	public long timestamp;
	public int size;	
	public File file;
	public String digest;
	
	static public void setLogger(Logger l)
	{
		logger=l;
	}
	
	static public void log(String s)
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
		
	public int compareTo(Message m)
	{
		return new Long(timestamp).compareTo(new Long(m.timestamp));
	}
	
	public static void setTmp(File f)
	{
		tmp=f;
	}
	
	public static Message readMessage(DataInputStream is) throws IOException
	{
		int msgType=is.read();
		log("msgType: "+msgType);

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
		case MSG_PHOTO:
			return new PhotoMessage(ts, num, is);
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
		
		if(f.getAbsolutePath().contains("Pictures"))
		{
			return new PhotoMessage(ts, num, f);			
		}
		else
		{
			return new TextMessage(ts, num, f);
		}
	}	
	
	public Message(int t, long ts, int num, InputStream is) throws IOException
	{
		type=t;
		timestamp=ts;
		size=num;
		
		file=createTempFile();
		FileOutputStream out=new FileOutputStream(file);
		digest=Util.pump(is, out, num);	
		out.close();
		file.setLastModified(timestamp);		
	}	
	
	static protected File createTempFile() throws IOException
	{
		if(tmp==null)
		{
			return File.createTempFile("sneakermesh", "tmp");		
		}
		else
		{
			String filename=String.valueOf(new Date().getTime())+"."+String.valueOf(random.nextInt());
			return new File(tmp, filename);
		}
	}
	
	public Message(int t, long ts, int num, File f) throws IOException
	{
		type=t;
		timestamp=ts;
		size=num;
		file=f;		
		digest=Util.hash(file);		
	}	
	
	public void save(File destDir)
	{
		File dest=new File(destDir, digest);
		file.renameTo(dest);
		dest.setLastModified(timestamp);
		file=dest;
	}
	
	public void write(DataOutputStream out) throws IOException
	{
		out.write(type);
		out.writeLong(timestamp);
		out.writeInt(size);
		writeData(out);
	}
	
	public void writeData(OutputStream out) throws FileNotFoundException
	{
		InputStream in=new FileInputStream(file);
		Util.pump(in, out, size);
	}
			
	public String toString()
	{
		return "[Message: "+timestamp+"]";
	}	
}
