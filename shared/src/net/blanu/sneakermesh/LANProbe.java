package net.blanu.sneakermesh;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;

public class LANProbe extends Thread
{
	private static final String TAG = "LANProbe";	
	Sneakermesh mesh;
		
	public LANProbe(Sneakermesh sm)
	{
		mesh=sm;
	}

	public void run()
	{
    	log("Started");        
        log("resumed");
        
        String ip=getLocalIpAddress();
        if(ip==null)
        {
        	log("null");        	
        }
        else
        {
        	log("my ip: "+ip);
        	
        	probeNetwork(ip);
        }
    }
    
    public void log(String s)
    {
    	System.out.println(s);
    }
    
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            log(ex.toString());
        }
        return null;
    }    
    
    private void probeNetwork(String ip)
    {
    	String[] parts=ip.split("\\.");
    	String me=parts[3];
    	
    	List<String> targets=new ArrayList<String>();
    	
    	for(int i=1; i<256; i++)
    	{
    	  String target=String.valueOf(i);
    	  if(!target.equals(me))
    	  {
    	    targets.add(target);
    	  }    	  
    	}
    	
    	Collections.shuffle(targets);
    	
    	for(String target : targets)
    	{
          parts[3]=target;
          parts[3]="93"; // FIXME - remove debugging hardcoding
    	  probe(join(parts, "."));
//          probe("206.76.83.73");
    	}
    }
    
    private void probe(String ip)
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