package net.blanu.sneakermesh;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;

import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

public class GUI implements Runnable, ActionListener
{
	protected JFrame frame=null;
	protected LANProbeService probe=null;
	MessageListActivity msgList=null;
	
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new GUI());
    }
    
    public GUI()
    {
    	probe=new LANProbeService();    	
    }
    
    public void run()
    {
    	createAndShowGUI();
    }
    
    protected void createAndShowGUI()
    {    	
        //Create and set up the window.
        frame = new JFrame("Sneakermesh");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createMenu();
 
        //Create and set up the content pane.
        msgList = new MessageListActivity(probe);
        msgList.setOpaque(true); //content panes must be opaque
        frame.setContentPane(msgList);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }    
    
    protected void createMenu()
    {
    	JMenuBar menuBar;
    	JMenu menu, submenu;
    	JMenuItem menuItem;    	
    	
    	menuBar = new JMenuBar();
    	menu=new JMenu("File");
    	menuBar.add(menu);
    	
    	menuItem = new JMenuItem("Destroy", KeyEvent.VK_D);
    	menuItem.addActionListener(this);    	
    	menu.add(menuItem);    	
    	
    	frame.setJMenuBar(menuBar);
    }

	@Override
	public void actionPerformed(ActionEvent event)
	{
		System.out.println("Action performed: "+event);
		JMenuItem source = (JMenuItem)(event.getSource());
		String name=source.getText();
		if(name=="Destroy")
		{
			probe.getMesh().deleteMessages();
			msgList.refreshUI();
		}
		else
		{
			System.out.println("Unknown menu item: "+name);
		}
	}
}