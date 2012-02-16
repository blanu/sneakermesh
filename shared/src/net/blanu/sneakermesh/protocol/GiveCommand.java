package net.blanu.sneakermesh.protocol;

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

import net.blanu.sneakermesh.content.Message;

public class GiveCommand extends Command {
	public String digest;
	public Message msg;
	
	static public File root=null;
	
	static public GiveCommand read(String ip, DataInputStream is) throws IOException
	{
		String s=readDigest(is);
				
		return new GiveCommand(ip, s, is);
	}
	
	public GiveCommand(String ip, String s, DataInputStream is) throws IOException
	{
		super(ip);
		digest=s;
		msg=Message.readMessage(is);
	}
	
	public GiveCommand(String ip, String s) throws IOException
	{
		super(ip);
		digest=s;
		File f=new File(new File(root, "texts"), digest);
		msg=Message.readMessage(f);
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_GIVE);		
		writeDigest(digest, out);				
		msg.write(out);
	}
	
	public String toString()
	{
		return "[Give: "+digest+" : "+msg+"]";
	}	
}
