package foundation.exception;

/**
 * if a move is not in the right order and therefore would lead to NullPointerExcetions in methods
 * processing it, it will throw an Exception.
 * on top, it was also thrown by the strict version of updateByNAO if an unvalid move was conducted.
 * @author Jonas
 *
 */
public class InvalidMoveException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
public InvalidMoveException(String message){
	super(message);
}
}
