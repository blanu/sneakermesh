package net.blanu.sneakermesh.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class UDPUtil
{
	static public InetAddress getBroadcastAddress() throws IOException
	{
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements())
		{
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback())
			{
				continue;    // Don't want to broadcast to the loopback interface
			}
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
			{
				InetAddress broadcast = interfaceAddress.getBroadcast();
			    if (broadcast == null)
			    {
			      continue;
			    }
			    return broadcast;
			  }
		}
		
		return null;
	}	
}