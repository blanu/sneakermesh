package net.blanu.sneakermesh;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LANProbe extends Thread
{
	private static final String TAG = "LANProbe";	
	private static final int MAX_PROBES=10;
	Sneakermesh mesh;
		
	public LANProbe(Sneakermesh sm)
	{
		mesh=sm;
	}

	public void run()
	{
    	log("Started");        
        log("resumed");
        
        List<String> ips=getLocalIpAddresses();
        if(ips==null)
        {
        	log("null");        	
        }
        else
        {
        	log("my ips: "+ips);

        	while(true)
        	{
        		for(String ip : ips)
        		{
        			log("probing: "+ip);
        			probeNetwork(ip);
        			try {
        				sleep(30000); // 30 seconds
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        }
    }
    
    public void log(String s)
    {
    	mesh.log(s);
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
    	
    	BlockingQueue<String> testing=new LinkedBlockingQueue<String>();
    	
    	for(String target : targets)
    	{
    		parts[3]=target;
    		testing.add(join(parts, "."));
    	}
    	
    	BlockingQueue<Socket> results=new LinkedBlockingQueue<Socket>();
    	
    	ProbeThread[] probes=new ProbeThread[MAX_PROBES];
    	
    	for(int x=0; x<MAX_PROBES; x++)
    	{
    		probes[x]=new ProbeThread(testing, results);
    		probes[x].start();
    	}

    	for(int x=0; x<MAX_PROBES; x++)
    	{
    		try {
				probes[x].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	log("found "+results.size()+" peers");
    	
    	for(Socket peer : results)
    	{
    		log("syncing: "+peer);
    		mesh.sync(peer,  true);
    	}
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