package net.blanu.sneakermesh;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
 
/* ListDemo.java requires no other files. */
abstract public class SneakermeshListActivity extends JPanel implements ListSelectionListener
{
	private static final long serialVersionUID = 3914401438783878062L;
	
	protected JList list;
    protected DefaultListModel listModel;
    protected JPanel buttonPane;
    LANProbeService probe=null;
  
    public SneakermeshListActivity(LANProbeService lps)
    {
        super(new BorderLayout());

        probe=lps;        
 
        listModel = new DefaultListModel();
        
        populateList();
        
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
    
        //Create a panel that uses BoxLayout.
        buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
        
    public void valueChanged(ListSelectionEvent e) {
    }
    
    abstract protected void populateList();    
    abstract protected void refreshUI();
}