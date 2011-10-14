package net.blanu.sneakermesh;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ListMessages {
	public static void main(String[] args) throws IOException {		
		Sneakermesh mesh=new LaptopSneakermesh();
		
		List<String> msgs=mesh.getMessages();
		for(String msg : msgs)
		{
			System.out.println(msg);
		}
	}
}
