package application.control;

import ui.view.*;
import domain.represent.Playboard;
import domain.nao.NAO_Handler;
import application.ai.AIPerformance;
import application.ai.ComputationalUnit;
import foundation.data.Move;
import foundation.monitor.PerformanceManager;

import java.awt.image.BufferedImage;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;

/**
 * This is the Controller class which is the central controlling unit between all classes.
 * <p>
 * The controller creates instances of the basic classes,<br>
 * handles the information flow from one class to another<br>
 * and calls basic methods.
 * 
 * @author Nick Weber
 * @version 2.0
 */
public class Controller extends Thread
{
	/**
	 * The virtual representation of the play board including its logic.
	 * @see Playboard
	 */
	private Playboard playboard;
	/**
	 * The Artificial Intelligence (AI) is charged with the choice of the next move.
	 * @see ComputationalUnit
	 * @see Move
	 */
	private ComputationalUnit ai;
	/**
	 * The handler of the user interface. All communication between the <Code>Controller</Code> and user interface
	 * runs over this <Code>GUI_Handler</Code>
	 * @see GUI_Handler
	 */
	private GUI_Handler gui_handler;
	/**
	 * The handler of the robot NAO. All communication between the <Code>Controller</Code> and NAO
	 * runs over this <Code>NAO_Handler</Code>
	 * @see NAO_Handler
	 */
	private NAO_Handler nao_handler;
	/**
	 * A <Code>Session</Code> object with the current connection
	 * @see Session
	 */
	private Session session;
	/**
	 * A <Code>boolean</Code> representing the connection status.<br>
	 * <Code>connected</Code> is true if a connection is established, false otherwise.
	 */
	private boolean connected;
	/**
	 * A help attribute in order to check if the <Code>instance()</Code> was already called.
	 */
	private boolean alreadyExecuted;
	/**
	 * The class which monitors the memory & cpu performance of the system.
	 * @see PerformanceManager
	 */
	private PerformanceManager performance_management;
	/**
	 * The class which customizes the AI performance depending on the game progress
	 * @see AIPerformance
	 */
	private AIPerformance ai_performance;

	/**
	 * Constructs the central controlling unit.
	 * @see ui.view.GUI_Connection
	 */
	Controller()
	{
		new GUI_Connection(this);		
	}
	
	/**
	 * This method 'starts' the other areas of the program (Playboard, AI, NAO).
	 * @see domain.represent.Playboard
	 * @see application.ai.ComputationalUnit
	 * @see domain.nao.Body
	 */
	private void initiate()
	{
		nao_handler = new NAO_Handler(this);
		playboard = new Playboard();
		ai = new ComputationalUnit();
		performance_management = new PerformanceManager();
	}
	
	/**
	 * This method can be called to call the <Code>initiate()</Code> method.
	 * <p>
	 * Note: The <Code>initiate()</Code> can only be called once.<br>
	 * This method sets a flag after the first successful call and therefore <Code>initiate()</Code> is never callable again.
	 * @see #initiate()
	 */
	public void initiateAll()
	{
		if(!alreadyExecuted)
		{
			initiate();
			this.alreadyExecuted = true;
		}
	}
	
	/**
	 * Terminates the currently running Java Virtual Machine.
	 * @see System#exit(int)
	 */
	public void terminate()
	{
		this.nao_handler.stopMove();
		System.exit(0);
	}
	
	/**
	 * Setter method to set the <Code>GUI_Handler</Code> of the <Code>Controller</Code>
	 * @param gui_handler the <Code>GUI_Handler</Code> to set
	 * @see Controller#gui_handler
	 * @see GUI_Handler
	 */
	public void setGUI_Handler(GUI_Handler gui_handler)
	{
		this.gui_handler = gui_handler;
	}
	
	/**
	 * Starts the <Code>CameraStream</Code> <Code>Thread</Code>.
	 * @see CameraStream
	 * @see Thread
	 */
	public void startCameraStream()
	{
		new CameraStream().start();
	}
	
