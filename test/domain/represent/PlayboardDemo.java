package domain.represent;

import java.util.ArrayList;
import foundation.data.Move;
import application.control.Controller;
/**
 * this was a demo to show the implemened functionality in the itermediate colloquium.
 * it has no real use but providing a sequence of moves for some other test classes and
 * is an example of what was done during writing the code
 * @author Jonas
 *
 */
public class PlayboardDemo extends Thread {
//	private Controller controller;
	private Playboard board;
	private ArrayList<Move> alm;

	public PlayboardDemo(Controller c, Playboard b) {
		this();
//		controller = c;
		board = b;
		board.testSetter("empty");
		alm = createMoves();
		
	}
	public PlayboardDemo(){
		board = new Playboard();
		alm = createMoves();
	}
	public ArrayList<Move> createMoves(){
		board = new Playboard();
		alm = new ArrayList<Move>();
		alm.add(new Move(false, true, board.getField(24), true, true, board
				.getField(1)));
		alm.add(new Move(false, false, board.getField(26), true, false, board
				.getField(22)));
		alm.add(new Move(false, true, board.getField(24), true, true, board
				.getField(14)));
		alm.add(new Move(false, false, board.getField(26), true, false, board
				.getField(10)));
		alm.add(new Move(false, true, board.getField(24), true, true, board
				.getField(2)));
		alm.add(new Move(false, false, board.getField(26), true, false, board
				.getField(21)));
		alm.add(new Move(false, true, board.getField(24), true, true, board
				.getField(23), false, false, board.getField(22), true, false,
				board.getField(25)));
		alm.add(new Move(false, false, board.getField(26), true, false, board
				.getField(3)));
		alm.add(new Move(false, true, board.getField(24), true, true, board
				.getField(13)));
		alm.add(new Move(false, false, board.getField(26), true, false, board
				.getField(18), false, true, board.getField(1), true, true,
				board.getField(27)));
		alm.add(new Move(false, true, board.getField(24), true, true, board
				.getField(12), false, false, board.getField(21), true, false,
				board.getField(25)));
		return alm;
	}
/**
 * @deprecated
 */
	public void run(){
/*		try
		{
			int i = 0;
			int [] men = new int[2];
			men[0]=0; //white set
			men[1]=0; //black set
			board.testSetter("empty");
			while(true)
			{
				Thread.sleep(5000);
				while(!(this.controller.getGUI_Handler().getPause()))
				{
					if(i<alm.size())
					{
						Thread.sleep(1000);
						board.updateByAI(alm.get(i), false);
						
						controller.sendPlayboard(board.updateGUI());
						if(board.allMenOnBoard(true, false)>men[0])
							controller.informMenPlaced(true);
						if (board.allMenOnBoard(true, false)<men[0])
							controller.informMenLost(true);
						if(board.allMenOnBoard(false, false)>men[1])
							controller.informMenPlaced(false);
						if(board.allMenOnBoard(false, false)<men[1])
							controller.informMenLost(false);
						men[0]=board.allMenOnBoard(true, false);
						men[1]=board.allMenOnBoard(false, false);	
						i++;
						Thread.sleep(1500);	
					}
					else
					{
						controller.reset();
						Thread.sleep(500);
						board.testSetter("empty");
						i=0;
						men[0]=0;
						men[1]=0;
						Thread.sleep(500);
					}
				}
		}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}*/
	}
}
