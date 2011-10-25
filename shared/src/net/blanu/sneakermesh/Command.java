package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Command implements Comparable<Command>
{
	protected static final int CMD_HAVE=0;
	protected static final int CMD_WANT=1;
	protected static final int CMD_GIVE=2;
	protected static final int CMD_DONE=3;	
	
	protected static Logger logger=null;
	
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
	
	public static Command readCommand(DataInputStream is) throws IOException
	{
		log("reading peerCmd");
		int peerCmd=is.read();
		log("peerCmd: "+peerCmd);

		switch(peerCmd)
		{
		case -1:
			return null;
		case CMD_HAVE:
			return HaveCommand.read(is);
		case CMD_WANT:
			return WantCommand.read(is);
		case CMD_GIVE:
			return GiveCommand.read(is);
		case CMD_DONE:
			return DoneCommand.read(is);
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
	
	abstract public void write(DataOutputStream out) throws IOException;
}
