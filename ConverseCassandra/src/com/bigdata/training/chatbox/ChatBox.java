package com.bigdata.training.chatbox;

import javax.swing.JFrame;
/**
 * enclosing JFRame class
 * @author ac2211
 *
 */
public class ChatBox extends JFrame{

	private static final long serialVersionUID = -1346468442356414573L;

	public ChatBox() {
	       setTitle("Musings of Cassandra");

	       setLocationRelativeTo(null);
	       setDefaultCloseOperation(EXIT_ON_CLOSE);
	       setVisible(true);	       

	        getContentPane().add(new ChatPanel("jane","maria"));
	        pack();
	        setVisible(true);
	    }	
}