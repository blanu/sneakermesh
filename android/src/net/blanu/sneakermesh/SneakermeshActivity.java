package net.blanu.sneakermesh;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class SneakermeshActivity extends Activity implements Logger
{	
	private static final String TAG="SneakermeshActivity";
	Intent serviceIntent;
    LANProbeService probe;
    boolean isBound=false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceIntent=new Intent(this, LANProbeService.class);
        startService(serviceIntent);                        
    }	
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        
        doBindService();
    }    
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        
//		stopService(serviceIntent);         
    	doUnbindService();
    }    
    	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.log:
        	launchLog();
        	return true;
        case R.id.list:
        	launchList();
            return true;
        case R.id.add:
        	launchAdd();
        	return true;
        case R.id.addphoto:
        	launchPhoto();
        	return true;
        case R.id.destroy:
        	selfDestruct();
        	return true;        	
        case R.id.sync:
        	forceSync();
        	return true;        	        	
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
    
    protected void launchLog()
    {
    	log("Launching log activity");
    	Intent intent=new Intent(this, LogViewerActivity.class);
    	startActivity(intent);    	
    }    
    
    protected void launchList()
    {
    	log("Launching list activity");
    	Intent intent=new Intent(this, MessageListActivity.class);
    	startActivity(intent);
    }
    
    protected void launchAdd()
    {
    	log("Launching add activity");
    	Intent intent=new Intent(this, AddMessageActivity.class);
    	startActivity(intent);
    }    
    
    protected void launchPhoto()
    {
    	log("Launching photo activity");
    	Intent intent=new Intent(this, AddPhotoActivity.class);
    	startActivity(intent);
    }    
    
    protected void forceSync()
    {
    	probe.forceSync();
    }
    
    public void selfDestruct()
    {
    	probe.getMesh().deleteMessages();
    	launchList();
    }
                
    public void log(String logline)
    {
    	Log.e(TAG, logline);
    }
    

    private ServiceConnection connection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            probe = ((LANProbeService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className)
        {
            probe = null;
        }
    };

    void doBindService()
    {
        bindService(new Intent(this, LANProbeService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    void doUnbindService()
    {
        if (isBound)
        {
            unbindService(connection);
            isBound = false;
        }
    }
}