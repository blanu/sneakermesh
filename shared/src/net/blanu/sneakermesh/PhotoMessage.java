package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PhotoMessage extends Message
{
	public String text;
	
	public PhotoMessage(long ts, String s)
	{
		super(MSG_PHOTO, ts, s.length());
		text=s;
	}
	
	public PhotoMessage(String s)
	{
		this(new Date().getTime(), s);
	}	
	
	public PhotoMessage(long ts, int num, InputStream is)
	{
		this(ts, new String(Util.fillBuffer(is, num)));
	}	
	
	public void writeData(OutputStream out) throws IOException
	{
		out.write(text.getBytes());
	}
	
	public String toString()
	{
		return "[Photo: "+text+"]";
	}
}
