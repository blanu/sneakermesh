package net.blanu.sneakermesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.blanu.sneakermesh.content.Message;
import net.blanu.sneakermesh.content.TextMessage;
import net.blanu.sneakermesh.protocol.Command;
import net.blanu.sneakermesh.protocol.GiveCommand;
import net.blanu.sneakermesh.protocol.HaveCommand;
import net.blanu.sneakermesh.protocol.PresenceCommand;
import net.blanu.sneakermesh.protocol.WantCommand;

abstract public class Sneakermesh implements Logger
{
	private static final String TAG = "Sneakermesh";
	
	private String password=null;

	private BlockingQueue<Command> queue;

	protected PeerDb db;
	protected File root;
	protected File tmp;
	protected File texts;
	
	DatagramSocket socket=null;
	
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
		
		try {
			socket=new DatagramSocket(11917);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void setPeerDb(PeerDb pdb)
	{
		db=pdb;
		
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
	
	public void addPeer(InetAddress peer)
	{
		String peerIp=peer.toString();
		db.addPeer(peerIp);
	}
	
	public List<String> getPeers()
	{
		return db.getPeers();
	}
	
	public void sync()
	{			
		try
		{
			while(true)
			{
				List<String> peers=db.getPeers();
				for(String peer : peers)
				{											
					sendHaves(peer);
					sendWants(peer);
					sendGives(peer);
				}					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	private void sendHaves(String peer)
	{
		List<String> hashes=db.getHaveExcept("127.0.0.1", peer);
		for(String hash : hashes)
		{
			Command msg=new HaveCommand(peer, hash);
			send(msg);
		}			
	}

	private void sendWants(String peer)
	{
		List<String> hashes=db.getWantAndHave("127.0.0.1", peer);
		for(String hash : hashes)
		{
			Command msg=new WantCommand(peer, hash);
			send(msg);
		}			
	}

	private void sendGives(String peer)
	{
		List<String> hashes=db.getWantAndHave(peer, "127.0.0.1");
		for(String hash : hashes)
		{
			try
			{
				Command msg=new GiveCommand(peer, hash);
				send(msg);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}			
	}		

	private void send(Command msg)
	{
		DatagramSocket socket;
		try {
			socket = new DatagramSocket();
			String data=msg.toString();
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), InetAddress.getByName(msg.peer), 11917);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
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

		db.deleteHave("127.0.0.1");
		db.deleteWant("127.0.0.1");
						
		for(int x=0; x<files.length; x++)
		{
			File f=new File(texts, files[x]);
			if(f.length()>0)
			{
				db.putHave("127.0.0.1", files[x]);
			}
			else
			{
				db.putWant("127.0.0.1", files[x]);
			}
		}
	}
	
	public void execute(Command msg) throws IOException, InterruptedException
	{
		if(msg instanceof PresenceCommand)
		{
			execute((PresenceCommand)msg);
		}
		else if(msg instanceof HaveCommand)
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

	private void execute(PresenceCommand msg)
	{
		db.addPeer(msg.peer);
	}
	
	private void execute(HaveCommand msg)
	{
		db.putHave(msg.peer, msg.have);
	}

	private void execute(WantCommand msg)
	{
		db.putWant(msg.peer, msg.want);
	}	

	private void execute(GiveCommand cmd) throws IOException, InterruptedException
	{	
		if(cmd.msg.type==Message.MSG_TEXT)
		{
			log("saving text");
			cmd.msg.save(texts);
		}

		db.deletePeerWant(cmd.peer, cmd.digest);
		db.putHave(cmd.peer, cmd.digest);
		fireHaveChangeEvent(cmd.digest);		
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
		log("addMessage: "+msg);
		String digest=msg.digest;
		msg.save(texts);

		db.putHave("127.0.0.1", digest);
		log("now have: "+db.getHave("127.0.0.1"));
		fireHaveChangeEvent(digest);
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
				e.printStackTrace();
		}
	}

	public List<Message> getMessages()
	{
		List<Message> msgs=new ArrayList<Message>();

		List<String> have=db.getHave("127.0.0.1");
		log("messages I have: "+have);
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
				
		return msgs;
	}

	public void deleteMessages()
	{
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
		
		db.deleteHave("127.0.0.1");
	}		
}
