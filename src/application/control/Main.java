package application.control;

/**
 * The class with the main method. The only purpose of this class is to start the <Code>Controller</Code> <Code>Thread</Code>.
 * @author Nick Weber
 * @version 2.0
 * @see Controller
 * @see Thread
 * @see Controller#start()
 */
public class Main 
{
	public static void main(String[] args) 
	{
		new Controller();
	}
}