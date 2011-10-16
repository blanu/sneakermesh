package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HaveCommand extends Command {
	public Set<String> have;
	
	static public HaveCommand read(DataInputStream is) throws IOException
	{
		Set<String>peerHas=new HashSet<String>();
		int num=is.read();
		for(int x=0; x<num; x++)
		{
			String digest=readDigest(is);
			peerHas.add(digest);
		}
		
		return new HaveCommand(peerHas);
	}
	
	public HaveCommand(Set<String> set)
	{
		have=set;
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_HAVE);
		out.write(have.size());
			
		for(String msg : have)
		{
			out.write(msg.getBytes());				
		}	    	
	}
	
	public String toString()
	{
		return "[Have: "+have.size()+"]";
	}
}
