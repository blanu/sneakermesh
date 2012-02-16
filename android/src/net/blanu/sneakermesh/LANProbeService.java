package net.blanu.sneakermesh;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import net.blanu.sneakermesh.content.Message;
import net.blanu.sneakermesh.udp.SyncServer;
import net.blanu.sneakermesh.udp.UDPBroadcastTask;
import net.blanu.sneakermesh.udp.SyncerTask;
import net.blanu.sneakermesh.udp.UDPUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class LANProbeService extends Service implements Logger
{
	private static final String TAG = "LANProbe";	
	static Sneakermesh mesh=null;
	static SyncServer probe=null;
	static UDPBroadcastTask broadcast=null;
	static SyncerTask syncer=null;

	static public String BROADCAST_ACTION;
	static Intent intent;
    private final IBinder mBinder = new LocalBinder();
    
    List<String> lines=new ArrayList<String>();
    
	@Override
	public void onCreate() {
		super.onCreate();
		BROADCAST_ACTION=this.getPackageName()+".log";
    	intent = new Intent(BROADCAST_ACTION);	
    	
		Message.setLogger(this);
		net.blanu.sneakermesh.Util.setLogger(this);    	
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if(mesh==null)
    	{
    		if(checkStorage())
    		{
    			mesh=new AndroidSneakermesh(this);

    			Timer timer = new Timer();    			
    			
    			broadcast=new UDPBroadcastTask(this);    			
    			timer.scheduleAtFixedRate(broadcast, 1, 30*1000);
    			
    			syncer=new SyncerTask(mesh);    			
    			timer.scheduleAtFixedRate(syncer, 1, 30*1000);
    			
    			try {
					probe=new SyncServer(mesh, UDPUtil.getBroadcastAddress(this));
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	return START_STICKY;
    }  
    
    public Sneakermesh getMesh()
    {
    	return mesh;
    }
    
    public class LocalBinder extends Binder {
        public LANProbeService getService() {
            return LANProbeService.this;
        }
    }    
    
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {		
		super.onDestroy();
	}	    
	
	public void forceSync()
	{
		syncer.run();
	}
	
	public void forceBroadcast()
	{
		broadcast.run();
	}	
    
    public void log(String s)
    {
    	Log.e(TAG, s);
    	
    	lines.add(s);
    	if(lines.size()>30)
    	{
    		lines.remove(0);
    	}

    	intent.putExtra("logline", net.blanu.sneakermesh.Util.join((String[])lines.toArray(new String[0]), "\n"));
    	sendBroadcast(intent);    	    	
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
}