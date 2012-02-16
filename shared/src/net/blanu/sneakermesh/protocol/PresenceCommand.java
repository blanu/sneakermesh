package net.blanu.sneakermesh.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PresenceCommand extends Command
{	
	static public PresenceCommand read(String ip, DataInputStream is) throws IOException
	{
		return new PresenceCommand(ip);
	}
	
	public PresenceCommand(String ip)
	{
		super(ip);
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_PRESENCE);
	}
	
	public String toString()
	{
		return "[Presence]";
	}	
}