	/**
	 * Starts the <Code>AnalyzeAndMove</Code> <Code>Thread</Code> in order to analyze the play board.
	 * @see AnalyzeAndMove
	 * @see #analyzePlayBoard()
	 * @see Thread
	 */
	public void startAnalysis()
	{
		new AnalyzeAndMove(true).start();
		new AIPerformanceThread().start();
	}
	
	/**
	 * Starts the <Code>AnalyzeAndMove</Code> <Code>Thread</Code> in order to start the next move.
	 * @see AnalyzeAndMove
	 * @see #nextMove()
	 * @see Thread
	 */
	public void startNextMove()
	{
		new AnalyzeAndMove(false).start();
	}
	
	/**
	 * This method is called to send the current assignment of the play board to the <Code>GUI</Code>
	 * @see GUI_Playboard
	 */
	public void sendPlayboard()
	{
		this.gui_handler.updatePlayboard(this.playboard.updateGUI());
		this.updateGameStatus();
	}
	
	/**
	 * Updates the <Code>GUI_GameStatus</Code> with the current assignments
	 * game_status[0] = own placed
	 * game_status[1] =  own left
	 * game_status[2] =  own lost
	 * game_status[3] =  own on board
	 * game_status[4] = opp placed
	 * game_status[5] = opp left
	 * game_status[6] = opp lost
	 * game_status[7] = opp on board
	 */
	public void updateGameStatus()
	{
		int[] pile = playboard.getPile();
		int[] game_status = new int[8];
		if(gui_handler.getOwnColor())
		{
			game_status[0] = 9 - pile[0];
			game_status[1] = pile[0];
			game_status[2] = pile[3];
			game_status[3] = 9 - pile[0] - pile[3];
			game_status[4] = 9 - pile[2];
			game_status[5] = pile[2];
			game_status[6] = pile[1];
			game_status[7] = 9 - pile[1] - pile[2];
		}
		else
		{
			game_status[4] = 9 - pile[0];
			game_status[5] = pile[0];
			game_status[6] = pile[3];
			game_status[7] = 9 - pile[0] - pile[3];
			game_status[0] = 9 - pile[2];
			game_status[1] = pile[2];
			game_status[2] = pile[1];
			game_status[3] = 9 - pile[1] - pile[2];
		}
		this.gui_handler.updateGameStatus(game_status);
	}
	
	/**
	 * This method is called to send the current image taken from NAO's camera to the <Code>GUI</Code>
	 * @param img the image to be shown on the <Code>GUI</Code>
	 * @see GUI_Camera
	 * @see BufferedImage
	 */
	public void sendImage(BufferedImage img)
	{
		this.gui_handler.updateImage(img);
	}
	
	/**
	 * This method tries to establish a connection to NAO
	 * @param ip the IP address to connect with
	 * @param port the port number to connect with
	 */
	public void startConnection(String ip, String port)
	{
		this.session = new Session();
		Future<Void> future = null;
		try 
		{
			future = this.session.connect(ip+":"+port);

			synchronized (future) 
			{
				future.wait(100);
			}
			ALMotion alMotion = new ALMotion(session);
			alMotion.wakeUp();

		} 
		catch (Exception e) 
		{
			System.err.println("WeBots was not opened or establishing session failed for other reasons.");
		}
		if(session.isConnected())
			connected = true;
		else connected = false;
	}
	
	/**
	 * Creates the AIPerformance object.
	 * @see AIPerformance
	 */
	public void createAIPerformance()
	{
		ai_performance = new AIPerformance(playboard, gui_handler.getOwnColor());
	}
	
	/**
	 * @return a <Code>boolean</Code> value with the status of the connection (true = connected, false otherwise).
	 * @see Controller#connected
	 */
	public boolean getConnection()
	{
		return this.connected;
	}
	
