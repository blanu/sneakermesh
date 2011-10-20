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
	public String path;
	
	public PhotoMessage(long ts, int num, InputStream is) throws IOException
	{
		super(MSG_PHOTO, ts, num, is);
	}	
		
	public PhotoMessage(long ts, int num, File f) throws IOException
	{
		super(MSG_PHOTO, ts, num, f);
	}	
	
	public String toString()
	{
		return "[Photo: "+path+"]";
	}
}
