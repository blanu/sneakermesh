package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class GiveMessage extends Message {
	protected String digest;
	protected long size;
	protected InputStream stream;
	
	static protected File root=null;
	
	static public GiveMessage read(DataInputStream is) throws IOException
	{
		String s=readDigest(is);
		long l=is.readLong();
				
		return new GiveMessage(s, l, is);
	}
	
	public GiveMessage(String s, long l, InputStream is)
	{
		digest=s;
		size=l;
		stream=is;
	}
	
	public GiveMessage(String s) throws FileNotFoundException
	{
		digest=s;
		File f=new File(root, digest);
		size=f.length();
		stream=new FileInputStream(f);
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_GIVE);		
		out.write(digest.getBytes());
		out.writeLong(size);
		pump(stream, out);
	}
	
	static public void pump(InputStream is, OutputStream out)
	{
		int buffsize=1024;
		byte[] buff=new byte[buffsize];
		int count=0;
		
		try {
			int read=is.read(buff, 0, buffsize);
			while(read!=-1)
			{	
				out.write(buff, 0, read);
				count=count+read;
				read=is.read(buff, 0, buffsize);
			}
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}	
	
	public String toString()
	{
		return "[Give: "+digest+" : "+size+"]";
	}	
}
