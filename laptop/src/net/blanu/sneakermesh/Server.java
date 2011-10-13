package net.blanu.sneakermesh;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server
{
	private static final String TAG="Server";
	Sneakermesh mesh;
	
	public static void main(String[] args)
	{
		Server server=new Server();
		server.run();
	}
	
	public Server()
	{
		mesh=new LaptopSneakermesh();
		System.out.println("my ip: "+getLocalIpAddress());
	}
	
	public void run()
	{
		ServerSocket server = null;
		try {
			server = new ServerSocket(11917);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		while(true)
		{
			Socket sock = null;
			try {
				sock = server.accept();
				System.out.println("Accepted: "+sock);
//				mesh.sync(sock, false);				
				mesh.sync(sock, true);				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	    
	
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().toString().contains(":")) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }    	
}
