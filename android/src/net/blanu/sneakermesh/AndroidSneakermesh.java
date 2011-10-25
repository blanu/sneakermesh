package net.blanu.sneakermesh;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AndroidSneakermesh extends Sneakermesh
{
	static private final String TAG="Sneakermesh";
	
	private Context probe;
	
	public AndroidSneakermesh(Context p)
	{
		super(p.getExternalFilesDir(null));		
		if(root==null)
		{
			Log.e(TAG, "null extdir: "+p.getExternalFilesDir(null));
		}
		
		probe=p;
	}
	
	public void log(String s)
	{
		if(probe!=null)
		{
			((Logger)probe).log(s);
		}
	}
	
	public void fireHaveChangeEvent(String digest)
	{
		String REFRESH_ACTION=probe.getPackageName()+".refresh";
    	Intent intent = new Intent(REFRESH_ACTION);		
    	probe.sendBroadcast(intent);
    }	
}
