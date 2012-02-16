package net.blanu.sneakermesh.udp;

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

import net.blanu.sneakermesh.Sneakermesh;
import net.blanu.sneakermesh.Util;
import net.blanu.sneakermesh.protocol.Command;

/*
 * Get all the messages I have that no one else has and send haves to peers that don't have them
 * Get all the messages that others have and I don't have and send wants to peers that have them
 * Get all the wants of other peers and compare to my haves and send gives to those peers
 */

public class SyncServer
{
	private static final String TAG = "UDPLANProbe";	
	private static final int MAX_PROBES=10;
	Sneakermesh mesh;
    DatagramSocket socket;
    DatagramSocket broadcastSocket;
    String data="\0x00";
    
	ListenThread listener;
	BroadcastListenThread broadcastListener;
			
	public SyncServer(Sneakermesh sm, InetAddress broadcastAddress)
	{
		mesh=sm;
		
		try {
			socket = new DatagramSocket(11917);
			
			broadcastSocket = new DatagramSocket(11916);
			broadcastSocket.setBroadcast(true);			
		} catch (SocketException e) {
			e.printStackTrace();
		}

		listener=new ListenThread();
		listener.start();
		
		broadcastListener=new BroadcastListenThread();
		broadcastListener.start();
	}
    
    public void log(String s)
    {
    	mesh.log(s);
    }
    
    private class ListenThread extends Thread
    {
    	public void run()
    	{
    		List<String> ips=Util.getLocalIpAddresses();
    		
    		log("running ListenThread");
    		while(true)
    		{
    			try {
        			byte[] buf = new byte[1024];
        			DatagramPacket packet = new DatagramPacket(buf, buf.length);
        			log("ready to receive udp packet");
        			socket.receive(packet);
        			log("received udp packet from "+packet.getAddress().toString());
        			log("I am "+ips);
                
        			InetAddress peer=packet.getAddress();
        			if(!ips.contains(peer.toString())) // Don't connect to self
        			{
        				log("real peer!");
        				mesh.addPeer(peer);
        				Command cmd=Command.readCommand(peer.toString(), buf);
        				mesh.execute(cmd);
        			}
        		} catch (Exception e) {
        				e.printStackTrace();
        				return;
        		}
    		}
    	}
    }
    
    private class BroadcastListenThread extends Thread
    {
    	public void run()
    	{
    		List<String> ips=Util.getLocalIpAddresses();
    		
    		log("running ListenThread");
    		while(true)
    		{
    			try {
        			byte[] buf = new byte[1024];
        			DatagramPacket packet = new DatagramPacket(buf, buf.length);
        			log("ready to receive udp broadcast");
        			broadcastSocket.receive(packet);
        			log("received udp broadcast from "+packet.getAddress().toString());
                
        			InetAddress peer=packet.getAddress();
        			if(!ips.contains(peer.toString())) // Don't connect to self
        			{
        				mesh.addPeer(peer);
        			}
        		} catch (Exception e) {
        				e.printStackTrace();
        				return;
        		}
    		}
    	}
    }    
}