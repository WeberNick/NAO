package ui.view;

import java.awt.image.BufferedImage;
import javax.swing.JButton;
import application.control.Controller;

/**
 * The purpose of this class is to handle the information
 * flow between the user interface and the <Code>Controller</code>.
 * 
 * @author Nick Weber
 * @version 2.0
 * @see Controller
 */
public class GUI_Handler 
{
	/**
	 * The <Code>Controller</Code> object to communicate with.
	 * @see Controller
	 */
	private Controller controller;
	/**
	 * The <Code>GUI</Code> object to send the informations to.
	 * @see GUI
	 */
	private GUI gui;
	/**
	 * An integer representing the current camera to be shown in the GUI. 
	 * <p>
	 * 0 = top camera, 1 = bottom camera
	 */
	private int currentCamera;
	
	GUI_Handler(GUI gui, Controller controller)
	{
		this.gui = gui;
		this.controller = controller;
		this.currentCamera = 0;
	}
	
	/**
	 * Terminates the currently running Java Virtual Machine.
	 * @see Controller#terminate()
	 */
	void terminate()
	{
		this.controller.terminate();
	}
	
	/**
	 * Starts the <Code>CameraStream</Code> Thread in the <Code>Controller</Code>.
	 * @see Controller.CameraStream
	 * @see Controller
	 * @see Controller#startCameraStream()
	 * @see Thread
	 */
	void startCameraStream()
	{
		this.controller.startCameraStream();
	}
	
	/**
	 * Changes the shown camera in the <Code>GUI</Code>
	 * @see GUI
	 * @see GUI_Camera
	 */
	void changeCamera()
	{
		if(this.currentCamera==0)
		{
			this.currentCamera = 1;
		}
		else if(this.currentCamera==1)
		{
			this.currentCamera = 0;
		}
	}
	
	/**
	 * Sends the manually read in play board assignment to the <Code>Playboard</Code>
	 * @param assignment the assignment of the play board
	 * @see domain.represent.Playboard
	 */
	void sendPlayboardAssignment(Boolean[] assignment, int[] piles)
	{
		this.controller.getPlayboard().updateByGUI(assignment, piles);
		this.controller.updateGameStatus();
	}
	
	/**
	 * This method is called to start the analysis of the play board in the <Code>Controller</Code>
	 * @see Controller#startAnalysis()
	 */
	void analyzePlayBoard()
	{
		this.controller.startAnalysis();
	}
	
	/**
	 * Starts the next move of NAO
	 * @see Controller#nextMove()
	 */
	void nextMove()
	{
		this.controller.startNextMove();
	}
	
	/**
	 * @return the main frame
	 */
	GUI getGUI()
	{
		return this.gui;
	}
	
	/**
	 * Calls the <Code>stopMove</Code> method of the <Code>Controller</Code> in order to stop the current move of NAO
	 * @see Controller#stopMove()
	 */
	void stopMove()
	{
		this.controller.stopMove();
	}
	
	/**
	 * Creates the AIPerformance object.
	 * @see Controller#createAIPerformance()
	 */
	void createAIPerformance()
	{
		controller.createAIPerformance();
	}
	
	/**
	 * Starts the performance monitoring thread
	 * @see Controller#startPerformanceMonitorThread()
	 * @see Controller.PerformanceMonitorThread
	 */
	void startPerformanceMonitor()
	{
		this.controller.startPerformanceMonitorThread();
	}
	
	/**
	 * @param clickable true if the button should be clickable and therefore enabled
	 */
	public void enableNextButton(boolean clickable)
	{
		this.gui.getGUI_Components().setNextButtonSetClickable(clickable);
	}
	
	/**
	 * Changes the text of the <Code>JButton</Code> for the next move.
	 * @param text the text that should be displayed on this button
	 * @see GUI_Components#next
	 * @see JButton
	 */
	public void changeNextButtonText(String text)
	{
		this.gui.getGUI_Components().changeNextButtonText(text);
	}
	
	/**
	 * After the play board was analyzed the user has to confirm that the analysis was correct.
	 * @param currentBoard the analyzed play board representation
	 * @param pile the representation of the number of men on the specific pile
	 * @return true if the analysis was correct, false otherwise.
	 */
	public boolean confirm(Boolean[] currentBoard, int[] pile)
	{
		return this.gui.getGUI_Components().confirm(currentBoard, pile);
	}
	
	/**
	 * The <Code>Controller</Code> calls this method to update the <Code>GUI</Code> with the current assignment on the play board.
	 * @param colors the color of the men on each field of the play board
	 * @see Controller
	 * @see GUI
	 */
	public void updatePlayboard(Boolean[] colors)
	{
		gui.getGUI_Components().getPlayboard().setPlayboard(colors);
	}
	