	/**
	 * @return if the current game is paused (true) or not (false)
	 * @see GUI_Handler#getPause()
	 */
	public boolean getPause()
	{
		return this.gui_handler.getPause();
	}
	
	/**
	 * @return the current session
	 * @see Session
	 * @see Controller#session
	 */
	public Session getSession()
	{
		return this.session;
	}
	
	/**
	 * @return the playboard
	 * @see Playboard
	 */
	public Playboard getPlayboard()
	{
		return this.playboard;
	}
	
	/**
	 * This method starts the analysis of the play board.
	 * <p>
	 * If the analysis is finished the user will be asked if the
	 * analyzed play board is correct.
	 * @see NAO_Handler#analyzePlayboard()
	 */
	private void analyzePlayBoard()
	{
		this.gui_handler.changeNextButtonText("Analyzing");
		this.gui_handler.enableNextButton(false);
		this.gui_handler.startMoveCount();
		this.gui_handler.setCurrentExecute(true);
//		Boolean[] currentBoard = nao_handler.analyzePlayboard();
		Boolean[] currentBoard = new Boolean[24];
		currentBoard[0] = false;
		currentBoard[3] = false;
		currentBoard[4] = true;
		currentBoard[11] = false;
		currentBoard[13] = true;
		currentBoard[16] = true;
		currentBoard[22] = true;
		playboard.updateByNAO(currentBoard, !gui_handler.getOwnColor(), false);
		this.gui_handler.changeNextButtonText("Next Move");
		this.gui_handler.enableNextButton(true);
		if(this.gui_handler.confirm(playboard.updateGUI(),playboard.getPile()))
		{
			this.updateGameStatus();
			this.gui_handler.updatePlayboard(playboard.updateGUI());
			System.out.println("Start the next move.");
			new AnalyzeAndMove(false).start();
		}
		else
		{
			this.gui_handler.stopMoveCount(false);
			this.gui_handler.setCurrentExecute(false);
		}
	}
	
	/**
	 * This method starts the next Move of NAO.
	 * <p>
	 * This move consists of two phases.<br>
	 * 1. Updating the <Code>playboard</Code> & calculating the next useful move in the AI,<br>
	 * 2. NAO executing the calculated move.<br>
	 * @see Playboard#update_NAO(Boolean[],boolean,boolean)
	 * @see ComputationalUnit#nextMove(Playboard, boolean, boolean)
	 * @see NAO_Handler#execute(Move)
	 */
	private void nextMove()
	{
		this.gui_handler.enableNextButton(false);
		this.gui_handler.startMoveCount();
		this.gui_handler.changeNextButtonText("Executing");
		this.gui_handler.setCurrentExecute(true);
		Move move;
		try
		{
			System.out.println("Starting AI computation: Evaluating possible moves");
			move = ai.nextMove(playboard, gui_handler.getOwnColor());
			System.out.println("Computation completed");
			playboard.updateByAI(move, false);
			this.sendPlayboard();
		}
		catch(foundation.exception.GameWonException gwe)
		{
			playboard.updateByAI(gwe.getMove(), false);
			this.sendPlayboard();
			nao_handler.execute(gwe.getMove());
			this.gui_handler.setGameResultNAO(true);
			this.gui_handler.setGameResultEnemy(false);
			this.gui_handler.stopMoveCount(true);
			this.gui_handler.stopGameCount();
			return;
		}
		catch(foundation.exception.GameLostException gle)
		{
			this.gui_handler.setGameResultNAO(false);
			this.gui_handler.setGameResultEnemy(true);
			this.gui_handler.stopMoveCount(true);
			this.gui_handler.stopGameCount();
			return;
		}
		catch(foundation.exception.GameDrawException gde)
		{
			this.gui_handler.setGameResultNAO(null);
			this.gui_handler.setGameResultEnemy(null);
			this.gui_handler.stopMoveCount(true);
			this.gui_handler.stopGameCount();
			return;
		}
//		nao_handler.execute(move);
		this.gui_handler.stopMoveCount(true);
		this.gui_handler.setCurrentExecute(false);
		this.gui_handler.changeNextButtonText("Analyze");
		this.gui_handler.enableNextButton(true);
	}
	
