package net.blanu.sneakermesh;

import java.io.IOException;
import java.util.Scanner;

import net.blanu.sneakermesh.content.TextMessage;

public class AddMessage {
	public static void main(String[] args) throws IOException {		
		System.out.print("Enter message: ");
		Scanner scanner=new Scanner(System.in);
		String msg=scanner.nextLine();
		
		Sneakermesh mesh=new LaptopSneakermesh();
		mesh.addMessage(new TextMessage(msg));
	}
}
