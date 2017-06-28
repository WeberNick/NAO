package foundation.exception;

/**
 * <p>Indicates that the game was lost.</p>
 * @author Julian Betz
 * @version 1.01
 */
public class GameLostException extends Exception {
	private static final long serialVersionUID = 1L;
	private final boolean color;
	
	/**
	 * <p>Constructs a new exception with the specified detail message and the specified color.</p>
	 * @param message the message to store
	 * @param color the color of the losing player's men
	 */
	public GameLostException(String message, boolean color) {
		super(message);
		this.color = color;
	}
	
	public boolean getColor() {
		return color;
	}
}
