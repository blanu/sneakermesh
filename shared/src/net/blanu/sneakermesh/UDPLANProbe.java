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
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPLANProbe extends Thread
{
	private static final String TAG = "UDPLANProbe";	
	private static final int MAX_PROBES=10;
	Sneakermesh mesh;
	InetAddress broadcastAddress;
	ListenThread listener;
	SyncThread syncer;
	Set<InetAddress> peers=new HashSet<InetAddress>();
	boolean broadcast=false;
		
	public UDPLANProbe(Sneakermesh sm, InetAddress ia, boolean b)
	{
		mesh=sm;
		broadcastAddress=ia;
		broadcast=b;
	}

	public void run()
	{        
		listener=new ListenThread();
		listener.start();
		
		syncer=new SyncThread();
		syncer.start();

		if(broadcast)
		{
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
	}
    
    public void log(String s)
    {
    	mesh.log(s);
    }
    
    private void probeNetwork(InetAddress ip)
    {
    	log("broadcasting to: "+ip);
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
    
    private class ListenThread extends Thread
    {
    	public void run()
    	{
    		List<String> ips=getLocalIpAddresses();
    		
    		log("running ListenThread");
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
        			log("received udp broadcast from "+packet.getAddress().toString());
                
        			InetAddress peer=packet.getAddress();
        			if(!ips.contains(peer.toString())) // Don't connect to self
        			{
        				synchronized(peers)
        				{
        					peers.add(peer);
        					peers.notify();
        				}
        			}
        		} catch (Exception e) {
        				e.printStackTrace();
        				return;
        		}
    		}
    	}
    }
        
    private class SyncThread extends Thread
    {
    	public void run()
    	{
    		log("running SyncThread");
    		
    		Set<InetAddress> currentPeers;
    		
    		while(true)
    		{	
    			synchronized(peers)
    			{
    				try {
						peers.wait();
	    				currentPeers=new HashSet<InetAddress>(peers);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
    			}
    			
    			for(InetAddress peer : currentPeers)
    			{
        			Socket sock;
					try {
						sock = new Socket(peer, 11917);
	        			log("syncing: "+peer);
	        			mesh.sync(sock,  true);
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
    		}
    	}
    }
    
    private List<String> getLocalIpAddresses() {
    	List<String>addrs=new ArrayList<String>();
    	
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
                    	addrs.add(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException ex) {
            log(ex.toString());
            return null;
        }

        System.out.println("addrs: "+addrs);
        return addrs;
    }	
}