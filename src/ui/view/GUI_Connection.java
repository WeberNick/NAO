package ui.view;

import application.control.Controller;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * The purpose of this class is to create a frame in which
 * the user has the possibility to enter an IP address and 
 * a port number to connect with the robot NAO.
 * <p>
 * It is the first interaction between the user and the user interface
 * after the program started.
 * 
 * @author Nick Weber
 * @version 2.0
 */
public class GUI_Connection extends JFrame
{
	/**
	 * The <Code>Container</Code> on which all the components of the frame are added
	 * @see Container
	 * @see #add(Component)
	 */
	private Container pane;
	/**
	 * This <Code>JTextField</Code> will contain the IP address entered by the user
	 * @see #createIPPanel(JPanel)
	 * @see ConnectionListener
	 */
	private JTextField ip_address;
	/**
	 * This <Code>JTextField</Code> will contain the port number entered by the user
	 * @see #createPortPanel(JPanel)
	 * @see ConnectionListener
	 */
	private JTextField port_number;
	/**
	 * The <Code>Controller</Code> which created this <Code>GUI_Connection</Code> object
	 * @see Controller
	 * @see ConnectionListener
	 */
	private Controller controller;
	private static final long serialVersionUID = 1l;
	
	/**
	 * Constructs a new <Code>GUI_Connection</Code> (-object)-Frame
	 * @param controller the <Code>Controller</Code> which called the constructor
	 * @see Controller
	 */
	public GUI_Connection(Controller controller)
	{
		try 
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
		this.controller = controller;
		this.setTitle("Start Connection");
		this.setSize(getMinimumSize());
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createComponents(this);
		this.setLocation(400,300);
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * This method calls the other 'create'-methods below and by that
	 * build up the frame's parts
	 * @see #createInformationPanel(JPanel)
	 * @see #createIPPanel(JPanel)
	 * @see #createPortPanel(JPanel)
	 * @see #createConnectPanel(JPanel)
	 */
	private void createComponents(JFrame frame)
	{
		pane = getContentPane();
		JPanel panel = new JPanel(new GridBagLayout());
		createInformationPanel(panel);
		createIPPanel(panel);
		createPortPanel(panel);
		createConnectPanel(panel,frame);
		pane.add(panel);
	}
	
	/**
	 * Creates the information panel on the top of the frame
	 * which informs the user what he has to do in this frame
	 * 
	 * @param c the main panel on which this panel will be added
	 */
	private void createInformationPanel(JPanel c)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.insets = new Insets(10,10,0,10);
		
		JLabel info = new JLabel("<html>"+"To connect with NAO please enter the"+"<br>"+ "required IP address and port number."+"</html>");
		c.add(info,gbc);
	}
	
	/**
	 * Creates the IP address panel where the user enters the IP address
	 * 
	 * @param c the main panel on which this panel will be added
	 */
	private void createIPPanel(JPanel c)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.insets = new Insets(10,10,10,10);
		
	    JLabel ip = new JLabel("IP:");
	   
	    c.add(ip, gbc);
		
	    
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
	    gbc.gridy = 1;
	    gbc.insets = new Insets(10,10,10,10);
	    gbc.anchor = GridBagConstraints.WEST;
	    
	    ip_address = new JTextField(15);
	    ip_address.setToolTipText("IP address - for example 127.0.0.1");
	    