	/**
	 * Calls the <Code>stopMove</Code> method of the <Code>NAO_Handler</Code> in order to stop the current move of NAO
	 * @see NAO_Handler#stopMove()
	 */
	public void stopMove()
	{
		this.nao_handler.stopMove();
	}
	
	public void startPerformanceMonitorThread()
	{
		new PerformanceMonitorThread().start();
	}
	
	/**
	 * This class constantly updates the UI with a new Image of NAO's camera every 300ms.
	 * @author Nick Weber
	 * @version 2.0
	 * @see GUI_Handler#updateImage(BufferedImage)
	 */
	private class CameraStream extends Thread
	{
		@Override
		public void run()
		{
			while(true)
			{
				try 
				{
					Thread.sleep(2000);
					while(!gui_handler.getPause())
					{
							gui_handler.updateImage(nao_handler.getHead().getCameraStream(gui_handler.getCamera()));
							Thread.sleep(300);
					}
				}
				catch(InterruptedException ex)
				{
					ex.printStackTrace();
				}
				catch(Exception ex) 
				{
					if(ex instanceof java.lang.NullPointerException)
					{
						System.err.println("java.lang.NullPointerException: Camera stream not found.");
					}
					else ex.printStackTrace();
				}
			} 
			
		}
	}
	
	/**
	 * This class is responsible for calling the <Code>analyzePlayboard</Code> or <Code>nextMove</Code> method in the <Code>Controller</Code>
	 * @author Nick Weber
	 * @version 2.0
	 * @see Controller
	 * @see Controller#analyzePlayBoard()
	 * @see Controller#nextMove()
	 */
	private class AnalyzeAndMove extends Thread
	{
		/**
		 * This <Code>boolean</Code> will determine either if the run method starts the analysis of the play board or the next move.
		 * <p>
		 * true for analysis
		 * false for next move
		 */
		boolean analyzeOrMove;
		
		AnalyzeAndMove(boolean analyzeOrMove)
		{
			this.analyzeOrMove = analyzeOrMove;
		}
		
		/**
		 * Starts the analysis of the play board or the next move.
		 * @see Thread#run()
		 */
		@Override
		public void run()
		{
			if(analyzeOrMove)
			{
				analyzePlayBoard();
			}
			else nextMove();
		}
	}
	
	/**
	 * Starts the <Code>AIPerformance</Code> <Code>Thread</Code>
	 * @author Nick Weber, Mike Siefert
	 * @version 2.0
	 * @see AIPerformance
	 * @see Thread
	 */
	private class AIPerformanceThread extends Thread
	{	private int newDepth=2;
		@Override
		public void run()
		{	ai_performance.updatePlayboard(playboard);
			newDepth = ai_performance.getAIDepth();
			ai.setMaxDepth(newDepth);
			System.out.println("New AI Depth was successfully set up to " + newDepth + ".");
		}
	}
	
	/**
	 * A <Code>Thread</Code> that constantly updates a <Code>JLabel</Code> with the current memory & cpu performance
	 * @author Nick Weber
	 * @version 2.0
	 * @see Thread
	 * @see JLabel
	 */
	private class PerformanceMonitorThread extends Thread
	{
		@Override
		public void run()
		{
//			while(true)
//			{
//				gui_handler.setPerformanceLabel("<html>" + performance_management.getMemoryUsage() + "<br>" + performance_management.getCpuPerformance()+"</html>");
//				try
//				{
//					Thread.sleep(1000);
//				}
//				catch(InterruptedException ex)
//				{
//					ex.printStackTrace();
//				}
//			}
		}
	}
}
