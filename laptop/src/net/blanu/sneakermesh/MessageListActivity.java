package net.blanu.sneakermesh;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;

import java.util.Collections;
import java.util.List;
 
/* ListDemo.java requires no other files. */
public class MessageListActivity extends SneakermeshListActivity
{
    private static final String sendString = "Send";
    private JTextField msgField;
 
    public MessageListActivity(LANProbeService lps)
    {
        super(lps);
  
        JButton sendButton = new JButton(sendString);
        HireListener sendListener = new HireListener(sendButton);
        sendButton.setActionCommand(sendString);
        sendButton.addActionListener(sendListener);
        sendButton.setEnabled(false);
  
        msgField = new JTextField(10);
        msgField.addActionListener(sendListener);
        msgField.getDocument().addDocumentListener(sendListener);
  
        buttonPane.add(msgField);
        buttonPane.add(sendButton);

        if(listModel.getSize()>0)
        {
        	list.ensureIndexIsVisible(listModel.getSize()-1);        
        }
    }
    
    public void populateList()
    {
    	List<Message> msgs=probe.getMesh().getMessages();
    	Collections.sort(msgs);
    	for(Message msg : msgs)
    	{
    		TextMessage tm=(TextMessage)msg;
    		listModel.addElement(tm.getText());
    	}
    }
    
    public void refreshUI()
    {
    	listModel.clear();
    	populateList();
    }
  
    //This listener is shared by the text field and the hire button.
    class HireListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;
 
        public HireListener(JButton button) {
            this.button = button;
        }
 
        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
            String msg = msgField.getText();
              
            listModel.addElement(msg); 
            
            try {
				probe.getMesh().addMessage(new TextMessage(msg));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
            msgField.requestFocusInWindow();
            msgField.setText("");
 
            list.ensureIndexIsVisible(listModel.getSize()-1);
        }
  
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }
 
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }
 
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }
 
        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }
 
        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }  
}