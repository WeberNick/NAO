package foundation.exception;

import foundation.data.Move;

/**
 * <p>Indicates that the game was won.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class GameWonException extends Exception {
	private static final long serialVersionUID = 1L;
	private Move move;
	
	/**
	 * <p>Constructs a new exception with the specified detail message and the specified move.</p>
	 * @param message the message to store
	 * @param move the move to execute to win the game
	 */
	public GameWonException(String message, Move move) {
		super(message);
		this.move = move;
	}
	
	public Move getMove() {
		return move;
	}
}
