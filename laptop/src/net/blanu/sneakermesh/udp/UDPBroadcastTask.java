package net.blanu.sneakermesh.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.TimerTask;

public class UDPBroadcastTask extends TimerTask
{
	InetAddress ip;    
	UDPBroadcaster broadcaster=null;
	
	public UDPBroadcastTask()
	{
		super();
				
		try {
			ip = UDPUtil.getBroadcastAddress();
			broadcaster=new UDPBroadcaster(ip);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	@Override
	public void run()
	{
		System.out.println("broadcast.run()");
		try
		{
			if(broadcaster!=null)
			{
				System.out.println("Sending broadcast to "+ip);
				broadcaster.broadcast();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
}