	    c.add(ip_address,gbc);
	}
	
	/**
	 * Creates the port number panel where the user enters the port number
	 * 
	 * @param c the main panel on which this panel will be added
	 */
	private void createPortPanel(JPanel c)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.insets = new Insets(0,10,10,10);
		
	    JLabel port = new JLabel("Port:");
	    
	    c.add(port, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
	    gbc.gridy = 2;
	    gbc.insets = new Insets(0,10,10,10);
	    gbc.anchor = GridBagConstraints.WEST;
	    
	    port_number = new JTextField(4);
	    port_number.setToolTipText("Port Number - for example 9559");
	    
	    c.add(port_number,gbc);
	}
	
	/**
	 * Creates the connect panel where the user can press the 'Connect'-button
	 * to start an <Code>ActionEvent</Code> resulting in an attempt to establish 
	 * a connection to NAO
	 * 
	 * @param c the main panel on which this panel will be added
	 * @see ConnectionListener
	 */
	private void createConnectPanel(JPanel c, JFrame frame)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		
	    JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton connect = new JButton("Connect");
		JButton cancel = new JButton("Cancel");
		frame.getRootPane().setDefaultButton(connect);
		ConnectionListener cL = new ConnectionListener(this, this.controller);
		connect.addActionListener(cL);
		cancel.addActionListener(cL);
		buttonPanel.add(connect);
		buttonPanel.add(cancel);
		c.add(buttonPanel, gbc);
	}
	
	/**
	 * In this class the <Code>ActionListener</Code> used for the 'Connect'-button is implemented.
	 * @author Nick Weber
	 * @version 2.0
	 */
	class ConnectionListener implements ActionListener
	{
		/**
		 * The object in which this <Code>ActionListener</Code> was initialized
		 * @see GUI_Connection
		 */
		private GUI_Connection connection_Frame;
		/**
		 * The <Code>Controller</Code> of the <Code>GUI_Connection</Code> class
		 * @see Controller
		 * @see GUI_Connection
		 */
		private Controller controller;
		
		/**
		 * Constructs a new <Code>ConnectionListener</Code> object and its attributes are initialized
		 * with the <Code>GUI_Connection</Code> object which called the <Code>ActionListener</Code> and aswell
		 * the <Code>Controller</Code>
		 * 
		 * @param frame the <Code>GUI_Connection</Code> object which called the <Code>ActionListener</Code>
		 * @param controller the <Code>Controller</Code> which created the <Code>GUI_Connection</Code>
		 * @see Controller
		 * @see GUI_Connection
		 */
		ConnectionListener(GUI_Connection frame, Controller controller)
		{
			connection_Frame = frame;
			this.controller = controller;
		}
		
		/**
		 * This method is called if the user entered an invalid IP address, whereby
		 * a <Code>JOptionPane</Code> pops up and informs about the invalid IP address.
		 * @see JOptionPane
		 */
		private void errorIP()
		{
			JOptionPane.showMessageDialog(connection_Frame,"Entered IP address is not a valid IPv4 address.", "Invalid IP Address", JOptionPane.ERROR_MESSAGE);
		}
		
		/**
		 * This method is called if the user entered an invalid port number, whereby
		 * a <Code>JOptionPane</Code> pops up and informs about the invalid port number.
		 * <p>
		 * A <Code>JOptionPane</Code> pops up and informs about the error.
		 * @see JOptionPane
		 */
		private void errorPort()
		{
			JOptionPane.showMessageDialog(connection_Frame,"Entered port number is not a valid port number.", "Invalid Port Number", JOptionPane.ERROR_MESSAGE);
		}
		
		/**
		 * This method is called if the connection with the entered IP address and port number
		 * could not be established.
		 * <p>
		 * A <Code>JOptionPane</Code> pops up and informs about the error.
		 * @see JOptionPane
		 */
		private void errorConnect()
		{
			JOptionPane.showMessageDialog(connection_Frame,"<html>A connection to the entered IP address and port number could not be established."+"<br>"+"Please check the entered IP address and port number.</html>", "Connection Error", JOptionPane.ERROR_MESSAGE);
		}
		
		/**
		 * This method is called if the 'Connect'-/ or 'Cancel'-Button is pressed.
		 * <p>
		 * If the pressed button is the 'Connect'-Button, then the method saves 
		 * the text entered in the <Code>JTextField</Code> and then checks the text for a valid 
		 * IP address and port number input.<br>
		 * If the entered text is a valid IP address and port number, a new thread object of
		 * the <Code>GUI_WindowChanger</Code> class becomes started.<br>
		 * Otherwise if the pressed button is the 'Cancel'-Button the program will be
		 * shutdown.
		 * @param e the <Code>ActionEvent</Code> generated if one of the buttons in the <Code>GUI_Connection</Code> class are pressed
		 * @see GUI_WindowChanger
		 * @see GUI_Connection
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton pressed = (JButton) e.getSource();
			String buttonText = pressed.getText();
			if(buttonText.equals("Connect"))
			{
				String ip = ip_address.getText();
				String port = port_number.getText();
				boolean port_correct = true;
				if(port.length() != 4)
				{
					port_correct = false;
				}
				else
				{
					try
					{
						Integer.parseInt(port);
					}
					catch(NumberFormatException ex)
					{
						port_correct = false;
					}
				}
				
				boolean validIP = InetAddressValidator.getInstance().isValidInet4Address(ip);
				
				if(validIP && port_correct)
				{
					pressed.setEnabled(false);
					controller.startConnection(ip, port);
					if(controller.getConnection())
					{
						connection_Frame.dispose();
						controller.initiateAll();
						new GUI(ip, port, controller);
					}
					else
					{
						pressed.setEnabled(true);
						errorConnect();
					}
				}
				else
				{
					if(!validIP && !port_correct)
					{
						errorConnect();
					}
					else if(!validIP)
					{
						errorIP();
					}
					else
					{
						errorPort();
					}
				}	
			}
			else
			{
				System.exit(0);
			}
		}
	}
}
