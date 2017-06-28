package application.ai;


import domain.represent.Playboard;
import foundation.exception.GameDrawException;
import foundation.exception.GameLostException;
import foundation.exception.GameWonException;
/**
 * <p>This class optimizes the artificial intelligence of NAO. During the game the AI max depth is dynamic. 
 * AIPerformance calculates different depth of the AI and chooses the one,
 * that deliver a move closest to 30 seconds.</p> 
 * <p>After each placed man the possibilities will decreases, that means 
 * the AI can use the memory resources efficiently.</p> 
 * 
 * @author Mike Siefert
 * @version 1.0
 * @see ComputationalUnit
 */
public class AIPerformance {
	private Playboard playboard;
	private final long MAXTIME;
	private final long MINTIME;
	private int optimizedDepth;
	private final boolean color;
	
	
	public AIPerformance(Playboard pb, boolean color){
		playboard = pb;
		MAXTIME = 30000000000l;
		MINTIME =  1000000000l;
		optimizedDepth = 2;
		this.color = color;
	}

	
	/**
	 * <p>This method calculates a new AI depth,which calculates the next move within 30 seconds.</p>
	 * @return new AI depth
	 */
	public int getAIDepth(){
		boolean run;
		do{
			if(optimizedDepth<2) 
				return 2;
			run = evaluateDepth(getTime(optimizedDepth));
		}while(run);
		
		return optimizedDepth;
	}
	/**
	 * <p>A simple heuristic, that uses the duration of an calculated move to decide the next optimized AI depth.</p>
	 * @param duration in nano seconds
	 * @return termination condition of getAIDepth
	 */
	
	private boolean evaluateDepth(long duration){
		if(duration < MINTIME){
			optimizedDepth+=4;
			return true;
		}else if(duration > MAXTIME){
			optimizedDepth-=1;
			return false;
		}else{
			optimizedDepth+=1;
			return true;
		}
	}

	/**
	 * <p>Calculates a move with the given AI depth and returns the duration.</p>
	 * @param depth shall be increased carefully, because AI algorithm has an exponential complexity.
	 * @return duration in nano seconds.
	 */
	
	private long getTime (int depth){
		ComputationalUnit ai = new ComputationalUnit();
		long timeParam1, timeParam2,durationTime;
		ai.setMaxDepth(depth);
	
		timeParam1 = System.nanoTime();
		nextMoveSimulation(ai);
		timeParam2 = System.nanoTime();
		durationTime = timeParam2 - timeParam1;
		
		return durationTime;
	}
	/**
	 * <p>Updates the playboard with the current game situation.</p>
	 */
	
	public void updatePlayboard(Playboard playboard){
		this.playboard = playboard.deepCopy();
	}
	
	/**
	 * <p>Simulates the next move with the current playboard.</p>
	 */
	
	private void nextMoveSimulation(ComputationalUnit ai) {
		Playboard board = playboard;
			
			try {
				board.updateByAI(ai.nextMove(board, !color), false);
			}
			catch (GameWonException exc) {
				board.updateByAI(exc.getMove(), false);
				return;
			}
			catch (GameDrawException exc) {
				return;
			}
			catch (GameLostException exc) {
				return;
			}
	}
}