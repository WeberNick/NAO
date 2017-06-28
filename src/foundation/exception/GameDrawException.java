package foundation.exception;

/**
 * <p>Indicates that the game ended in a draw.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class GameDrawException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>Constructs a new exception with the specified detail message.</p>
	 * @param message the message to store
	 */
	public GameDrawException(String message) {
		super(message);
	}
}