	public void updateGameStatus(int[] assignment)
	{
		int[] own = {assignment[0], assignment[1], assignment[2], assignment[3]};
		int[] opp = {assignment[4], assignment[5], assignment[6], assignment[7]};
		this.gui.getGUI_Components().getNao().updateStatus(own);
		this.gui.getGUI_Components().getOpponent().updateStatus(opp);
	}
	
	/**
	 * The class <Code>CameraStream</Code> calls this method to update the <Code>GUI</Code> with the current image taken from NAO's camera.
	 * @param img the image to be shown on the <Code>GUI</Code>
	 * @see GUI
	 * @see GUI_Camera
	 */
	public void updateImage(BufferedImage img)
	{
		gui.getGUI_Components().getCamera().updateImage(GUI_Camera.resize(img, 170, 100));
	}
	
	/**
	 * This method starts the counter for the current move time
	 */
	public void startMoveCount()
	{
		this.gui.getGUI_Components().getMoveCounter().getTimer().start();
	}
	
	/**
	 * This method stops the counter for the current move time.
	 * @param finished true if the move finished completely, false if only a intermediate step
	 */
	public void stopMoveCount(boolean finished)
	{
		if(finished)
		{
			this.gui.getGUI_Components().getMoveCounter().getTimer().stop();
			this.gui.getGUI_Components().getMoveCounter().setCounter(0, 0);
			this.gui.getGUI_Components().getMoveCounter().setSec(0);
			this.gui.getGUI_Components().getMoveCounter().setMin(0);
		}
		else this.gui.getGUI_Components().getMoveCounter().getTimer().stop();
		
	}
	
	/**
	 * This method stops the counter for the game time
	 */
	public void stopGameCount()
	{
		this.gui.getGUI_Components().getGameCounter().getTimer().stop();
	}
	
	/**
	 * Sets a <Code>JLabel</Code> in <Code>GUI_GameStatus</Code> with the game result for NAO.
	 * @param result a <Code>Boolean</Code> representation of the game result<br>
	 * <Code>null</Code> if a draw occurred<br>
	 * <Code>true</Code> for a win<br>
	 * <Code>false</Code> for a defeat<br>
	 * @see GUI_GameStatus
	 * @see GUI_GameStatus#setGameResultLabel(Boolean)
	 */
	public void setGameResultNAO(Boolean result)
	{
		this.gui.getGUI_Components().getNao().setGameResultLabel(result);
	}
	
	/**
	 * Sets a <Code>JLabel</Code> in <Code>GUI_GameStatus</Code> with the game result for the enemy.
	 * @param result a <Code>Boolean</Code> representation of the game result<br>
	 * <Code>null</Code> if a draw occurred<br>
	 * <Code>true</Code> for a win<br>
	 * <Code>false</Code> for a defeat<br>
	 * @see GUI_GameStatus
	 * @see GUI_GameStatus#setGameResultLabel(Boolean)
	 */
	public void setGameResultEnemy(Boolean result)
	{
		this.gui.getGUI_Components().getOpponent().setGameResultLabel(result);
	}
	
	/**
	 * @return if the current game is paused (true) or not (false)
	 * @see GUI_Components#getPause()
	 */
	public boolean getPause()
	{
		return this.gui.getGUI_Components().getPause();
	}
	
	/**
	 * @return the <Code>Controller</Code> of the <Code>GUI_Handler</Code>
	 * @see Controller
	 */
	public Controller getController()
	{
		return this.controller;
	}
	
	/**
	 * @return the integer representation of the current shown camera (0 = top camera, 1 = bottom camera)
	 */
	public int getCamera()
	{
		return this.currentCamera;
	}
	
	/**
	 * @return a boolean representation of the own color
	 */
	public boolean getOwnColor()
	{
		return this.gui.getOwnColor();
	}
	
	/**
	 * Sets a <Code>boolean</Code> representing if NAO is currently executing a task.
	 * @param current true if NAO is currently executing a task, false otherwise
	 * @see GUI_Components#setCurrentExecute(boolean)
	 */
	public void setCurrentExecute(boolean current)
	{
		this.gui.getGUI_Components().setCurrentExecute(current);
	}
	
	/**
	 * @param text the String to set on the performance label
	 * @see GUI_Components#setPerformanceLabel(String)
	 */
	public void setPerformanceLabel(String text)
	{
		this.gui.getGUI_Components().setPerformanceLabel(text);
	}
}
