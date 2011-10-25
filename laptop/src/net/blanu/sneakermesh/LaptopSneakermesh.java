package net.blanu.sneakermesh;

import java.io.File;

public class LaptopSneakermesh extends Sneakermesh implements Logger
{
	static protected File getRoot(String s)
	{
		File parent=null;
		
		if(s==null)
		{
			File home=new File(System.getProperty("user.home"));
			parent=new File(home, "sneakermesh");
		}
		else
		{
			parent=new File(s);			
		}
		
		return new File(parent, "sneakermesh");
	}
	
	public LaptopSneakermesh() {
		this(null);
	}

	public LaptopSneakermesh(String s) {
		super(getRoot(s));
	}	
	
	public void log(String s)
	{
		System.out.println(s);
	}
	
	public void fireHaveChangeEvent(String digest)
	{
		System.out.println("New message!!!!!! "+digest);
	}
}
