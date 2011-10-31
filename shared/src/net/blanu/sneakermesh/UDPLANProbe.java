package net.blanu.sneakermesh;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPLANProbe extends Thread
{
	private static final String TAG = "UDPLANProbe";	
	private static final int MAX_PROBES=10;
	Sneakermesh mesh;
	InetAddress broadcastAddress;
		
	public UDPLANProbe(Sneakermesh sm, InetAddress ia)
	{
		mesh=sm;
		broadcastAddress=ia;
	}

	public void run()
	{        
		new SyncThread().start();
		
        while(true)
        {
        	probeNetwork(broadcastAddress);
        	try {
        		sleep(30000); // 30 seconds
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
        }
	}
    
    public void log(String s)
    {
    	mesh.log(s);
    }
    
    private void probeNetwork(InetAddress ip)
    {
    	log("braodcasting to: "+ip);
        DatagramSocket socket;
		try {
			socket = new DatagramSocket();
	        socket.setBroadcast(true);
	        String data="\0x00";
	        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), ip, 11917);
	        socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private class SyncThread extends Thread
    {
    	public void run()
    	{
    		log("running SyncThread");
    		while(true)
    		{
    			DatagramSocket socket;
    			try {
    				socket = new DatagramSocket(11917);
        			socket.setBroadcast(true);
        			
        			byte[] buf = new byte[1024];
        			DatagramPacket packet = new DatagramPacket(buf, buf.length);
        			log("ready to receive udp broadcast");
        			socket.receive(packet);
        			log("received udp broadcast");
                
        			InetAddress peer=packet.getAddress();
        			Socket sock=new Socket(peer, 11917);
        			log("syncing: "+peer);
        			mesh.sync(sock,  true);
    			} catch (Exception e) {
    				e.printStackTrace();
    				return;
    			}
    		}
    	}
    }
        
	private static String join(String[] s, String delim)
	{
		if(s.length==0)
		{
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<s.length-1; i++)
		{
			buffer.append(s[i]);
			buffer.append(delim);
		}
		buffer.append(s[s.length-1]);

		return buffer.toString();
	}	
}