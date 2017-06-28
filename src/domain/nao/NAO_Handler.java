package domain.nao;

import com.aldebaran.qi.Session;
import application.control.Controller;
import foundation.data.Move;

public class NAO_Handler 
{
	private Head_Functionality head_Functionality;
	private Controller controller;
	private Gaming_Functionality game;
	
	public NAO_Handler(Controller controller)
	{
		this.controller = controller;
		head_Functionality = new Head_Functionality(this);
		game = new Gaming_Functionality(this);

	}
	/**	Gets the analyzed play board
	 * @return Boolean[] Array of play board
	 */
	public Boolean[] analyzePlayboard()
	{
		try {
			return game.analyzePlayboard();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Read in null");
			return null;
			
		}
	}
	
	/**
	 * This method will execute a given move physically.
	 * @param move
	 */
	public void execute(Move move)
	{
		game.executeMove(move);
	}
	
	public Head_Functionality getHead()
	{
		return this.head_Functionality;
	}
	
	Session getSession()
	{
		return controller.getSession();
	}
	
	public boolean getPause(){
		
		return controller.getPause();
		
	}
	
	/**
	 * Will stop Nao's move/walk
	 */
	public void stopMove(){
		
		game.body.leg_Functionality.stopMove();
	}
}
