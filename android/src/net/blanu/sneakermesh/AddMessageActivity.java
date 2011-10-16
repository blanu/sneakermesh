package net.blanu.sneakermesh;

import java.io.IOException;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddMessageActivity extends SneakermeshActivity implements Logger
{
	private static final String TAG="AddMessageActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.add_message);        
        
        final Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final TextView tv = (TextView) findViewById(R.id.msgtext);
                String s=tv.getText().toString();
            	try {
					probe.getMesh().addMessage(new TextMessage(s));
					
					launchList();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });        
    }
    
    public void log(String logline)
    {
    	Log.e(TAG, logline);
    }
}