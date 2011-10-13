package net.blanu.sneakermesh;

import java.io.File;

public class LaptopSneakermesh extends Sneakermesh {
	public LaptopSneakermesh() {
		super(new File(new File(new File(System.getProperty("user.home")), "sneakermesh"), "sneakermesh"));
		
		if(!root.exists())
		{
			root.mkdir();
			loadHashes();
		}
	}

	public void log(String s)
	{
		System.out.println(s);
	}
}
