package net.blanu.sneakermesh;

import java.io.IOException;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageListActivity extends SneakermeshListActivity implements Logger
{
	private static final String TAG="MessageListActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.list_messages);        
        
        final Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                final TextView tv = (TextView) findViewById(R.id.msgtext);
                String s=tv.getText().toString();
                tv.setText("");
                tv.clearComposingText();
                                
            	try
            	{
					probe.getMesh().addMessage(new TextMessage(s));
					
					refreshUI();					
				} catch (IOException e) {
					e.printStackTrace();
				}     
            	
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(tv.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);            	
            }
        });        
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        
        if(probe!=null && !probe.getMesh().isPasswordSet())
        {
        	launchPassword();
        }
    }       
	
    protected void refreshUI()
    {
    	probe.getMesh().loadHashes();
    	adapter.clear();
    	
    	List<Message> msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
    	Collections.reverse(msgs);
    	
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		adapter.add(tm.getText());
    	}
    	adapter.notifyDataSetChanged();    	
    }
    
    protected void createView()
    {
        List<Message>msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
    	Collections.reverse(msgs);
    	
        adapter=new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<String>());
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		adapter.add(tm.getText());
    	}
        setListAdapter(adapter);
        
        ListView lv = getListView();
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);

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