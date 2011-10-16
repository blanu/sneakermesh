package net.blanu.sneakermesh;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ProbeThread extends Thread
{
	BlockingQueue<String> testing;
	BlockingQueue<Socket> results;
	
	public ProbeThread(BlockingQueue<String> qt, BlockingQueue<Socket> qr)
	{
		testing=qt;
		results=qr;
	}
	
	public void run()
	{
		while(!testing.isEmpty())
		{
			try {
				String ip = testing.take();
				Socket result=probe(ip);
				if(result!=null)
				{
					results.put(result);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
	
    private Socket probe(String ip)
    {
    	Socket sock=null;
    	
    	try
    	{
    	  sock=new Socket(ip, 11917);
    	  return sock;
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }           	
}
