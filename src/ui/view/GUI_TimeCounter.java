package ui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The purpose of this class is to count the elapsed time
 * since the Nine Men's Morris game started and count the 
 * elapsed time of our current move as well.
 * 
 * @author Nick Weber
 * @version 2.0
 */
public class GUI_TimeCounter extends JPanel
{
	/**
	 * A <Code>Timer</Code> object that is constantly sending an <Code>ActionEvent</Code> within a given delay
	 * @see Timer
	 */
	private Timer timer;
	/**
	 * Contains the type of counter as <Code>String</Code>, either a counter for the game duration or move duration
	 */
	private String counterType;
	/**
	 * The <Code>JLabel</Code> containing the counter
	 */
	private JLabel counter;
	private static final long serialVersionUID = 1l;
	/**
	 * Help attributes in order to reset the move time counter
	 */
	private int sec, min;
	
	/**
	 * Constructs a new counter.
	 * @param counterType what type of counter (what the counter counts)
	 * @param delay within which delay the counter shall send an <Code>ActionEvent</Code>
	 * @param listener the <Code>ActionListener</Code> which handles the send <Code>ActionEvent</Code> of the counter
	 */
	GUI_TimeCounter(String counterType, int delay, ActionListener listener)
	{
		timer = new Timer(delay, listener);
		this.setLayout(new GridLayout(1,2,5,5));
		this.setBackground(Color.WHITE);
		this.counterType = counterType;
		counter = new JLabel(counterType + " Duration:   0:00");
		counter.setFont(new Font("Verdana",Font.PLAIN,13));
		
		this.add(counter);
		if(counterType.equals("Game"))
		{
			timer.start();
		}
	}
	
	/**
	 * This method updates the <Code>JLabel</Code> with the passed minutes and seconds.
	 * @param minutes the amount of past minutes since this counter got started
	 * @param seconds the amount of past seconds since this counter got started
	 */
	void setCounter(int minutes, int seconds)
	{
		if(seconds < 10)
		{
			this.counter.setText(this.counterType + " Duration:   " + minutes + ":0" + seconds);
			this.counter.repaint();
		}
		else
		{
			this.counter.setText(this.counterType + " Duration:   " + minutes + ":" + seconds);
			this.counter.repaint();
		}
	}
	
	/**
	 * @return the timer
	 * @see Timer
	 */
	Timer getTimer()
	{
		return this.timer;
	}
	
	/**
	 * @return the <Code>sec</Code> attribute
	 */
	int getSec()
	{
		return this.sec;
	}
	
	/**
	 * @return the <Code>min</Code> attribute
	 */
	int getMin()
	{
		return this.min;
	}
	
	/**
	 * 
	 * @param sec sets the <Code>sec</Code> attribute
	 */
	void setSec(int sec)
	{
		this.sec = sec;
	}
	
	/**
	 * @param min sets the <Code>min</Code> attribute
	 */
	void setMin(int min)
	{
		this.min = min;
	}
}
