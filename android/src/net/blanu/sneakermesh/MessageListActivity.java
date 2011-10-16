package net.blanu.sneakermesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

public class MessageListActivity extends ListActivity implements Logger
{
	private static final String TAG="MessageListActivity";
	Intent serviceIntent;
	String REFRESH_ACTION;
	ArrayAdapter<String> adapter;
    private LANProbeService probe;
    boolean isBound=false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        REFRESH_ACTION=this.getPackageName()+".refresh";        
        
        serviceIntent=new Intent(this, LANProbeService.class);        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        
        startService(serviceIntent);                        
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
        case R.id.add:
        	launchAdd();
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

    private void launchAdd()
    {
    	log("Launching add activity");
    	Intent intent=new Intent(this, AddMessageActivity.class);
    	startActivity(intent);
    }        
        
    public void log(String logline)
    {
    	Log.e(TAG, logline);
    }
    
    private void refreshUI()
    {
    	probe.getMesh().loadHashes();
    	adapter.clear();
    	
    	List<Message> msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
    	
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		adapter.add(tm.text);
    	}
    	log("NOTIFY DATA SET CHANGED");
    	adapter.notifyDataSetChanged();
    }
    
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "onReceive");
        	refreshUI();       
        }
    };      
    
    protected void createView()
    {
        List<Message>msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
        adapter=new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<String>());
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		adapter.add(tm.text);
    	}
        setListAdapter(adapter);
        
        ListView lv = getListView();
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });            	
    }
    
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