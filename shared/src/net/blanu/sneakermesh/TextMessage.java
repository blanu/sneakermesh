package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TextMessage extends Message
{
	public long timestamp;
	public int size;
	public String text;
	
	static public TextMessage read(DataInputStream is) throws IOException
	{
		long ts=is.readLong();
		System.out.println("timestamp: "+ts);
		int num=is.readInt();
		System.out.println("num: "+num);
		byte[] buff=Util.fillBuffer(is, num);
				
		return new TextMessage(ts, new String(buff));
	}
		
	public TextMessage(long ts, String s)
	{
		timestamp=ts;
		text=s;
		size=text.length();
	}

	public TextMessage(String s)
	{
		this(new Date().getTime(), s);
	}	
	
	public void write(DataOutputStream out) throws IOException
	{
		System.out.println("writing ts: "+timestamp);
		System.out.println("writing size: "+size);
		
		out.write(MSG_TEXT);
		out.writeLong(timestamp);
		out.writeInt(size);
		out.write(text.getBytes());
	}
	
	public String toString()
	{
		return "[Text: "+text+"]";
	}
	
	public int compareTo(Message m)
	{
		if(m instanceof TextMessage)
		{
			return new Long(timestamp).compareTo(new Long(((TextMessage)m).timestamp));
		}
		else
		{
			return 0;
		}
	}
}
