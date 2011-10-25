package net.blanu.sneakermesh;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class LogViewerActivity extends SneakermeshActivity
{	
	private static final String TAG="LogViewerActivity";
	String BROADCAST_ACTION;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BROADCAST_ACTION=getPackageName()+".log";
        
        setContentView(R.layout.main);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        
    	TextView text=(TextView)findViewById(R.id.textview);        
        text.setText("Init...\n");
        
        startService(serviceIntent);                        
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));                
   }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        
		unregisterReceiver(broadcastReceiver);
		stopService(serviceIntent);         
    }
    
    private void updateUI(Intent intent) {
    	String logline = intent.getStringExtra("logline"); 
    	log(logline);
    }
    
    public void log(String logline)
    {
       	Log.e(TAG, logline);
       	TextView text=(TextView)findViewById(R.id.textview);
    	if(text!=null && logline!=null)
    	{
    		text.append(logline);
    		text.append("\n");
    	}
    	else
    	{
    		Log.e(TAG, "Could not update because of null: "+text+"/"+logline);
    	}    	
    }
    
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "onReceive");
        	updateUI(intent);       
        }
    };    
}