package ui.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;

/**
 * The purpose of this class is to create all swing components needed for the <Code>GUI</Code> and also 
 * update the <Code>GUI</Code> via method calls of the other classes if informations are received from the 
 * <Code>Controller</Code> or <Code>ActionLister</Code>.
 * 
 * @author Nick Weber
 * @version 2.0
 * @see GUI
 * @see application.control.Controller
 * @see GameTimeListener
 * @see MoveTimeListener
 * @see ButtonListener
 * @see MenuListener
 */
public class GUI_Components
{
	/**
	 * The main frame
	 * @see GUI
	 */
	private GUI gui;
	/**
	 * A <Code>JPanel</Code> which handles the video stream to the frame
	 * @see GUI_Camera
	 * @see JPanel
	 */
	private GUI_Camera cam;	
	/**
	 * A <Code>JLabel</Code> which represents the information about the current connection
	 * @see JLabel
	 */
	private JLabel connection_label;
	/**
	 * A <Code>JPanel</Code> which represents the current state of the play board
	 * @see GUI_Playboard
	 * @see JPanel
	 */
	private GUI_Playboard gui_playboard;
	/**
	 * A <Code>JPanel</Code> which provides information about the current score of NAO
	 * @see GUI_GameStatus
	 * @see JPanel
	 */
	private GUI_GameStatus nao;
	/**
	 * A <Code>JPanel</Code> which provides information about the current score of the enemy.
	 * @see GUI_GameStatus
	 * @see JPanel
	 */
	private GUI_GameStatus opponent;
	/**
	 * A <Code>JPanel</Code> which shows the passed time since the current move started
	 * @see GUI_TimeCounter
	 * @see JPanel
	 */
	private GUI_TimeCounter moveCounter;
	/**
	 * A <Code>JPanel</Code> which shows the passed time since the game started
	 * @see GUI_TimeCounter
	 * @see JPanel
	 */
	private GUI_TimeCounter gameCounter;
	/**
	 * Handles the information flow between the user interface and the <Code>Controller</Code>.
	 * @see GUI_Handler
	 * @see application.control.Controller
	 */
	private GUI_Handler gui_handler;
	/**
	 * A <Code>JDialog</Code> which shows the underlying rules of Nine Men's Morris
	 * @see JDialog
	 */
	private JDialog rules_dialog;
	/**
	 * A <Code>JDialog</Code> which shows the system output stream
	 * @see JDialog
	 */
	private JDialog console_dialog;
	/**
	 * A <Code>JDialog</Code> which shows information about the team behind the project
	 * @see JDialog
	 */
	private JDialog about_dialog;
	/**
	 * A <Code>JTextArea</Code> in which the system's output stream is written
	 * @see JTextArea
	 */
	private JTextArea textArea;	
	/**
	 * A <Code>boolean</Code> representing if the current move is executed by us (true) or the enemy (false)
	 */
	private boolean currentExecute;
	/**
	 * The <Code>JButton</Code> which starts the next move when clicked.
	 * @see JButton
	 * @see GUI_Handler#nextMove()
	 */
	private JButton next;
	/**
	 * Indicates if the current game shall be paused (true) or continued (false);
	 */
	private boolean pause;
	/**
	 * Help attribute to know if the <Code>JOptionPane</Code> in the visual play board read in needs to be shown or not.<br>
	 * true if the dialog needs to be shown, false otherwise
	 * @see GUI_ReadIn
	 */
	private boolean showMessage = true;
	/**
	 * 
	 */
	private JLabel performance;
	

