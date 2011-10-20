package net.blanu.sneakermesh;

import java.io.ByteArrayInputStream;
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
	
	public TextMessage(long ts, int num, InputStream is) throws IOException
	{
		super(MSG_TEXT, ts, num, is);
	}	
	
	public TextMessage(long ts, int num, File f) throws IOException
	{
		super(MSG_TEXT, ts, num, f);
	}	
	
	public TextMessage(long ts, String s) throws IOException
	{
		super(MSG_TEXT, ts, s.length(), new ByteArrayInputStream(s.getBytes()));
	}
	
	public TextMessage(String s) throws IOException
	{
		this(new Date().getTime(), s);
	}		
	
	public String getText()
	{
		try
		{
			FileInputStream in=new FileInputStream(file);
			byte[] buff=Util.fillBuffer(in, size);
			return new String(buff);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
			
	public String toString()
	{
		return "[Text: "+file.length()+"]";
	}
}
