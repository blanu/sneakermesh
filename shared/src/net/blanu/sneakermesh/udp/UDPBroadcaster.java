package net.blanu.sneakermesh.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

public class UDPBroadcaster
{
    DatagramSocket socket;
    DatagramPacket packet;
    static private final String TAG="UDPBroadcast";
	InetAddress ip;    
	
	public UDPBroadcaster(InetAddress ia)
	{
		ip = ia;
		
		try {
			socket = new DatagramSocket();
	        socket.setBroadcast(true);
	        String data="\0x00";
	        packet = new DatagramPacket(data.getBytes(), data.length(), ip, 11917);
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	public void broadcast()
	{
		try
		{
			if(socket!=null && packet!=null)
			{
				socket.send(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
}
