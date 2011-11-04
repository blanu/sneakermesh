package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

abstract public class Sneakermesh implements Logger
{
	private static final String TAG = "Sneakermesh";
	
	private String password=null;

	Set<String> have;
	private Set<String> want;
	
	private BlockingQueue<Command> queue;
	
	protected File root;
	protected File tmp;
	protected File texts;
	
	public Sneakermesh(File f)
	{
		root=f;
		GiveCommand.root=f;
		
		Command.setLogger(this);
		
		if(!root.exists())
		{
			root.mkdirs();
		}
		
		tmp=new File(root, "tmp");
		if(!tmp.exists())
		{
			tmp.mkdirs();
		}
		Message.setTmp(tmp);
		
		texts=new File(root, "texts");
		if(!texts.exists())
		{
			texts.mkdirs();
		}
		
        have=new HashSet<String>();
        want=new HashSet<String>();
        queue=new LinkedBlockingQueue<Command>();
        
        loadHashes();
	}
	
	public abstract void log(String s);
	
	public void setPassword(String s)
	{
		password=s;
	}
	
	public boolean isPasswordSet()
	{
		return password!=null;
	}
	
	public void sync(Socket sock, boolean pushHave)
	{
		try {
			DataInputStream is=new DataInputStream(sock.getInputStream());
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			
			queue.add(new HaveCommand(have));
			
			Thread reader=new ReadSync(is);
			reader.start();
			Thread writer=new WriteSync(out);
			writer.start();
			
			/*
			try {
				reader.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				writer.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
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
				Command msg=Command.readCommand(is);
				while(msg!=null)
				{
					log("read command: "+msg);
					try
					{
						execute(msg);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					log("reading command");
					msg=Command.readCommand(is);
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
				while(true)
				{
					log("waiting for something to write");
					Command msg=queue.take();
					log("writing: "+msg);
					msg.write(out);
					out.flush();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
	}		

	public void loadHashes()
	{
		if(texts==null)
		{
			return;
		}
		String[] files=texts.list();
		if(files==null)
		{
			return;
		}
		System.out.println("files: "+files);

		synchronized(have)
		{
			synchronized(want)
			{
				have.clear();
				want.clear();
				
				for(int x=0; x<files.length; x++)
				{
					File f=new File(texts, files[x]);
					if(f.length()>0)
					{
						have.add(files[x]);
					}
					else
					{
						want.add(files[x]);
					}
				}
			}
		}		
	}
	
	private void execute(Command msg) throws IOException, InterruptedException
	{
		if(msg instanceof HaveCommand)
		{
			execute((HaveCommand)msg);
		}
		else if(msg instanceof WantCommand)
		{
			execute((WantCommand)msg);
		}
		else if(msg instanceof GiveCommand)
		{
			execute((GiveCommand)msg);
		}
	}

	private void execute(HaveCommand msg)
	{
		Set<String> available=new HashSet<String>(msg.have);		

		synchronized(have)
		{
			synchronized(want)
			{
				available.removeAll(have);
				log("available: "+available.size());
				want.addAll(available);
				log("now want: "+want.size());
				
				if(want.size()>0)
				{
					try {
						queue.put(new WantCommand(new HashSet<String>(want)));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void execute(WantCommand msg)
	{
		synchronized(have)
		{
			for(String digest : msg.want)
			{
				if(have.contains(digest))
				{
					try
					{
						queue.put(new GiveCommand(digest));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}	

	private void execute(GiveCommand cmd) throws IOException, InterruptedException
	{	
		if(cmd.msg.type==Message.MSG_TEXT)
		{
			log("saving text");
			cmd.msg.save(texts);
		}

		synchronized(want)
		{
			want.remove(cmd.digest);
			log("now want: "+want.size());
			queue.put(new WantCommand(new HashSet<String>(want)));
		}

		synchronized(have)
		{
			have.add(cmd.digest);
			log("now have: "+have.size());
			queue.put(new HaveCommand(new HashSet<String>(have)));
			fireHaveChangeEvent(cmd.digest);
		}
	}
	
	public void fireHaveChangeEvent(String digest)
	{		
		log("fireHaveChangeEvent default");
	}

	public void addTextMessage(String msg) throws IOException
	{
		TextMessage tm=new TextMessage(msg);
		addMessage(tm);
	}
	
	public void addMessage(Message msg) throws IOException
	{
		String digest=msg.digest;
		msg.save(texts);
		
		synchronized(have)
		{
			have.add(digest);
			fireHaveChangeEvent(digest);
			synchronized(queue)
			{
				try {
					queue.put(new HaveCommand(have));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
	
	public void pump(InputStream is, OutputStream out, long maxlen)
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
			while(read!=-1 && count<maxlen)
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

	public List<Message> getMessages()
	{
		List<Message> msgs=new ArrayList<Message>();
		
		synchronized(have)
		{
			for(String digest : have)
			{
				File file=new File(texts, digest);
				long size=file.length();
				if(file.exists() && size>0)
				{
					try
					{
						TextMessage msg=(TextMessage)Message.readMessage(file);
						msgs.add(msg);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					log("Bad file: "+file);
				}
			}
		}
				
		return msgs;
	}

	public void deleteMessages() {
		if(texts==null)
		{
			log("No texts to delete...");
			return;
		}
		String[] files=texts.list();
		if(files==null)
		{
			return;
		}
				
		for(int x=0; x<files.length; x++)
		{
			File f=new File(texts, files[x]);
			f.delete();
		}		
		
		synchronized(have)
		{
			have.clear();
		}		
	}		
}
