package net.blanu.sneakermesh;

import android.util.Log;

public class AndroidSneakermesh extends Sneakermesh
{
	static private final String TAG="Sneakermesh";
	
	private LANProbeService probe;
	
	public AndroidSneakermesh(LANProbeService p)
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
			probe.log(s);
		}
	}
}
