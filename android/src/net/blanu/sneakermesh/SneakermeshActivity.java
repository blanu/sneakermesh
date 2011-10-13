package net.blanu.sneakermesh;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SneakermeshActivity extends Activity {	
	private static final String TAG="SneakermeshActivity";
	Intent serviceIntent;
	String BROADCAST_ACTION;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BROADCAST_ACTION=getPackageName()+".log";
        
        setContentView(R.layout.main);
        serviceIntent=new Intent(this, LANProbeService.class);
    }
        
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
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
    
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }    
    
    private void updateUI(Intent intent) {
    	Log.e(TAG, "updateUI");
    	String logline = intent.getStringExtra("logline"); 
    	Log.e(TAG, logline);
    	log(logline);
    }
    
    private void log(String logline)
    {
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