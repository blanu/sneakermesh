package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DoneMessage extends Message {
	static public DoneMessage read(DataInputStream is) throws IOException
	{
		return new DoneMessage();
	}
	
	public DoneMessage()
	{
	}

	public void write(DataOutputStream out) throws IOException
	{
		out.write(CMD_DONE);
	}
	
	public String toString()
	{
		return "[Done]";
	}
}
