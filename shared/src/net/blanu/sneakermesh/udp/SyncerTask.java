package net.blanu.sneakermesh.udp;

import java.util.TimerTask;

import net.blanu.sneakermesh.Sneakermesh;

public class SyncerTask extends TimerTask
{
	Sneakermesh mesh=null;
    
	public SyncerTask(Sneakermesh sm)
	{
		super();
		
		mesh=sm;    		
	}
	
	@Override
	public void run()
	{
		if(mesh!=null)
		{
			mesh.sync();
		}
	}
}  