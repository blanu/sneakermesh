package net.blanu.sneakermesh.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class WantCommand extends Command {
	public Set<String> want;
	
	static public WantCommand read(String ip, DataInputStream is) throws IOException
	{
		Set<String>peerWants=new HashSet<String>();
		int num=is.read();
		for(int x=0; x<num; x++)
		{
			String digest=readDigest(is);
			peerWants.add(digest);
		}
		
		return new WantCommand(ip, peerWants);
	}
	
	public WantCommand(String ip, String digest)
	{
		super(ip);
		Set<String> set=new HashSet<String>();
		set.add(digest);
		want=set;
	}
	
	public WantCommand(String ip, Set<String> set)
	{
		super(ip);
		want=set;
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_WANT);
		out.write(want.size());
			
		for(String msg : want)
		{
			writeDigest(msg, out);				
		}	    	
	}
	
	public String toString()
	{
		return "[Want: "+want.size()+"]";
	}	
}
