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

public class GiveCommand extends Command {
	protected String digest;
	protected Message msg;
	
	static protected File root=null;
	
	static public GiveCommand read(DataInputStream is) throws IOException
	{
		String s=readDigest(is);
				
		return new GiveCommand(s, is);
	}
	
	public GiveCommand(String s, DataInputStream is) throws IOException
	{
		digest=s;
		msg=Message.readMessage(is);
	}
	
	public GiveCommand(String s) throws IOException
	{
		digest=s;
		File f=new File(root, digest);
		msg=Message.readMessage(f);
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_GIVE);		
		out.write(digest.getBytes());
		msg.write(out);
	}
	
	public String toString()
	{
		return "[Give: "+digest+" : "+msg+"]";
	}	
}
