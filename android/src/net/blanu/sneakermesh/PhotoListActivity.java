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

public class PhotoListActivity extends SneakermeshListActivity implements Logger
{
	private static final String TAG="MessageListActivity";
	        
    protected void refreshUI()
    {
    	probe.getMesh().loadHashes();
    	adapter.clear();
    	
    	List<Message> msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
    	
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		adapter.add(tm.getText());
    	}
    	log("NOTIFY DATA SET CHANGED");
    	adapter.notifyDataSetChanged();
    }
    
    protected void createView()
    {
        List<Message>msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
        adapter=new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<String>());
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		adapter.add(tm.getText());
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
}