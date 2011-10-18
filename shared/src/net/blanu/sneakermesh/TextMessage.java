package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TextMessage extends Message
{
	public String text;
	
	public TextMessage(long ts, String s)
	{
		super(MSG_TEXT, ts, s.length());
		text=s;
	}
	
	public TextMessage(String s)
	{
		this(new Date().getTime(), s);
	}	
	
	public TextMessage(long ts, int num, InputStream is)
	{
		this(ts, new String(Util.fillBuffer(is, num)));
	}	
	
	public void writeData(OutputStream out) throws IOException
	{
		out.write(text.getBytes());
	}
	
	public String toString()
	{
		return "[Text: "+text+"]";
	}
}