	/**
	 * Constructs all the needed <Code>JPanel</Code> to be shown on the <Code>GUI</Code>
	 * @param gui the main frame
	 * @param pane the <Code>Container</Code> on which all the components of the frame are added
	 * @param ip the IP address of the established connection
	 * @param port the port number of the established connection
	 * @param ownColor the <Code>boolean</Code> representation of the own color (true = blue, false = red)
	 * @param handler the object that handles the information flow between the user interface and the <Code>Controller</Code>.
	 * @see GUI
	 * @see JPanel
	 * @see Container
	 * @see GUI_Handler
	 * @see application.control.Controller
	 */
	GUI_Components(GUI gui, Container pane, String ip, String port, boolean ownColor, GUI_Handler handler)
	{
		this.gui = gui;
		connection_label = new JLabel("<html><b>IP Address:</b> " + ip + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Port Number:</b> " + port + "</html>");
		GameTimeListener gtl = new GameTimeListener();
		MoveTimeListener mtl = new MoveTimeListener();
		gameCounter = new GUI_TimeCounter("Game",1000, gtl);
		moveCounter = new GUI_TimeCounter("Move",1000, mtl);
		cam = new GUI_Camera();
		nao = new GUI_GameStatus("NAO",ownColor);
		opponent = new GUI_GameStatus("Opponent",!ownColor);
		gui_playboard = new GUI_Playboard();
		this.gui_handler = handler;
		currentExecute = false;
		pause = false;
		this.createComponents(gui,pane,ownColor);
	}
	
	/**
	 * This method creates the components of the northern part for the main frame's <Code>BorderLayout</Code>. 
	 * <p>
	 * The resulting panel is divided into two parts, a camera panel and an information
	 * panel which consists of a connection information label, two time counters and
	 * three buttons to change the camera, terminate or interrupt the program
	 * 
	 * @param pane the content pane of the main frame on which to add the components
	 * @see BorderLayout
	 * @see GUI_Camera
	 * @see GUI_TimeCounter
	 */
	private void createNorth(Container pane, boolean ownColor)
	{
		JPanel north_panel = new JPanel(new GridBagLayout());
		north_panel.setPreferredSize(new Dimension(710, 100));
		JPanel information_panel = new JPanel(new GridLayout(3,1,0,0));
		JPanel counter_panel = new JPanel(new GridLayout(1,2,5,5));
		JPanel button_panel = new JPanel (new GridLayout(1,4,5,5));	
		north_panel.setBackground(Color.WHITE);
		information_panel.setBackground(Color.WHITE);
		counter_panel.setBackground(Color.WHITE);
		button_panel.setBackground(Color.WHITE);
		
		JButton changeCamera = new JButton("Start Camera");
		changeCamera.setFont(new Font("Verdana", Font.PLAIN,10));
		if(ownColor)
		{
			next = new JButton("Next Move");
		}
		else
		{
			next = new JButton("Analyze");
		}
		JButton terminate = new JButton("Terminate");
		JButton interrupt = new JButton("Pause");
		
		ButtonListener bL = new ButtonListener();
		
		changeCamera.addActionListener(bL);
		next.addActionListener(bL);
		terminate.addActionListener(bL);
		interrupt.addActionListener(bL);
		
		counter_panel.add(gameCounter);
		counter_panel.add(moveCounter);
		
		button_panel.add(changeCamera);
		button_panel.add(next);
		button_panel.add(interrupt);
		button_panel.add(terminate);
		
		information_panel.setPreferredSize(new Dimension(500,100));
		information_panel.add(connection_label);
		information_panel.add(counter_panel);
		information_panel.add(button_panel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
	    gbc.gridy = 0;

	    gbc.anchor = GridBagConstraints.WEST;
	    
	    north_panel.setPreferredSize(new Dimension(700,100));
	    
		north_panel.add(cam,gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0,10,0,0);
		
		north_panel.add(information_panel,gbc);
		
		pane.add(north_panel, BorderLayout.NORTH);
	}
	
	/**
	 * This method creates the components of the eastern and western part for the main frame's <Code>BorderLayout</Code>. 
	 * <p>
	 * The components present the current game status of both NAO and its enemy.<br>
	 * The generated instance of the <Code>GUI_GameStatus</Code> class provides further methods
	 * to update the main frame with informations.
	 * 
	 * @param pane the content pane of the main frame on which to add the components
	 * @see BorderLayout
	 * @see GUI_GameStatus
	 */
	private void createEastAndWest(Container pane)
	{
		pane.add(opponent, BorderLayout.EAST);
		pane.add(nao, BorderLayout.WEST);
	}
	
	/**
	 * This method creates the components of the central part of the main frame's <Code>BorderLayout</Code>. 
	 * <p>
	 * The central component consists of an image of the Nine Men's Morris board and the current placement of the gaming pieces.
	 * 
	 * @param pane the content pane of the main frame on which to add the components
	 * @see BorderLayout
	 * @see ui.view.GUI_Playboard
	 */
	private void createCenter(Container pane)
	{
		pane.add(gui_playboard, BorderLayout.CENTER);
		gui_playboard.drawing();
	}
	
	/**
	 * Fills the south side of the <Code>BorderLayout</Code> with some images for decoration.
	 * @param pane the content pane of the main frame on which to add the components
	 */
	private void createSouth(Container pane)
	{

		JPanel filler = new JPanel();
		filler.setLayout(null);
		filler.setPreferredSize(new Dimension(710,85));
		filler.setBackground(Color.WHITE);
		
		JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/UniMA_logo.png")));
		JLabel schrift = new JLabel(new ImageIcon(getClass().getResource("/UniMA_schrift.png")));
		JLabel logo2 = new JLabel(new ImageIcon(getClass().getResource("/UniMA_logo.png")));
		logo.setBackground(Color.WHITE);
		logo.setOpaque(true);
		schrift.setBackground(Color.WHITE);
		schrift.setOpaque(true);
		logo2.setBackground(Color.WHITE);
		logo2.setOpaque(true);
		
		logo.setBounds(12,0,85,85);
		schrift.setBounds(155,5,400,75);
		logo2.setBounds(610, 0, 85, 85);
		
		filler.add(logo);
		filler.add(schrift);
		filler.add(logo2);
		
		pane.add(filler,BorderLayout.SOUTH);
	}
	
	/**
	 * This method creates the menu bar of the frame
	 * @param gui the main frame
	 * @see JMenuBar
	 * @see GUI
	 */
	private void createMenuBar(GUI gui)
	{
		JMenuBar menuBar = new JMenuBar();
		
		JMenu info = new JMenu("Info");
		JMenuItem game_rules = new JMenuItem("Game Rules");
		game_rules.addActionListener(new MenuListener(gui));
		JMenuItem console_output = new JMenuItem("Show Console Output");
		console_output.addActionListener(new MenuListener(gui));
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new MenuListener(gui));
		
		info.add(game_rules);
		info.add(console_output);
		info.add(about);
		
		JMenu read = new JMenu("Read In");
		JMenuItem vri = new JMenuItem("Visual Read In");
		vri.addActionListener(new MenuListener(gui));
		
		read.add(vri);
		
		menuBar.add(info);
		menuBar.add(read);
		gui.setJMenuBar(menuBar);
	}
	
	/**
	 * This method is only for structure and overview.
	 * The only purpose is to call the other create-methods
	 * @param gui the main frame
	 * @param pane the content pane of the main frame on which to add the components
	 * @see #createNorth(Container)
	 * @see #createEastAndWest(Container)
	 * @see #createSouth(Container)
	 * @see #createMenuBar(GUI)
	 */
	void createComponents(GUI gui,Container pane, boolean ownColor)
	{
		createNorth(pane,ownColor);
		createEastAndWest(pane);
		createCenter(pane);
		createSouth(pane);
		createMenuBar(gui);
	}
	
	/**
	 * @param text the <Code>String</Code> to set on the performance label
	 * @see GUI_Components#performance
	 */
	void setPerformanceLabel(String text)
	{
		this.performance.setText(text);
	}
	
	/**
	 * @return if the current game is paused (true) or not (false)
	 * @see GUI_Components#pause
	 */
	boolean getPause()
	{
		return this.pause;
	}
	
	/**
	 * @return the <Code>JPanel</Code> object which handles the information of NAO's game status
	 * @see GUI_GameStatus
	 */
	GUI_GameStatus getNao() {
		return nao;
	}

	/**
	 * @return the <Code>JPanel</Code> object which handles the information of the enemy's game status
	 * @see GUI_GameStatus
	 */
	GUI_GameStatus getOpponent() {
		return opponent;
	}
	
	/**
	 * @return the <Code>JPanel</Code> object which visualize the play board on the frame
	 * @see GUI_Playboard
	 */
	GUI_Playboard getPlayboard()
	{
		return gui_playboard;
	}
	
	/**
	 * @return the <Code>JPanel</Code> object which handles the visualization of NAO's view
	 * @see GUI_Camera
	 */
	GUI_Camera getCamera()
	{
		return cam;
	}
	
	/**
	 * @return the timer responsible for the move time count
	 */
	GUI_TimeCounter getMoveCounter()
	{
		return this.moveCounter;
	}
	
	/**
	 * @return the timer responsible for the game time count
	 */
	GUI_TimeCounter getGameCounter()
	{
		return this.gameCounter;
	}
	
	/**
	 * Sets a <Code>boolean</Code> representing if NAO is currently executing a task.
	 * @param current true if NAO is currently executing a move, false otherwise
	 * @see #currentExecute
	 */
	void setCurrentExecute(boolean current)
	{
		this.currentExecute = current;
	}
	
	/**
	 * Enables or disables the <Code>JButton</Code> for the next move
	 * @param clickable true if the button shall be clickable, false otherwise
	 * @see GUI_Components#next
	 * @see JButton
	 */
	void setNextButtonSetClickable(boolean clickable)
	{
		this.next.setEnabled(clickable);
	}
	
	/**
	 * Changes the text of the <Code>JButton</Code> for the next move.
	 * @see GUI_Components#next
	 * @see JButton
	 */
	void changeNextButtonText(String text)
	{
		next.setText(text);
	}
	
	/**
	 * After the play board was analyzed the user has to confirm that the analysis was correct.
	 * @param currentBoard the analyzed play board representation
	 * @param pile the representation of the number of men on the specific pile
	 * @return true if the analysis was correct, false otherwise
	 * @see application.control.Controller#startAnalysis()
	 * @see ConfirmDialog#confirm()
	 */
	boolean confirm(Boolean[] currentBoard, int[] pile)
	{
		return new ConfirmDialog(gui, currentBoard, pile).confirm();
	}
	
	/**
	 * This Method creates a new <Code>JDialog</Code> containing the gaming rules.
	 * @param gui the <Code>JDialog</Code> is located relative to this frame
	 * @see JDialog
	 * @see GUI
	 */
	private void showRulesDialog(GUI gui)
	{
		if (rules_dialog == null) 
		{
			rules_dialog = new JDialog();
			rules_dialog.setTitle("Game Rules");
			rules_dialog.setLocationRelativeTo(gui);
			rules_dialog.setLayout(new FlowLayout());
			rules_dialog.setBackground(Color.WHITE);
			rules_dialog.getContentPane().setBackground(Color.WHITE);
			rules_dialog.setSize(600, 320);
			rules_dialog.setResizable(false);
			JLabel rules_text = new JLabel();
			rules_text.setBackground(Color.WHITE);
			rules_text.setOpaque(true);
			rules_text.setText("<html><b>\"Nine Men's Morris\" - Gaming rules:</b><br><br>"
								+"The board consists of a grid with twenty-four<br>"
								+ "intersections or points. Each player has nine<br>"
								+ "pieces, or \"men\", usually colored black and white.<br>"
								+ "Players try to form 'mills' - three of their own men<br>"
								+ "lined horizontally or vertically - allowing a player<br>"
								+ "to remove an opponent's man from the game. A<br>"
								+ "player wins by reducing the opponent to two<br>"
								+ "pieces (where he could no longer form mills and<br>"
								+ "thus be unable to win), or by leaving him without<br>"
								+ "a legal move.<br>"
								+ "<br>"
								+ "<i>The game proceeds in three phases:</i><br>"
								+ "<br>"
								+ "1. Placing men on vacant points<br>"
								+ "2. Moving men to adjacent points<br>"
								+ "3. (Optional phase) Moving men to any vacant point<br>"
								+ "when a player has been reduced to three men</html>");
			JLabel rules_icon = new JLabel(new ImageIcon(getClass().getResource("/rules_board_icon.png")));
			rules_icon.setBackground(Color.WHITE);
			rules_icon.setOpaque(true);
			rules_dialog.add(rules_text);
			rules_dialog.add(rules_icon);
			rules_dialog.pack();
			System.out.println();
		}
		rules_dialog.setVisible(true);
	}
	
	/**
	 * This Method creates a new <Code>JDialog</Code> containing the system's output stream in a <Code>JTextArea</Code>.
	 * @param gui the <Code>JDialog</Code> is located relative to this frame
	 * @see JDialog
	 * @see GUI
	 */
	private void showConsoleDialog(GUI gui)
	{
		if(console_dialog == null)
		{
			textArea = new JTextArea(50, 10);
	        textArea.setEditable(false);
	        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
	        System.setOut(printStream);
	        System.setErr(printStream);
			console_dialog = new JDialog(gui, "Console Output",false);
			console_dialog.setLocationRelativeTo(gui);
			console_dialog.setLayout(new GridBagLayout());
	        GridBagConstraints constraints = new GridBagConstraints();
	        constraints.gridx = 0;
	        constraints.gridy = 0;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.WEST;
	        JButton buttonClear = new JButton("Clear");
	        console_dialog.add(buttonClear, constraints);
	        
	        constraints.insets = new Insets(0,0,0,0);
	        constraints.gridx = 1;
	        constraints.fill = GridBagConstraints.BOTH;
	        
	        performance = new JLabel("<html>Memory Performance: 0,00 mb / 0,00mb | 00,00 %"
	        		+ "<br>"
	        		+ "CPU Performance: 1. CPU states: 0.0% | 2. CPU states: 0.0% "
	        		+ "<br>"
	        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	        		+ "3. CPU states: 0.0%  | 4. CPU states: 0.0% "
	        		+ "</html>");
	        console_dialog.add(performance, constraints);
	   
	        constraints.gridx = 0;
	        constraints.gridy = 1;
	        constraints.gridwidth = 2;
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1.0;
	        constraints.weighty = 1.0;
	        console_dialog.add(new JScrollPane(textArea), constraints);
	        buttonClear.addActionListener(new ActionListener() 
	        {
	            @Override
	            public void actionPerformed(ActionEvent evt) 
	            {
	                try 
	                {
	                    textArea.getDocument().remove(0,textArea.getDocument().getLength());
	                }
	                catch (BadLocationException ex) 
	                {
	                    ex.printStackTrace();
	                }
	            }
	        });
			console_dialog.setSize(560, 320);
		}
		console_dialog.setVisible(true);
	}
		
	/**
	 * This Method creates a new <Code>JDialog</Code> containing informations about the team behind the project in a <Code>JTextArea</Code>.
	 * @param gui the <Code>JDialog</Code> is located relative to this frame
	 * @see JDialog
	 * @see GUI
	 */
	private void showAboutDialog(GUI gui)
	{
		if (about_dialog == null) 
		{
			about_dialog = new JDialog();
			about_dialog.setTitle("About the Team");
			about_dialog.setLocationRelativeTo(gui);
			about_dialog.setLayout(null);
			about_dialog.setBackground(Color.WHITE);
			about_dialog.getContentPane().setBackground(Color.WHITE);
			about_dialog.setSize(460, 260);
			about_dialog.setResizable(false);
			JLabel about_text_head = new JLabel();
			about_text_head.setSize(460, 80);
			about_text_head.setText("<html><font size=\"6\"><center>Praktikum Software Engineering</center></font><br>"
								+"<font size=\"5\"><center><i>NAO Project 2015 - Group 3</i></center></font></html>");
			JLabel about_text_projectTeam = new JLabel("<html><font size=\"4\">Project Team:</font></html>");
			JLabel about_text_member = new JLabel();
			about_text_member.setText("<html>"
								+ "<b>-Nick Weber:</b><br>"
								+ "<b>-Wei Hao Lu:</b><br>"
								+ "<b>-Aljoscha Narr:</b><br>"
								+ "<b>-Mike Siefert:</b><br>"
								+ "<b>-Julian Betz:</b><br>"
								+ "<b>-Jonas Thietke:</b><br>"
								+ "</html>");
			JLabel about_text_tasks = new JLabel();
			about_text_tasks.setText("<html>"
					+ "User Interface & Class Communication<br>"
					+ "Movement<br>"
					+ "Events & Detection<br>"
					+ "Exception Handling & Testing<br>"
					+ "Artificial Intelligence & Data Representation<br>"
					+ "Logics & Data Representation<br>"
					+ "</html>");
			JLabel about_icon = new JLabel(new ImageIcon(getClass().getResource("/GreyNao.png")));
			
			about_text_head.setBounds(8, 0, 450, 80);
			about_text_projectTeam.setBounds(5, 85, 100, 20);
			about_text_member.setBounds(5, 107, 100, 92);
			about_text_tasks.setBounds(110, 107, 250, 92);
			about_icon.setBounds(350, 60, 100, 150);
			
			about_dialog.add(about_text_head);
			about_dialog.add(about_text_projectTeam);
			about_dialog.add(about_text_member);
			about_dialog.add(about_text_tasks);
			about_dialog.add(about_icon);
		}
		about_dialog.setVisible(true);
	}
	
	/**
	 * The class is an <Code>ActionListener</Code> designed to update the elapsed time on the main frame since the game started.
	 * @author Nick Weber
	 * @version 2.0
	 * @see ActionListener
	 * @see GUI_TimeCounter
	 */
	private class GameTimeListener implements ActionListener
	{
		/**
		 * The elapsed seconds.
		 */
		int seconds;
		/**
		 * The elapsed minutes.
		 */
		int minutes;
		
		/**
		 * This method handles the received events send by a <Code>Timer</Code> object
		 * @param e the received <Code>ActionEvent</Code>
		 * @see Timer
		 * @see ActionEvent
		 */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			seconds++;
			if(seconds == 60)
			{
				seconds = 0;
				minutes++;
			}
			gameCounter.setCounter(minutes, seconds);
		}
	}
	
