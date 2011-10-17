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
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class LANProbeService extends Service implements Logger
{
	private static final String TAG = "LANProbe";	
	Sneakermesh mesh=null;
	LANProbe probe=null;

	public String BROADCAST_ACTION;
	Intent intent;
    private final IBinder mBinder = new LocalBinder();
    
	@Override
	public void onCreate() {
		super.onCreate();
		BROADCAST_ACTION=this.getPackageName()+".log";
    	intent = new Intent(BROADCAST_ACTION);	
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	log("Started");

    	if(mesh==null)
    	{
    		if(checkStorage())
    		{
    			log("New mesh");

    			mesh=new AndroidSneakermesh(this);
    			
    			try
    			{
    				SyncServer server=new SyncServer(mesh);
    				server.start();    			
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    			
    			probe=new LANProbe(mesh);
    			probe.start();
    		}
    	}
    	
    	return START_STICKY;
    }
    
    public Sneakermesh getMesh()
    {
    	return mesh;
    }
    
    public class LocalBinder extends Binder {
        LANProbeService getService() {
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
    
    public void log(String s)
    {
    	Log.e(TAG, s);
    	
    	intent.putExtra("logline", s);
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