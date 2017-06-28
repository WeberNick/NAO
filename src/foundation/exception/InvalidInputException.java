package foundation.exception;

/**
 * Indicates an invalid input.
 * @author Nick Weber
 * @version 1.0
 */
public class InvalidInputException extends Exception
{
	private static final long serialVersionUID = 1l;
	
	public InvalidInputException(String info)
	{
		super(info);
	}
}