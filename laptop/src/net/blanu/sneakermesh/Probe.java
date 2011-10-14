package net.blanu.sneakermesh;

public class Probe
{
	public static void main(String[] args)
	{
		Sneakermesh mesh=new LaptopSneakermesh();		
		LANProbe probe=new LANProbe(mesh);
		probe.start();
	}
}
