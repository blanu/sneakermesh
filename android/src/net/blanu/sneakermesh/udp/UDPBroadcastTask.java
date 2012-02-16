package net.blanu.sneakermesh.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;

public class UDPBroadcastTask extends TimerTask
{
    static private final String TAG="UDPBroadcast";
	InetAddress ip;    
	UDPBroadcaster broadcaster=null;
	
	public UDPBroadcastTask(Context context)
	{
		super();
				
		try {
			ip = UDPUtil.getBroadcastAddress(context);
			broadcaster=new UDPBroadcaster(ip);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	@Override
	public void run()
	{
		Log.e(TAG, "broadcast.run()");
		try
		{
			if(broadcaster!=null)
			{
				Log.e(TAG, "Sending broadcast to "+ip);
				broadcaster.broadcast();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
}
