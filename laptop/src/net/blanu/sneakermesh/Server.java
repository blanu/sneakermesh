package net.blanu.sneakermesh;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

public class Server extends Thread
{
	private static final String TAG="Server";
	Sneakermesh mesh;
	
	public static void main(String[] args)
	{
		Sneakermesh sm=new LaptopSneakermesh();
		
		SyncServer server=new SyncServer(sm);
		server.start();
		
		Scanner scanner=new Scanner(System.in);
		while(true)
		{
			String line=scanner.nextLine();
			try {
				sm.addMessage(new TextMessage(line));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
