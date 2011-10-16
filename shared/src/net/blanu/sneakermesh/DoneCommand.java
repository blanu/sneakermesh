package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DoneCommand extends Command {
	static public DoneCommand read(DataInputStream is) throws IOException
	{
		return new DoneCommand();
	}
	
	public DoneCommand()
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
