package net.blanu.sneakermesh;

public class LANProbeService implements Logger
{
	private static final String TAG = "LANProbeService";	
	
	Sneakermesh mesh=null;
	LANProbe probe=null;	
	
	public LANProbeService()
	{
		mesh=new LaptopSneakermesh();		
		    			
		try
		{
			SyncServer server=new SyncServer(mesh);
			server.start();    			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    			
		probe=new LANProbe(mesh);
		probe.start();
	}
	
	public Sneakermesh getMesh()
	{
		return mesh;
	}

	public void log(String s)
	{
		System.out.println(s);
	}
}
