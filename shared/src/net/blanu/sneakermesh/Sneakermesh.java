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
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

abstract public class Sneakermesh
{
	private static final String TAG = "Sneakermesh";
	
	private static final int CMD_HAVE=0;
	private static final int CMD_WANT=1;
	private static final int CMD_GIVE=2;
	private static final int CMD_DONE=3;

	private Set<String> have;
	private Set<String> want;
	private Set<String> give;
	private Set<String> wanted;
	private Set<String> available;
	protected File root;	
	
	public Sneakermesh(File f)
	{
		root=f;
		log("root: "+f);
        have=new HashSet<String>();
        want=new HashSet<String>();
        give=new HashSet<String>();
        wanted=new HashSet<String>();
        available=new HashSet<String>();
        loadHashes();
	}
	
	abstract void log(String s);
	
	public void sync(Socket sock, boolean pushHave)
	{
		log("sync");
		try {
			DataInputStream is=new DataInputStream(sock.getInputStream());
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			
			new ReadSync(is).start();
			new WriteSync(out).start();

			log("done send");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class ReadSync extends Thread
	{
		DataInputStream is;
		
		public ReadSync(DataInputStream i)
		{
			is=i;
		}
		
		public void run()
		{
			try
			{
				boolean done=false;
				while(!done)
				{
					log("reading command");
					done=readCommand(is);	    		    	
				}			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class WriteSync extends Thread
	{
		DataOutputStream out;
		
		public WriteSync(DataOutputStream os)
		{
			out=os;
		}
		
		public void run()
		{
			try
			{
				sendHave(out);    
			
				if(want.size()>0)
				{
					log("Sending want");
					sendWant(out);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}		

	public void loadHashes()
	{
		if(root==null)
		{
			return;
		}
		String[] files=root.list();
		if(files==null)
		{
			return;
		}
		System.out.println("files: "+files);
		
		for(int x=0; x<files.length; x++)
		{
			File f=new File(root, files[x]);
			if(f.length()>0)
			{
				log("I have: "+files[x]);
				have.add(files[x]);
			}
			else
			{
				log("I want: "+files[x]);
				want.add(files[x]);
			}
		}
	}

	public boolean readCommand(DataInputStream is) throws IOException
	{
		int peerCmd=is.read();
		log("readCommand: "+peerCmd);

		switch(peerCmd)
		{
		case -1:
			return true;
		case CMD_HAVE:
			readHave(is);
			break;
		case CMD_WANT:
			readWant(is);
			break;
		case CMD_GIVE:
			readGive(is);
			break;
		case CMD_DONE:
			return true;
		default:
			log("Unknown command: "+peerCmd);
		}
		
		return false;
	}

	public void readHave(DataInputStream is) throws IOException
	{    		
		log("readHave");
		Set<String>peerMsgs=new HashSet<String>();
		int num=is.read();
		log("numhave: "+num);
		for(int x=0; x<num; x++)
		{
			String digest=readDigest(is);
			log("Peer has "+digest);
			peerMsgs.add(digest);
		}

		synchronized(want)
		{
			for(String digest : peerMsgs)
			{
				File f=new File(root, digest);
				if(!f.exists())
				{	
					FileOutputStream out=new FileOutputStream(new File(root, digest));
					out.close();
					want.add(digest);
				}
			}
		}
	}

	public void readWant(DataInputStream is) throws IOException
	{
		log("readWant");
		int num=is.read();
		for(int x=0; x<num; x++)
		{
			String digest=readDigest(is);
			
			File file=new File(root, digest);
			if(!file.exists() && file.length()>0)
			{
				FileInputStream fis=new FileInputStream(file);
				long size=file.length();
//				sendGive(out, digest, size, fis);
				fis.close();
			}
		}    	
	}

	public void readGive(DataInputStream is) throws IOException
	{
		log("readGive");
		String digest=readDigest(is);
		long num=is.read();
		File file=new File(root, digest);
		FileOutputStream out=new FileOutputStream(file);
		pump(is, out, num);
		out.close();
	}

	public void sendHave(DataOutputStream out) throws IOException
	{
		log("sendHave");
		out.write(CMD_HAVE);
		out.write(have.size());
		
		synchronized(have)
		{
			for(String msg : have)
			{
				out.write(msg.getBytes());				
			}	    	
		}
	}

	public void sendWant(DataOutputStream out) throws IOException
	{
		log("sendWant");
		out.write(CMD_HAVE);
		out.write(want.size());
		
		synchronized(want)
		{
			for(String msg : have)
			{
				out.write(msg.getBytes());				
			}	    	
		}
	}	
	
	public void sendGive(DataOutputStream out, String digest, long size, FileInputStream file) throws IOException
	{
		log("sendGive");
		out.write(digest.getBytes());
		out.writeLong(size);
		pump(file, out);
	}
	
	public void addMessage(String msg) throws IOException
	{
		String digest=hash(msg);
		File file=new File(root, digest);
		FileOutputStream out=new FileOutputStream(file);
		out.write(msg.getBytes());
		out.close();		
	}

	static public String readDigest(InputStream is)
	{
		byte[] digest=fillBuffer(is, (512/8)*2);
		return new String(digest);
	}    

	static public byte[] fillBuffer(InputStream is, int size)
	{
		byte[] digest=new byte[size];
		int offset=0;
		int count=0;
		while(count<digest.length)
		{
			int read;
			try {
				read = is.read(digest, offset, digest.length-offset);
				offset=offset+read;
				count=count+read;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return digest;
	}

	static public void pump(InputStream is, OutputStream out)
	{
		int buffsize=1024;
		byte[] buff=new byte[buffsize];
		int count=0;
		
		try {
			int read=is.read(buff, 0, buffsize);
			while(read!=-1)
			{	
				out.write(buff, 0, read);
				count=count+read;
				read=is.read(buff, 0, buffsize);
			}
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}	

	static public void pump(InputStream is, OutputStream out, long maxlen)
	{
		int buffsize=1024;
		byte[] buff=new byte[buffsize];
		int count=0;
		
		try {
			int toread;
			if(count+buffsize>maxlen)
			{
				toread=(int)(buffsize-((count+buffsize)-maxlen));
			}
			else
			{
				toread=buffsize;
			}
			int read=is.read(buff, 0, toread);
			while(read!=-1)
			{	
				out.write(buff, 0, read);
				count=count+read;
				if(count+buffsize>maxlen)
				{
					toread=(int)(buffsize-((count+buffsize)-maxlen));
				}
				else
				{
					toread=buffsize;
				}
				read=is.read(buff, 0, toread);
			}
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}		
	
	public static String asHex(byte buf[])
	{
		StringBuffer strbuf = new StringBuffer(buf.length * 2);

		for(int i=0; i< buf.length; i++)
		{
			if(((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	public String hash(String s)
	{
		byte[] digest=Skein.hash(s.getBytes());
		return asHex(digest);
	}
}
