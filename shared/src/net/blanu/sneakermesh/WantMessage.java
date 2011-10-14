package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WantMessage extends Message {
	protected Set<String> want;
	
	static public WantMessage read(DataInputStream is) throws IOException
	{
		Set<String>peerWants=new HashSet<String>();
		int num=is.read();
		for(int x=0; x<num; x++)
		{
			String digest=readDigest(is);
			peerWants.add(digest);
		}
		
		return new WantMessage(peerWants);
	}
	
	public WantMessage(Set<String> set)
	{
		want=set;
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_WANT);
		out.write(want.size());
			
		for(String msg : want)
		{
			out.write(msg.getBytes());				
		}	    	
	}
}
