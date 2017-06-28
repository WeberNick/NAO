package ui.view;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
 
/**
 * The purpose of this class is to write the standard output stream -which would appear in the console- into a <Code>JTextArea</Code>.
 * <p>
 * With this, the user has the possibility to observe <Code>Exception</Code> messages and the current state of NAO's calculations.
 * @author Nick Weber
 * @version 2.0
 * @see JTextArea
 * @see Exception
 */
public class CustomOutputStream extends OutputStream 
{
	/**
	 * The <Code>JTextArea</Code> to write the <Code>OutputStream</Code> into.
	 * @see JTextArea
	 * @see OutputStream
	 */
    private JTextArea textArea;
     
    public CustomOutputStream(JTextArea textArea) 
    {
        this.textArea = textArea;
    }
     
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException 
    {
        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}