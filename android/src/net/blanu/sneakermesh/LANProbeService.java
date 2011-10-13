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

public class LANProbeService extends Service {
	private static final String TAG = "LANProbe";	
	Sneakermesh mesh;

	public String BROADCAST_ACTION;
	Intent intent;

	@Override
	public void onCreate() {
		super.onCreate();
		BROADCAST_ACTION=this.getPackageName()+".log";
    	intent = new Intent(BROADCAST_ACTION);	
	}

    @Override
    public void onStart(Intent intent, int startId) {
    	log("Started");
        
        mesh=new AndroidSneakermesh(this);
        
        log("resumed");
        String ip=getLocalIpAddress();
        if(ip==null)
        {
        	log("null");        	
        }
        else
        {
        	log("my ip: "+ip);
        	
        	if(checkStorage())
        	{
        		probeNetwork(ip);
        	}
        }
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
		super.onDestroy();
	}	    
    
    public void log(String s)
    {
    	Log.e(TAG, s);
    	
    	intent.putExtra("logline", s);
    	sendBroadcast(intent);    	    	
    }
    
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
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
//          parts[3]="82"; // FIXME - remove debugging hardcoding
//    	  probe(join(parts, "."));
          probe("206.76.83.73");
    	}
    }
    
    private void probe(String ip)
    {
    	ProbeThread t=new ProbeThread(this, ip);
    	t.start();
    	try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
           
    public boolean checkStorage()
    {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    	
    	return mExternalStorageAvailable && mExternalStorageWriteable;
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