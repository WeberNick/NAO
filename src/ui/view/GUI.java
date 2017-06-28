package ui.view;

import application.control.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class creates the main frame with all the informations about the game status, the camera feed and the buttons
 * to terminate and pause the current session.
 * <p>
 * This class also creates the <Code>GUI_Handler</Code> which handles all the information flow between the user interface
 * and the <Code>Controller</Code>.
 * 
 * @author Nick Weber
 * @version 2.0
 * @see GUI_Components
 * @see GUI_GameStatus
 * @see GUI_Camera
 * @see GUI_Playboard
 * @see GUI_Handler
 * @see Controller
 * @see JFrame
 */
public class GUI extends JFrame
{
	/**
	 * The <Code>Container</Code> on which all the components of the frame are added
	 * @see Container
	 * @see #add(Component)
	 */
	private Container pane;
	/**
	 * In this object of <Code>GUI_Components</Code> all the relevant <Code>JPanel</Code>
	 * are created which contain everything seen on the user interface, such as the camera stream,
	 * informations about the game status or the visual play board representation.
	 * @see GUI_Components
	 * @see JPanel
	 */
	private GUI_Components components;
	/**
	 * In this <Code>boolean</Code> attribute the own color selected in the pop up panel will be saved.
	 * <p>
	 * true if the own color is blue
	 * false if the own color is red
	 */
	private boolean ownColor;
	private static final long serialVersionUID = 1l;
	
	/**
	 * Constructs the main frame of the user interface
	 * @param ip the IP address of the established connection
	 * @param port the port number of the established connection
	 * @param controller the <Code>Controller</Code> needed for the communication between the classes
	 * @see Controller
	 */
	GUI(String ip, String port,Controller controller)
	{
		try 
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
		
		GUI_Handler gui_handler = new GUI_Handler(this, controller);
		gui_handler.getController().setGUI_Handler(gui_handler);
		
		this.setTitle("NAO Remote Control");
		this.setSize(710,700);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter()
		{
			/**
			 * The possible options in the <Code>JOptionPane</Code> if asked for confirmation to close the frame
			 */
	        String[] options = {"     Yes     ", "     No     "};
			
	        /**
	         * A <Code>JOptionPane</Code> pops up as the user tries to close the user interface, 
	         * asking for the confirmation.
	         */
	        @Override
			public void windowClosing(WindowEvent e) 
			{
	        	JFrame frame = (JFrame)e.getSource();
				int confirm = JOptionPane.showOptionDialog(frame,"Do you really want to close this application?", "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (confirm == 0) 
				{
					gui_handler.terminate();
				}
			}
	    });
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JLabel jlbl = new JLabel("<html>To continue please choose the color "+"<br>"+"of your gaming pieces.</html>");
		String[] options = {"   Blue   ","   Red   ", " Cancel "};
		ImageIcon icon = new ImageIcon(getClass().getResource("/check.png"));
		switch(JOptionPane.showOptionDialog(this, jlbl,
			    "Connection Successful", JOptionPane.YES_OPTION,
			    JOptionPane.QUESTION_MESSAGE, icon, options, null))
		{
			case 0: this.ownColor = true;
				break;
			case 1: this.ownColor = false;
				break;
			case 2: gui_handler.terminate();
				break;
			case -1: gui_handler.terminate();
				break;
		}
		gui_handler.createAIPerformance();
		pane = getContentPane();
		pane.setBackground(Color.WHITE);
		pane.setLayout(new BorderLayout(2,2));
		components = new GUI_Components(this,pane,ip,port,this.ownColor, gui_handler);
		this.setVisible(true);
	}
	
	/**
	 * @return the <Code>GUI_Components</Code> object the main frame is made of
	 * @see GUI_Components
	 */
	GUI_Components getGUI_Components()
	{
		return this.components;
	}
	
	/**
	 * @return a boolean representation of the own color
	 */
	boolean getOwnColor()
	{
		return this.ownColor;
	}
}