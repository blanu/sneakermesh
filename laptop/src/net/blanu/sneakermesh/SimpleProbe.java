package net.blanu.sneakermesh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SimpleProbe
{
	static final String ip="";
	
	public static void main(String[] args) throws IOException
	{
		Sneakermesh mesh=new LaptopSneakermesh("sneakermesh2");
		Socket sock=new Socket();
  	  	sock.connect(new InetSocketAddress(ip, 11917), 200);
  	  	mesh.sync(sock, true);
	}
}
