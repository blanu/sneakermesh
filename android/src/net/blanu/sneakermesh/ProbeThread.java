package net.blanu.sneakermesh;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class ProbeThread extends Thread
{
	private static final String TAG = "ProbeThread";	
	Sneakermesh mesh;
	LANProbeService parent;
	String ip;

	public ProbeThread(LANProbeService lps, String s)
	{
		parent=lps;
		ip=s;
        
        mesh=new AndroidSneakermesh(parent);
	}	
    
    public void log(String s)
    {
    	parent.log(s);
    }    
    
	public void run()
	{            
    	log(ip);

    	Socket sock=null;
    	
    	try
    	{
    	  sock=new Socket(ip, 11917);
    	}
    	catch(Exception e)
    	{
    		log("fail ");
    		return;
    	}

  	  log("succeed");
  	  mesh.sync(sock, true);
    }           
}