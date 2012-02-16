package net.blanu.sneakermesh.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.blanu.sneakermesh.Logger;
import net.blanu.sneakermesh.Util;

public abstract class Command implements Comparable<Command>
{
	protected static final int CMD_PRESENCE=0;
	protected static final int CMD_HAVE=1;
	protected static final int CMD_WANT=2;
	protected static final int CMD_GIVE=3;
	
	protected static Logger logger=null;
	
	public String peer=null;
	
	public Command(String ip)
	{
		peer=ip;
	}
	
	public int compareTo(Command obj)
	{
		return 0;
	}
	
	static public void log(String s)
	{
		if(logger!=null)
		{
			logger.log(s);
		}
		else
		{
			System.out.println(s);
		}
	}
	
	static public void setLogger(Logger l)
	{
		logger=l;
	}
	
	public static Command readCommand(String ip, byte[] b) throws IOException
	{
		ByteArrayInputStream bin=new ByteArrayInputStream(b);
		DataInputStream din=new DataInputStream(bin);
		return readCommand(ip, din);
	}
	
	public static Command readCommand(String ip, DataInputStream is) throws IOException
	{
		log("reading peerCmd");
		int peerCmd=is.read();
		log("peerCmd: "+peerCmd);

		switch(peerCmd)
		{
		case -1:
			return null;
		case CMD_PRESENCE:
			return PresenceCommand.read(ip, is);
		case CMD_HAVE:
			return HaveCommand.read(ip, is);
		case CMD_WANT:
			return WantCommand.read(ip, is);
		case CMD_GIVE:
			return GiveCommand.read(ip, is);
		default:
			log("Unknown command: "+peerCmd);
			return null;
		}
	}
	
	static protected String readDigest(InputStream is)
	{
		byte[] digest=fillBuffer(is, 20); // SHA-1 is 20 bytes
		return Util.asHex(digest);
	}    
	
	static protected void writeDigest(String s, OutputStream out) throws IOException
	{
		out.write(Util.asBytes(s));
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
	
	public byte[] getBytes() throws IOException
	{
		ByteArrayOutputStream sout=new ByteArrayOutputStream();
		DataOutputStream dout=new DataOutputStream(sout);
		write(dout);
		dout.close();
		return sout.toByteArray();
	}
	
	abstract public void write(DataOutputStream out) throws IOException;
}
