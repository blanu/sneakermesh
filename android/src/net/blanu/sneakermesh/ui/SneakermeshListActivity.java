package net.blanu.sneakermesh.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.blanu.sneakermesh.LANProbeService;
import net.blanu.sneakermesh.Logger;
import net.blanu.sneakermesh.R;
import net.blanu.sneakermesh.LANProbeService.LocalBinder;
import net.blanu.sneakermesh.R.id;
import net.blanu.sneakermesh.R.menu;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

abstract public class SneakermeshListActivity extends ListActivity implements Logger
{
	private static final String TAG="SneakermeshListActivity";
	Intent serviceIntent;
	String REFRESH_ACTION;
	ArrayAdapter<String> adapter;
    LANProbeService probe;
    boolean isBound=false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        REFRESH_ACTION=this.getPackageName()+".refresh";        
        
        serviceIntent=new Intent(this, LANProbeService.class);        
        startService(serviceIntent);                        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        
        registerReceiver(broadcastReceiver, new IntentFilter(REFRESH_ACTION));        
        doBindService();
    }       
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        
		unregisterReceiver(broadcastReceiver);
//		stopService(serviceIntent);         
		doUnbindService();
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.log:
        	launchLog();
            return true;
        case R.id.list:
        	launchList();
            return true;
        case R.id.addphoto:
        	launchPhoto();
        	return true;        	
        case R.id.sync:
        	forceSync();
        	return true;        	        	        	
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
    
    private void launchLog()
    {
    	log("Launching log activity");
    	Intent intent=new Intent(this, LogViewerActivity.class);
    	startActivity(intent);    	
    }
    
    private void launchList()
    {
    	log("Launching list activity");
    	Intent intent=new Intent(this, MessageListActivity.class);
    	startActivity(intent);
    }      
        
    protected void launchPhoto()
    {
    	log("Launching photo activity");
    	Intent intent=new Intent(this, AddPhotoActivity.class);
    	startActivity(intent);
    }    

    protected void launchPassword()
    {
    	log("Launching password activity");
    	Intent intent=new Intent(this, EnterPasswordActivity.class);
    	startActivity(intent);
    }        
    
    protected void forceSync()
    {
    	probe.forceSync();
    }    
    
    public void log(String logline)
    {
    	Log.e(TAG, logline);
    }
    
    abstract protected void refreshUI();
    abstract protected void createView();
    
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "onReceive");
        	refreshUI();       
        }
    };          
    
    private ServiceConnection connection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            probe = ((LANProbeService.LocalBinder)service).getService();
            createView();
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