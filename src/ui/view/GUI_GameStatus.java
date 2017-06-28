package ui.view;

import javax.swing.*;
import java.awt.*;

/**
 * The purpose of this class is to present the current
 * status of the game and update the <Code>GUI</Code> as soon
 * the <Code>Controller</Code> sends relevant information.
 * 
 * @author Nick Weber
 * @version 2.0
 * @see GUI
 * @see application.control.Controller
 */
public class GUI_GameStatus extends JPanel
{
	/**
	 * The <Code>String</Code> representation of the color of the associated game status
	 */
	private String color;
	/**
	 * The <Code>JLabel</Code> representing the current game status informations
	 * @see JLabel
	 */
	private JLabel status;
	/**
	 * The <Code>JLabel</Code> representing the game result after the game ended
	 * @see JLabel
	 */
	private JLabel gameResult;
	private static final long serialVersionUID = 1l;
	
	/**
	 * With the initialization of a <Code>GUI_GameStatus</Code> object, the components
	 * presenting the current game status are created.
	 * @param statusOf assigns a name to the game status to distinguish them
	 * @param ownColor assigns a color to the game status (true = blue, false = red)
	 */
	GUI_GameStatus(String statusOf, boolean ownColor)
	{
		this.setBackground(Color.WHITE);
		
		JLabel pic;
		if(ownColor)
		{
			this.color = "Blue";
			pic = new JLabel(new ImageIcon(getClass().getResource("/BlueNao.png")));
		}
		else
		{
			this.color = "Red";
			pic = new JLabel(new ImageIcon(getClass().getResource("/RedNao.png")));
		}
		this.setLayout(null);
		this.setPreferredSize(new Dimension(120,200));
		
		JLabel player = new JLabel("<html><div style='text-align: center;'>"+statusOf+"</html>");
		player.setFont(new Font("Verdana",Font.BOLD,20));
		status = new JLabel("<html>"+"Color: <b>"+this.color+"</b><br>"+"0/9 Men Placed"+"<br>"+"9/9 Men Left"+"<br>"+"0/9 Men Lost"+"<br>"+"0 Men on Board"+"</html>");
		gameResult = new JLabel();
		if(statusOf.equals("NAO"))
		{
			player.setBounds(30, 0, 60, 40);
		}
		else
		{
			player.setBounds(0, 0, 110, 40);
		}
		status.setBounds(10, 40, 120, 80);
		gameResult.setBounds(10, 120, 120, 40);
		pic.setBounds(0, 240, 120, 227);
		this.add(player);
		this.add(status);
		this.add(gameResult);
		this.add(pic);
	}
	
	/**
	 * @return the <Code>JLabel</Code> containing the <Code>String</Code> representation of the game result
	 */
	JLabel getGameResultLabel()
	{
		return this.gameResult;
	}
	
	/**
	 * Sets the game result in the <Code>JLabel</Code> 
	 * @param result a <Code>Boolean</Code> representation of the game result<br>
	 * <Code>null</Code> if a draw occurred<br>
	 * <Code>true</Code> for a win<br>
	 * <Code>false</Code> for a defeat<br>
	 */
	void setGameResultLabel(Boolean result)
	{
		if(result == null)
		{
			gameResult.setForeground(Color.YELLOW);
			gameResult.setText("<html><div style='text-align: center;'>DRAW</html>");
			gameResult.setFont(new Font("Verdana",Font.BOLD,20));
		}
		else if(result == true)
		{
			gameResult.setForeground(Color.GREEN);
			gameResult.setText("<html><div style='text-align: center;'>WINNER</html>");
			gameResult.setFont(new Font("Verdana",Font.BOLD,20));
		}
		else
		{
			gameResult.setForeground(Color.RED);
			gameResult.setText("<html><div style='text-align: center;'>LOSER</html>");
			gameResult.setFont(new Font("Verdana",Font.BOLD,20));
		}
		gameResult.repaint();
	}
	
	/**
	 * This method sets the text on the status label and repaints the label.
	 * @see #repaint()
	 */
	void updateStatus(int[] assignment)
	{
		status.setText("<html>"+"Color: <b>"+this.color+"</b><br>"+assignment[0]+"/9 Men Placed"+"<br>"+assignment[1]+"/9 Men Left"+"<br>"+assignment[2]+"/9 Men Lost"+"<br>"+assignment[3]+" Men on Board"+"</html>");
		status.repaint();
	}
}
