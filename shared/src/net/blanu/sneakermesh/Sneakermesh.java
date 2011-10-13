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
import java.util.Queue;
import java.util.Set;

abstract public class Sneakermesh
{
	private static final String TAG = "Sneakermesh";

	private Set<String> have;
	private Set<String> want;
	private Set<String> give;
	
	private Queue<Message> queue;
	
	protected File root;	
	
	public Sneakermesh(File f)
	{
		root=f;
		log("root: "+f);
		GiveMessage.root=f;
		
        have=new HashSet<String>();
        want=new HashSet<String>();
        give=new HashSet<String>();
        
        loadHashes();
	}
	
	abstract void log(String s);
	
	public void sync(Socket sock, boolean pushHave)
	{
		log("sync");
		try {
			DataInputStream is=new DataInputStream(sock.getInputStream());
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			
			queue.add(new HaveMessage(have));
			
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
				log("reading command");
				Message msg=Message.readCommand(is);
				while(msg!=null)
				{
					log("command: "+msg);
					log("reading command");
					msg=Message.readCommand(is);
					execute(msg);
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
			while(true)
			{
				try
				{
					log("writesync");

					Message msg=queue.poll();
					if(msg!=null)
					{
						msg.write(out);
					}
					
					sleep(10000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return;
				}
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
	
	private void execute(Message msg) throws IOException
	{
		if(msg instanceof HaveMessage)
		{
			execute((HaveMessage)msg);
		}
		else if(msg instanceof WantMessage)
		{
			execute((WantMessage)msg);
		}
		else if(msg instanceof GiveMessage)
		{
			execute((GiveMessage)msg);
		}
	}

	private void execute(HaveMessage msg)
	{
		Set<String> available=new HashSet<String>(msg.have);		
		
		synchronized(have)
		{
			synchronized(want)
			{
				available.removeAll(have);
				want.addAll(available);
				
				if(want.size()>0)
				{
					synchronized(queue)
					{
						queue.add(new WantMessage(new HashSet<String>(want)));
					}
				}
			}
		}
	}

	private void execute(WantMessage msg)
	{
		synchronized(have)
		{
			for(String digest : msg.want)
			{
				if(have.contains(digest))
				{
					synchronized(queue)
					{
						try
						{
							queue.add(new GiveMessage(digest));
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}	

	private void execute(GiveMessage msg) throws IOException
	{	
		File file=new File(root, msg.digest);
		FileOutputStream out=new FileOutputStream(file);
		pump(msg.stream, out, msg.size);
		out.close();		

		synchronized(want)
		{
			want.remove(msg.digest);
		}
		
		synchronized(have)
		{
			have.add(msg.digest);
		}
	}

	public void addMessage(String msg) throws IOException
	{
		String digest=hash(msg);
		File file=new File(root, digest);
		FileOutputStream out=new FileOutputStream(file);
		out.write(msg.getBytes());
		out.close();		
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
}