	/**
	 * The class is an <Code>ActionListener</Code> designed to update the elapsed time on the main frame since the current move started.
	 * @author Nick Weber
	 * @version 2.0
	 * @see ActionListener
	 * @see GUI_TimeCounter
	 */
	private class MoveTimeListener implements ActionListener
	{
		/**
		 * The elapsed seconds.
		 */
		int seconds;
		/**
		 * The elapsed minutes.
		 */
		int minutes;
		
		/**
		 * This method handles the received events send by a <Code>Timer</Code> object
		 * @param e the received <Code>ActionEvent</Code>
		 * @see Timer
		 * @see ActionEvent
		 */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			int temp = moveCounter.getSec();
			temp++;
			moveCounter.setSec(temp);
			seconds = moveCounter.getSec();
			if(seconds == 60)
			{
				moveCounter.setSec(0);
				int temp1 = moveCounter.getMin();
				temp1++;
				moveCounter.setMin(temp1);
				minutes = moveCounter.getMin();
			}
			moveCounter.setCounter(minutes, seconds);
		}
	}
	
	/**
	 * The class is an <Code>ActionListener</Code> designed to handle the received events 
	 * triggered by the users input through the buttons on the main frame.
	 * 
	 * @author Nick Weber
	 * @version 2.0
	 * @see ActionListener
	 */
	private class ButtonListener implements ActionListener
	{
		/**
		 * This method handles the received events send by the buttons on the main frame
		 * @param e the received <Code>ActionEvent</Code>
		 * @see ActionEvent
		 */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			switch(((JButton) e.getSource()).getText())
			{
				case "Start Camera":
					gui_handler.startCameraStream();
					((JButton) e.getSource()).setText("Change Camera");
					System.out.println("Camera stream started.");
					break;
				case "Change Camera":
					if(gui_handler.getCamera()==0)
					{
						gui_handler.changeCamera();
						System.out.println("Changed the camera stream to bottom camera.");
					}
					else if(gui_handler.getCamera()==1)
					{
						gui_handler.changeCamera();
						System.out.println("Changed the camera stream to top camera.");
					}
					break;
				case "Analyze":
					System.out.println("Start the analysis of the play board.");
					gui_handler.analyzePlayBoard();
					break;
				case "Next Move":
					System.out.println("Start the next move.");
					gui_handler.nextMove();
					break;
				case "Terminate": 
					String[] options = {"     Yes     ", "     No     "};
					int confirm = JOptionPane.showOptionDialog(gui,"Do you really want to terminate all processes?", "Terminate Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (confirm == 0) 
					{
						gui_handler.terminate();
					}
					break;
				case "Pause": 
					((JButton) e.getSource()).setText("Continue");
					pause = true;
					if(currentExecute)
						moveCounter.getTimer().stop(); 
					gameCounter.getTimer().stop();
					gui_handler.stopMove();
					System.out.println("Game Paused.");
					break;
				case "Continue": 
					((JButton) e.getSource()).setText("Pause");
					pause = false;
					if(currentExecute)
						moveCounter.getTimer().start(); 
					gameCounter.getTimer().start();
					System.out.println("Game Continued.");
					break;
			}
		}
	}
	
	/**
	 * The class is an <Code>ActionListener</Code> designed to handle the received events
	 * triggered by the users input through the menus on the main frame.
	 * @author Nick Weber
	 * @version 2.0
	 * @see ActionListener
	 */
	private class MenuListener implements ActionListener
	{
		/**
		 * The main frame
		 * @see GUI
		 */
		private GUI gui;
		MenuListener(GUI gui)
		{
			this.gui = gui;
		}
		
		/**
		 * This method handles the received events send by the menus on the main frame
		 * @param e the received <Code>ActionEvent</Code>
		 * @see ActionEvent
		 */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			switch(((JMenuItem) e.getSource()).getText())
			{
				case "Game Rules":
					showRulesDialog(gui);
					break;
				case "Show Console Output":
					showConsoleDialog(gui);
					gui_handler.startPerformanceMonitor();
					break;
				case "About":
					showAboutDialog(gui);
					break;
				case "Visual Read In":
					new GUI_ReadIn(gui_handler);
					break;
			}
		}
	}

	/**
	 * This class is the implementation of a custom dialog which is shown after the play board analysis of NAO.
	 * @author Nick Weber
	 * @version 2.0
	 */
	private class ConfirmDialog
	{
		/**
		 * The <Code>JOptionPane</Code> in the dialog
		 * @see JOptionPane
		 */
		private JOptionPane optionPane;
		/**
		 * The <Code>Boolean</Code> array representation of the analyzed play board
		 */
		private Boolean[] currentBoard;
		/**
    	 * This <Code>int</Code> array represents the number of men on the specific pile
    	 * <p>
    	 * The pile are numbered from 0 to 3:<br>
    	 * Pile 0: The unset men of blue,<br>
    	 * Pile 1: The lost men of red,<br>
    	 * Pile 2: The unset men of red,<br>
    	 * Pile 3: The lost men of blue.
    	 */
		private int[] pile;

		private ConfirmDialog(JFrame parent, Boolean[] currentBoard, int[] pile)
		{	
			this.currentBoard = currentBoard;
			this.pile = pile;
			String[] options = {"Apply","Correct","Visual Read In"};
			optionPane = new JOptionPane(new AnalyzedPlayBoard(currentBoard), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[0]);
			JDialog dialog = optionPane.createDialog(parent, "Analysis Successful");
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setSize(600, 255);
			dialog.setLocationRelativeTo(parent);
			dialog.setModal(true);
			dialog.setVisible(true);
		}
		
		/**
		 * @return after the user pressed a button on the dialog, this method returns a boolean representation of the pressed button
		 */
		private boolean confirm()
		{
			while(optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE);
			String answer="";
			if(optionPane.getValue() instanceof String)
			{
				answer = (String) optionPane.getValue();
			}
			switch(answer)
			{
				case "Apply":
					return true;
				case "Correct": 
					new GUI_ReadIn(gui_handler,currentBoard,pile);
					return false;
				case "Visual Read In": 
					new GUI_ReadIn(gui_handler);
					return false;
				default: 
					return false;
			}
		}
		
		/**
		 * This class paints a thumbnail of the analyzed play board on a <Code>JPanel</Code>
		 * <p>
		 * This <Code>JPanel</Code> with the play board on it, is shown in the confirm dialog after the analysis of the play board.
		 * The user then can see if the analysis was right or not and can react accordingly.
		 * @author Nick Weber
		 * @version 2.0
		 * @see JPanel
		 */
		private class AnalyzedPlayBoard extends JPanel
		{
			/**
			 * The constant for the width (pixels) of the tokens to draw on the play board
			 * @see #redraw(BufferedImage)
			 */
			private final int WIDTH = 14;
			/**
			 * The constant for the height (pixels) of the tokens to draw on the play board
			 * @see #redraw(BufferedImage)
			 */
			private final int HEIGHT = 14;
			/**
			 * The x-coordinates of the fields of the play board within the <code>JPanel</code>. From bottom to top, from left to right
			 * @see #redraw(BufferedImage)
			 */
			private final int[] x = {4,68,133,25,68,111,46,68,90,4,25,46,90,111,133,46,68,90,25,68,111,4,68,133};
			/**
			 * The y-coordinates of the fields of the play board within the <code>JPanel</code>. From bottom to top, from left to right
			 * @see #redraw(BufferedImage)
			 */
			private final int[] y = {133,133,133,111,111,111,90,90,90,69,69,69,69,69,69,46,46,46,25,25,25,4,4,4};
			/**
			 * The three possible colors to draw the men on the board: blue, red or transparent
			 * @see #redraw(BufferedImage)
			 */
			private final Color[] color = {new Color(5, 35, 140), new Color(200,0,35), new Color(0,0,0,0)};
			/**
			 * This object saves the representation of the play board
			 */
			private BufferedImage board;
			private static final long serialVersionUID = 1l;
			
			private AnalyzedPlayBoard(Boolean[] currentBoard)
			{
				this.setLayout(null);
				try 
				{
					this.board = ImageIO.read(getClass().getResourceAsStream("/NMM_Small.png"));
				} 
				catch (IOException e) 
				{	
					e.printStackTrace();
				}
				this.board = this.redraw(board,currentBoard);
				JLabel jlbl = new JLabel("<html>"
						+"You can see the analyzed play board on the left.<br>"
						+"If the analysis is incorrect, you may want to make a new visual read in or correct the consisiting play board.<br>"
						+"Note: If \"Apply\" is choosen, NAO will continue with its Move.</html>");
				jlbl.setBounds(160, 20, 390, 100);
				this.add(jlbl);
				this.repaint();
			}
		    	
			/**
			 * The purpose of this method is to draw the token placement on the board.
			 * @param emptyBoard the play board to draw the placement of the tokens on
			 * @param currentBoard the <Code>Boolean</Code> array representation of the token placement on the board
			 */
			private BufferedImage redraw(BufferedImage emptyBoard, Boolean[] currentBoard)
			{
				BufferedImage img = new BufferedImage(emptyBoard.getWidth(), emptyBoard.getHeight(),BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.drawImage(emptyBoard, 0, 0, null);
				for(int i = 0; i < 24; i++) 
				{
					if(currentBoard[i] == null)
					{
						g2d.setColor(color[2]);
						g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
					}
					else if(currentBoard[i] == true)
					{
						g2d.setColor(color[0]);
						g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					}
					else
					{
						g2d.setColor(color[1]);
						g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					}
				}
				return img;
			}
			
			/**
			 * {@inheritDoc}
			 * @param g the Graphics object to draw
			 */
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(this.board, 0, 0,150,150,null);
			}
		}
	}
	
	/**
	 * This class is responsible for the manual read in of the play board.
	 * <p>
	 * After the play board was analyzed by NAO, the user can determine if the analysis
	 * was right or not. If not he may want to do a visual read in within this class.
	 * @author Nick Weber
	 * @version 2.0
	 */
 	private class GUI_ReadIn extends JFrame
	{
		/**
		 * The <Code>Container</Code> on which all the components of the frame are added
		 * @see Container
		 * @see #add(Component)
		 */
		private Container pane;
		/**
		 * The <Code>JPanel</Code> within the frame, on which the play board assignment will be read in
		 * @see VisualReadIn
		 */
		private VisualReadIn vri;
		/**
    	 * This <Code>int</Code> array represents the number of men on the specific pile
    	 * <p>
    	 * The pile are numbered from 0 to 3:<br>
    	 * Pile 0: The unset men of blue,<br>
    	 * Pile 1: The lost men of red,<br>
    	 * Pile 2: The unset men of red,<br>
    	 * Pile 3: The lost men of blue.
    	 */
    	private int[] pile;
    	private JLabel[] placed;
		private static final long serialVersionUID = 1l;
		
		/**
		 * The constructor for a whole new visual read in
		 * @param gui_handler the <Code>GUI_Handler</Code> to communicate with.
		 */
		private GUI_ReadIn(GUI_Handler gui_handler)
		{
			try 
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			} 
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) 
			{
				e.printStackTrace();
			}
			this.setTitle("Manual Playboard Read In");
			this.setSize(600,750);
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.setResizable(false);
			this.setLocationRelativeTo(null);
			GUI_ReadIn gui_readIn = this;
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
					int confirm = JOptionPane.showOptionDialog(gui_readIn,"Do you really want to close this frame?", "Close Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (confirm == JOptionPane.YES_OPTION) 
					{
						gui_readIn.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}
				}
			});
			pane = getContentPane();
			pane.setBackground(Color.WHITE);
			pane.setLayout(new BorderLayout());
			pile = new int[4];
			vri = new VisualReadIn();
			pane.add(vri, BorderLayout.CENTER);
			JButton readIn = new JButton("Read In");
			readIn.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					int blue = 0, red = 0;
					for (int i = 0; i < gui_readIn.getColorsMenOnBoard().length; i++) 
					{
						if(gui_readIn.getColorsMenOnBoard()[i] != null)
						{
							if(gui_readIn.getColorsMenOnBoard()[i] == true)
							{
								blue++;
							}
							else if(gui_readIn.getColorsMenOnBoard()[i] == false)
							{
								red++;
							}
						}
					}
					int blue_pile = gui_readIn.getPile()[0] + gui_readIn.getPile()[3];
					int red_pile = gui_readIn.getPile()[2] + gui_readIn.getPile()[1];
					if(((blue + blue_pile) == 9) && ((red + red_pile) == 9))
					{
						/**
						 * The possible options in the <Code>JOptionPane</Code> if asked for confirmation to close the frame
						 */
						String[] options = {"     Yes     ", "     No     "};
						int confirm = JOptionPane.showOptionDialog(gui_readIn,"Do you really want to read the current assignment in?\nThis frame will be closed afterwards!", "Read in Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if (confirm == JOptionPane.YES_OPTION) 
						{
							gui_handler.sendPlayboardAssignment(gui_readIn.getColorsMenOnBoard(), gui_readIn.getPile());
							gui_handler.getGUI().getGUI_Components().getPlayboard().setPlayboard(gui_readIn.getColorsMenOnBoard());
							gui_readIn.dispose();
						}
					}
					else
					{
						if(((blue + blue_pile) != 9) && ((red + red_pile) != 9))
						{
							JLabel jlbl = new JLabel("The number of gaming pieces on the play board and on the pile must amount to 9.");
							JOptionPane.showMessageDialog(gui_readIn, jlbl, "Incorrect number of gaming pieces", JOptionPane.ERROR_MESSAGE);
						}
						else if((red + red_pile) != 9)
						{
							JLabel jlbl = new JLabel("The number of red gaming pieces on the play board and on the pile must amount to 9.");
							JOptionPane.showMessageDialog(gui_readIn, jlbl, "Incorrect number of red gaming pieces", JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							JLabel jlbl = new JLabel("The number of blue gaming pieces on the play board and on the pile must amount to 9.");
							JOptionPane.showMessageDialog(gui_readIn, jlbl, "Incorrect number of blue gaming pieces", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			createPilePanel(gui_readIn.getColorsMenOnBoard());
			pane.add(readIn, BorderLayout.SOUTH);
			vri.drawing();
			this.setVisible(true);
			if(showMessage)
			{
				JPanel pop_up_panel = new JPanel();
				pop_up_panel.setLayout(new BoxLayout(pop_up_panel, BoxLayout.Y_AXIS));
				JCheckBox check = new JCheckBox("Don't show this message again");
				check.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						JCheckBox temp = (JCheckBox) e.getSource();
						if(temp.isSelected())
						{
							showMessage = false;
						}
						else showMessage = true;
					}
					
				});
				JLabel jlbl = new JLabel("<html>"
						+ "You can now do the assignment of the play board."
						+ "<br>"
						+ "In order to assign a gaming piece, click on the respective field."
						+ "<br>"
						+ "You can change the color of the gaming piece with another click on the field."
						+ "<br>"
						+ "<b>Note: The number of placed, unplaced and lost gaming pieces must amount to 9.</b>"
						+"</html>");
				pop_up_panel.add(jlbl);
				pop_up_panel.add(check);
				JOptionPane.showMessageDialog(gui_readIn, pop_up_panel, "Notification", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		private GUI_ReadIn(GUI_Handler gui_handler, Boolean[] currentMenOnBoard, int[] pile)
		{
			try 
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			} 
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) 
			{
				e.printStackTrace();
			}
			this.setTitle("Manual Playboard Read In");
			this.setSize(600,750);
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.setResizable(false);
			this.setLocationRelativeTo(null);
			GUI_ReadIn gui_readIn = this;
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
					int confirm = JOptionPane.showOptionDialog(gui_readIn,"Do you really want to close this frame?", "Close Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (confirm == JOptionPane.YES_OPTION) 
					{
						gui_readIn.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}
				}
			});
			pane = getContentPane();
			pane.setBackground(Color.WHITE);
			pane.setLayout(new BorderLayout());
			this.pile = pile;
			vri = new VisualReadIn(currentMenOnBoard);
			pane.add(vri, BorderLayout.CENTER);
			JButton readIn = new JButton("Read In");
			readIn.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					int blue = 0, red = 0;
					for (int i = 0; i < gui_readIn.getColorsMenOnBoard().length; i++) 
					{
						if(gui_readIn.getColorsMenOnBoard()[i] != null)
						{
							if(gui_readIn.getColorsMenOnBoard()[i] == true)
							{
								blue++;
							}
							else if(gui_readIn.getColorsMenOnBoard()[i] == false)
							{
								red++;
							}
						}
					}
					int blue_pile = gui_readIn.getPile()[0] + gui_readIn.getPile()[3];
					int red_pile = gui_readIn.getPile()[2] + gui_readIn.getPile()[1];
					if(((blue + blue_pile) == 9) && ((red + red_pile) == 9))
					{
						/**
						 * The possible options in the <Code>JOptionPane</Code> if asked for confirmation to close the frame
						 */
						String[] options = {"     Yes     ", "     No     "};
						int confirm = JOptionPane.showOptionDialog(gui_readIn,"Do you really want to read the current assignment in?\nThis frame will be closed afterwards!", "Read in Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if (confirm == JOptionPane.YES_OPTION) 
						{
							gui_handler.sendPlayboardAssignment(gui_readIn.getColorsMenOnBoard(), gui_readIn.getPile());
							gui_handler.getGUI().getGUI_Components().getPlayboard().setPlayboard(gui_readIn.getColorsMenOnBoard());
							gui_readIn.dispose();
						}
					}
					else
					{
						if(((blue + blue_pile) != 9) && ((red + red_pile) != 9))
						{
							JLabel jlbl = new JLabel("The number of gaming pieces on the play board and on the pile must amount to 9.");
							JOptionPane.showMessageDialog(gui_readIn, jlbl, "Incorrect number of gaming pieces", JOptionPane.ERROR_MESSAGE);
						}
						else if((red + red_pile) != 9)
						{
							JLabel jlbl = new JLabel("The number of red gaming pieces on the play board and on the pile must amount to 9.");
							JOptionPane.showMessageDialog(gui_readIn, jlbl, "Incorrect number of red gaming pieces", JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							JLabel jlbl = new JLabel("The number of blue gaming pieces on the play board and on the pile must amount to 9.");
							JOptionPane.showMessageDialog(gui_readIn, jlbl, "Incorrect number of blue gaming pieces", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			createPilePanel(currentMenOnBoard);
			pane.add(readIn, BorderLayout.SOUTH);
			vri.drawing();
			this.setVisible(true);
			if(showMessage)
			{
				JPanel pop_up_panel = new JPanel();
				pop_up_panel.setLayout(new BoxLayout(pop_up_panel, BoxLayout.Y_AXIS));
				JCheckBox check = new JCheckBox("Don't show this message again");
				check.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						JCheckBox temp = (JCheckBox) e.getSource();
						if(temp.isSelected())
						{
							showMessage = false;
						}
						else showMessage = true;
					}
					
				});
				JLabel jlbl = new JLabel("<html>"
						+ "You can now do the assignment of the play board."
						+ "<br>"
						+ "In order to assign a gaming piece, click on the respective field."
						+ "<br>"
						+ "You can change the color of the gaming piece with another click on the field."
						+ "<br>"
						+ "<b>Note: The number of placed, unplaced and lost gaming pieces must amount to 9.</b>"
						+"</html>");
				pop_up_panel.add(jlbl);
				pop_up_panel.add(check);
				JOptionPane.showMessageDialog(gui_readIn, pop_up_panel, "Notification", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		/**
		 * Sets the text shown on the <Code>placed</Code> <Code>JLabel</Code>
		 * @see #placed
		 * @see JLabel
		 */
		private void setPlacedLabel()
		{
			int blue = 0, red = 0;
			for(int i = 0; i < vri.getColorsMenOnBoard().length; i++) 
			{
				if(vri.getColorsMenOnBoard()[i] != null)
				{
					if(vri.getColorsMenOnBoard()[i] == true)
					{
						blue++;
					}
					else
					{
						red++;
					}
				}
			}
			placed[0].setText(blue+" placed blue gaming pieces");
			placed[1].setText(red+" placed red gaming pieces");
			placed[0].repaint();
			placed[1].repaint();
		}
		
		/**
		 * @return the pile representation
		 */
		private int[] getPile()
		{
			return this.pile;
		}
		
		/**
		 * Creates a <Code>JPanel</Code> with <Code>JButton</Code> on it to increase and decrease the pile number.
		 * @see #pile
		 * @see JPanel
		 * @see JButton
		 */
		private void createPilePanel(Boolean[] currentBoard)
		{
			JPanel piles_pnl = new JPanel(new GridBagLayout());
			piles_pnl.setBackground(new Color(198,193,112));
			piles_pnl.setPreferredSize(new Dimension(600,100));
			GridBagConstraints gbc = new GridBagConstraints();

			int blue = 0, red = 0;
			for(int i = 0; i < currentBoard.length; i++) 
			{
				if(currentBoard[i] != null)
				{
					if(currentBoard[i] == true)
					{
						blue++;
					}
					else
					{
						red++;
					}
				}
			}
			placed = new JLabel[2];
			placed[0] = new JLabel(blue + " placed blue gaming pieces");
			placed[1] = new JLabel(red+ " placed red gaming pieces");
			
			JButton[] buttons = new JButton[8];
			buttons[0] = new JButton("-");	//Decrease blue unset gaming piece
			buttons[1] = new JButton("+");	//Increase blue unset gaming piece
			buttons[2] = new JButton("-");	//Decrease red unset gaming piece
			buttons[3] = new JButton("+");	//Increase red unset gaming piece
			buttons[4] = new JButton("-");	//Decrease blue lost gaming piece
			buttons[5] = new JButton("+");	//Increase blue los gaming piece
			buttons[6] = new JButton("-");	//Decrease red lost gaming piece
			buttons[7] = new JButton("+");	//Increase red lost gaming piece
			
			//pile[0]: Blue unset gaming piece
			if(pile[0]<1)
			{
				buttons[0].setEnabled(false);
			} 
			else if(pile[0]>8)
			{
				buttons[0].setEnabled(false);
			}
			//pile[1]: Red lost gaming piece
			if(pile[1]<1)
			{
				buttons[6].setEnabled(false);
			}
			else if(pile[1]>8)
			{
				buttons[6].setEnabled(false);
			}
			//pile[2]: Red unset gaming piece
			if(pile[2]<1)
			{
				buttons[2].setEnabled(false);
			}
			else if(pile[2]>8)
			{
				buttons[3].setEnabled(false);
			}
			//pile[3]: Blue lost gaming piece
			if(pile[3]<1)
			{
				buttons[4].setEnabled(false);
			}
			else if(pile[3]>8)
			{
				buttons[5].setEnabled(false);
			}
			
			JLabel[] pile_lbl = new JLabel[4];
			pile_lbl[0] = new JLabel(pile[0]+" unplaced blue gaming pieces");
			pile_lbl[1] = new JLabel(pile[3]+" lost blue gaming pieces");
			pile_lbl[2] = new JLabel(pile[2]+" unplaced red gaming pieces");
			pile_lbl[3] = new JLabel(pile[1]+" lost red gaming pieces");
			
			//The ActionListener for the Decrease/Increase Buttons
			ActionListener bL = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					String action = e.getActionCommand();
					switch(action)
					{
						case "0":
							pile[0]--;
							pile_lbl[0].setText(pile[0]+" unplaced blue gaming pieces");
							buttons[1].setEnabled(true);
							break;
						case "1":
							pile[0]++;
							pile_lbl[0].setText(pile[0]+" unplaced blue gaming pieces");
							buttons[0].setEnabled(true);
							break;
						case "2":
							pile[2]--;
							pile_lbl[2].setText(pile[2]+" unplaced red gaming pieces");
							buttons[3].setEnabled(true);
							break;
						case "3":
							pile[2]++;
							pile_lbl[2].setText(pile[2]+" unplaced red gaming pieces");
							buttons[2].setEnabled(true);
							break;
						case "4":
							pile[3]--;
							pile_lbl[1].setText(pile[3]+" lost blue gaming pieces");
							buttons[5].setEnabled(true);
							break;
						case "5":
							pile[3]++;
							pile_lbl[1].setText(pile[3]+" lost blue gaming pieces");
							buttons[4].setEnabled(true);
							break;
						case "6":
							pile[1]--;
							pile_lbl[3].setText(pile[1]+" lost red gaming pieces");
							buttons[7].setEnabled(true);
							break;
						case "7":
							pile[1]++;
							pile_lbl[3].setText(pile[1]+" lost red gaming pieces");
							buttons[6].setEnabled(true);
							break;
					}
					if(pile[0]==0)
					{
						buttons[0].setEnabled(false);
					}
					else if(pile[0]==9)
					{
						buttons[1].setEnabled(false);
					}
					if(pile[1]==0)
					{
						buttons[6].setEnabled(false);
					}
					else if(pile[1]==9)
					{
						buttons[7].setEnabled(false);
					}
					if(pile[2]==0)
					{
						buttons[2].setEnabled(false);
					}
					else if(pile[2]==9)
					{
						buttons[3].setEnabled(false);
					}
					if(pile[3]==0)
					{
						buttons[4].setEnabled(false);
					}
					else if(pile[3]==9)
					{
						buttons[5].setEnabled(false);
					}
				}
			};
			
			for(int i = 0; i < buttons.length; i++) 
			{
				buttons[i].addActionListener(bL);
				buttons[i].setActionCommand(""+i);
			}
			
			JButton clear = new JButton("Clear");
			clear.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					vri.reset();
					pile[0] = 9;
					buttons[1].setEnabled(false);
					buttons[0].setEnabled(true);
					pile[1] = 0;
					buttons[6].setEnabled(false);
					buttons[7].setEnabled(true);
					pile[2] = 9;
					buttons[3].setEnabled(false);
					buttons[2].setEnabled(true);
					pile[3] = 0;
					buttons[4].setEnabled(false);
					buttons[5].setEnabled(true);
					pile_lbl[0].setText(pile[0]+" unplaced blue gaming pieces");
					pile_lbl[1].setText(pile[3]+" lost blue gaming pieces");
					pile_lbl[2].setText(pile[2]+" unplaced red gaming pieces");
					pile_lbl[3].setText(pile[1]+" lost red gaming pieces");
					for(int i = 0; i < pile_lbl.length; i++) 
					{
						pile_lbl[i].repaint();
					}
				}
				
			});
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(0, 0, 5, 0);
			piles_pnl.add(placed[0],gbc);
			
			gbc.gridwidth = 0;
			gbc.gridx = 1;
			gbc.insets = new Insets(0, 0, 5, 40);
			piles_pnl.add(clear,gbc);
			
			gbc.gridwidth = 3;
			gbc.gridx = 3;
			gbc.insets = new Insets(0, 0, 5, 0);
			piles_pnl.add(placed[1],gbc);
			gbc.gridwidth = 1;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(0, 5, 0, 5);
			piles_pnl.add(buttons[0],gbc);
			gbc.gridx = 1;
			piles_pnl.add(pile_lbl[0],gbc);
			gbc.gridx = 2;
			piles_pnl.add(buttons[1],gbc);

			gbc.insets = new Insets(0, 30, 0, 5);
			
			gbc.gridx = 3;
			piles_pnl.add(buttons[2],gbc);
			gbc.insets = new Insets(0, 5, 0, 5);
			gbc.gridx = 4;
			piles_pnl.add(pile_lbl[2],gbc);
			gbc.gridx = 5;
			piles_pnl.add(buttons[3],gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 2;
			piles_pnl.add(buttons[4],gbc);
			gbc.gridx = 1;
			piles_pnl.add(pile_lbl[1],gbc);
			gbc.gridx = 2;
			piles_pnl.add(buttons[5],gbc);

			gbc.insets = new Insets(0, 30, 0, 5);
			
			gbc.gridx = 3;
			piles_pnl.add(buttons[6],gbc);
			gbc.insets = new Insets(0, 5, 0, 5);
			gbc.gridx = 4;
			piles_pnl.add(pile_lbl[3],gbc);
			gbc.gridx = 5;
			piles_pnl.add(buttons[7],gbc);
			
			pane.add(piles_pnl, BorderLayout.NORTH);
		}
		
		/**
		 * @return the <Code>Boolean</Code> array representation of the play board, the user just read in.
		 * @see VisualReadIn#getColorsMenOnBoard()
		 */
		private Boolean[] getColorsMenOnBoard()
		{
			return vri.getColorsMenOnBoard();
		}
		
		/**
		 * The <Code>JPanel</Code> on which the user selects the field assignment on the play board.
		 * @author Nick Weber
		 * @version 2.0
		 * @see JPanel
		 */
		private  class VisualReadIn extends JPanel
		{
			/**
	    	 * The constant for the width (pixels) of the tokens to draw on the play board
	    	 * @see #redraw(BufferedImage)
	    	 */
	    	private final int WIDTH = 50;
	    	/**
	    	 * The constant for the height (pixels) of the tokens to draw on the play board
	    	 * @see #redraw(BufferedImage)
	    	 */
	    	private final int HEIGHT = 50;
	    	/**
	    	 * The x-coordinates of the fields of the play board within the <code>JPanel</code>. From bottom to top, from left to right
	    	 * @see #redraw(BufferedImage)
	    	 */
	    	private final int[] x = {15,270,530,100,270,445,185,270,360,15,100,185,360,445,530,185,270,360,100,270,445,15,270,530};
	    	/**
	    	 * The y-coordinates of the fields of the play board within the <code>JPanel</code>. From bottom to top, from left to right
	    	 * @see #redraw(BufferedImage)
	    	 */
	    	private final int[] y = {530,530,530,445,445,445,360,360,360,275,275,275,275,275,275,185,185,185,100,100,100,15,15,15};
	    	/**
	    	 * The three possible colors to draw the men on the board: blue, red or transparent
	    	 * @see #redraw(BufferedImage)
	    	 */
	    	private final Color[] color = {new Color(5, 35, 140), new Color(200,0,35), new Color(0,0,0,0)};
	    	/**
	    	 * This object saves the representation of the play board without any men on it
	    	 */
	    	private BufferedImage board;
	    	/**
	    	 * This objects saves the representation of the play board with the currently placed men on it
	    	 * @see #redraw(BufferedImage)
	    	 * @see #paintComponent(Graphics)
	    	 */
	    	private BufferedImage toDraw;
	    	/**
	    	 * This Boolean array represents which color is on the playboard's 24 fields.
	    	 * <p>
	    	 * The play board's fields are numbered from 0 to 23, from bottom to top, from left to right.<br>
	    	 * 0 for bottom left, 23 for top right.<br>
	    	 * An array position represents the men's color on the position:<br>
	    	 * *null = transparent<br>
	    	 * *true = blue<br>
	    	 * *false = red
	    	 */
	    	private Boolean[] colorsMenOnBoard;
	    	private static final long serialVersionUID = 1l;
	    	
	    	/**
	    	 * The constructor for a whole new visual read in
	    	 */
	    	private VisualReadIn()
	    	{
	    		try 
	    		{
					this.board = ImageIO.read(getClass().getResourceAsStream("/NMM.png"));
				} 
	    		catch (IOException e) 
	    		{
					e.printStackTrace();
				}
	    		this.setSize(600, 600);
	    		this.addMouseListener(new PlacedListener());
	    		colorsMenOnBoard = new Boolean[24];
	    		this.toDraw = board;
	    	}
	    	
	    	/**
	    	 * The constructor if the analyzed play board will be corrected
	    	 * @param colorsMenOnBoard the play board assignment from the analysis
	    	 */
	    	private VisualReadIn(Boolean[] colorsMenOnBoard)
	    	{
	    		try 
	    		{
					this.board = ImageIO.read(getClass().getResourceAsStream("/NMM.png"));
				} 
	    		catch (IOException e) 
	    		{
					e.printStackTrace();
				}
	    		this.setSize(600, 600);
	    		this.addMouseListener(new PlacedListener());
	    		this.colorsMenOnBoard = colorsMenOnBoard;
	    		this.toDraw = board;
	    		this.setPlayboard();
	    	}
	    	
	    	private void reset()
	    	{
	    		for(int i = 0; i < colorsMenOnBoard.length; i++) 
	    		{
	    			colorsMenOnBoard[i] = null;
				}
	    		setPlayboard();
	    		placed[0].setText(0 + " placed blue gaming pieces");
				placed[1].setText(0 + " placed red gaming pieces");
	    	}
	    	
	    	/**
	    	 * The purpose of this method is to draw the token placement on the board.
	    	 * <p>
	    	 * Every time a token is placed by either NAO or its enemy, this method is
	    	 * called to redraw the board with the current placement.<br>
	    	 * The redrawn board image is saved in the <Code>toDraw</Code> attribute.
	    	 * @param old the play board to draw the placement of the tokens on
	    	 */
	    	private void redraw(BufferedImage old)
	    	{
	    		BufferedImage img = new BufferedImage(old.getWidth(), old.getHeight(),BufferedImage.TYPE_INT_ARGB);
	    		Graphics2D g2d = img.createGraphics();
	    		g2d.drawImage(old, 0, 0, null);
	    		for(int i = 0; i < 24; i++) 
	    		{
	    			if(colorsMenOnBoard[i] == null)
	    			{
	    				g2d.setColor(color[2]);
	    				g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
	    			}
	    			else if(colorsMenOnBoard[i] == true)
	    			{
	    				g2d.setColor(color[0]);
	    				g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
	    				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    			}
	    			else
	    			{
	    				g2d.setColor(color[1]);
	    				g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
	    				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    			}
	    		}
	    		this.toDraw = img;
	    	}
	    	
	    	/**
	    	 * Getter method to access the image of the play board
	    	 * @return the BufferedImage of the play board
	    	 * @see BufferedImage
	    	 */
	    	private BufferedImage getImage()
	    	{
	    		return this.board;
	    	}
	    	
	    	/**
	    	 * @return the colors of the men on the board with the current assignment
	    	 */
	    	private Boolean[] getColorsMenOnBoard()
	    	{
	    		return this.colorsMenOnBoard;
	    	}
	    	
	    	/**
	    	 * This method draws the <Code>Boolean</Code> array representation of the current assignment on the board
	    	 */
	    	private void setPlayboard()
	    	{
	    		this.redraw(this.getImage());
	    		this.drawing();
	    	}
	    	
	    	/**
	    	 * This method calls the repaint method because the repaint
	    	 * method can not be called outside of this class.
	    	 * <p>
	    	 * The repaint method is needed to update the frame and
	    	 * repaint its components.
	    	 * @see #repaint()
	    	 */
	    	private void drawing()
	    	{
	    		repaint();
	    	}
	    	
	    	/**
	    	 * {@inheritDoc}
	    	 * @param g the Graphics object to draw
	    	 */
	    	@Override
	    	public void paintComponent(Graphics g)
	    	{
	    		super.paintComponent(g);
	    		g.drawImage(this.toDraw, 0, 0,600,600,null);
	    	}
	    	
	    	/**
	    	 * A <Code>MouseListener</Code> for the VisualReadIn panel.
	    	 * <p>
	    	 * This <Code>MouseListener</Code> will call methods to draw a token on the fields the user clicked on.
	    	 * @author Nick Weber
	    	 * @version 2.0
	    	 * @see MouseListener
	    	 * @see VisualReadIn
	    	 */
	    	private class PlacedListener implements MouseListener
	        {
	    		@Override
	    		public void mouseClicked(MouseEvent e) 
	    		{
	    			int field = -1;
	    			if((530<e.getY())&&(e.getY()<585))
	    			{
	    				if((15 < e.getX()) && (e.getX() < 70))
	    				{
	    					field = 0;
	    				}
	    				else if((270 < e.getX()) && (e.getX() < 325))
	    				{
	    					field = 1;
	    				}
	    				else if((530 < e.getX()) && (e.getX() < 585))
	    				{
	    					field = 2;
	    				}
	    			}
	    			else if((445<e.getY())&&(e.getY()<500))
	    			{
	    				if((100 < e.getX()) && (e.getX() < 155))
	    				{
	    					field = 3;
	    				}
	    				else if((270 < e.getX()) && (e.getX() < 325))
	    				{
	    					field = 4;
	    				}
	    				else if((445 < e.getX()) && (e.getX() < 500))
	    				{
	    					field = 5;
	    				}
	    			}
	    			else if((360<e.getY())&&(e.getY()<415))
	    			{
	    				if((185 < e.getX()) && (e.getX() < 240))
	    				{
	    					field = 6;
	    				}
	    				else if((270 < e.getX()) && (e.getX() < 325))
	    				{
	    					field = 7;
	    				}
	    				else if((360 < e.getX()) && (e.getX() < 415))
	    				{
	    					field = 8;
	    				}
	    			}
	    			else if((275<e.getY())&&(e.getY()<330))
	    			{
	    				if((15 < e.getX()) && (e.getX() < 70))
	    				{
	    					field = 9;
	    				}
	    				else if((100 < e.getX()) && (e.getX() < 155))
	    				{
	    					field = 10;
	    				}
	    				else if((185 < e.getX()) && (e.getX() < 240))
	    				{
	    					field = 11;
	    				}
	    				else if((360 < e.getX()) && (e.getX() < 415))
	    				{
	    					field = 12;
	    				}
	    				else if((445 < e.getX()) && (e.getX() < 500))
	    				{
	    					field = 13;
	    				}
	    				else if((530 < e.getX()) && (e.getX() < 585))
	    				{
	    					field = 14;
	    				}
	    			}
	    			else if((185<e.getY())&&(e.getY()<240))
	    			{
	    				if((185 < e.getX()) && (e.getX() < 240))
	    				{
	    					field = 15;
	    				}
	    				else if((270 < e.getX()) && (e.getX() < 325))
	    				{
	    					field = 16;
	    				}
	    				else if((360 < e.getX()) && (e.getX() < 415))
	    				{
	    					field = 17;
	    				}
	    			}
	    			else if((100<e.getY())&&(e.getY()<155))
	    			{
	    				if((100 < e.getX()) && (e.getX() < 155))
	    				{
	    					field = 18;
	    				}
	    				else if((270 < e.getX()) && (e.getX() < 325))
	    				{
	    					field = 19;
	    				}
	    				else if((445 < e.getX()) && (e.getX() < 500))
	    				{
	    					field = 20;
	    				}
	    			}
	    			else if((15<e.getY())&&(e.getY()<70))
	    			{
	    				if((15 < e.getX()) && (e.getX() < 70))
	    				{
	    					field = 21;
	    				}
	    				else if((270 < e.getX()) && (e.getX() < 325))
	    				{
	    					field = 22;
	    				}
	    				else if((530 < e.getX()) && (e.getX() < 585))
	    				{
	    					field = 23;
	    				}
	    			}
	    			
	    			if(0<=field && field <= 23)
	    			{
		    			if(colorsMenOnBoard[field] == null)
						{
							colorsMenOnBoard[field] = true;
						}
						else if(colorsMenOnBoard[field] == true)
						{
							colorsMenOnBoard[field] = false;
						}
						else if(colorsMenOnBoard[field] == false)
						{
							colorsMenOnBoard[field] = null;
						}
		    			setPlayboard();
		    			setPlacedLabel();
	    			}
	    		}

	    		@Override
	    		public void mousePressed(MouseEvent e) {
	    			// TODO Auto-generated method stub
	    			
	    		}

	    		@Override
	    		public void mouseReleased(MouseEvent e) {
	    			// TODO Auto-generated method stub
	    			
	    		}

	    		@Override
	    		public void mouseEntered(MouseEvent e) {
	    			// TODO Auto-generated method stub
	    			
	    		}

	    		@Override
	    		public void mouseExited(MouseEvent e) {
	    			// TODO Auto-generated method stub
	    		}
	        }
	    }
	}   
}