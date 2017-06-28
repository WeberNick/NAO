package ui.view;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The purpose of this class is to create the visual representation of the gaming pieces on the board.
 * 
 * @author Nick Weber
 * @version 2.0
 */
public class GUI_Playboard extends JPanel
{
	/**
	 * The constant for the width (pixels) of the tokens to draw on the play board
	 * @see #redraw(BufferedImage)
	 */
	private static final int WIDTH = 50;
	/**
	 * The constant for the height (pixels) of the tokens to draw on the play board
	 * @see #redraw(BufferedImage)
	 */
	private static final int HEIGHT = 50;
	/**
	 * The x-coordinates of the fields of the play board within the <code>JPanel</code>. From bottom to top, from left to right
	 * @see #redraw(BufferedImage)
	 */
	private static int[] x = {15,270,530,100,270,445,185,270,360,15,100,185,360,445,530,185,270,360,100,270,445,15,270,530};
	/**
	 * The y-coordinates of the fields of the play board within the <code>JPanel</code>. From bottom to top, from left to right
	 * @see #redraw(BufferedImage)
	 */
	private static int[] y = {530,530,530,445,445,445,360,360,360,275,275,275,275,275,275,185,185,185,100,100,100,15,15,15};
	/**
	 * The three possible colors to draw the men on the board: blue, red or transparent
	 * @see #redraw(BufferedImage)
	 */
	private static Color[] color = {new Color(5, 35, 140), new Color(200,0,35), new Color(0,0,0,0)};
	/**
	 * This object saves the representation of the play board without any men on it
	 */
	private BufferedImage board;
	/**
	 * This objects saves the representation of the play board with the currently placed men on it
	 * @see #redraw(BufferedImage)
	 * @see #paintComponent(Graphics)
	 */
	private BufferedImage toDraw;
	/**
	 * This Boolean array represents which color is on the playboard's 24 fields.
	 * <p>
	 * The play board's fields are numbered from 0 to 23, from bottom to top, from left to right.<br>
	 * 0 for bottom left, 23 for top right.<br>
	 * An array position represents the men's color on the position:<br>
	 * *null = transparent<br>
	 * *true = blue<br>
	 * *false = red
	 */
	private Boolean[] colorsMenOnBoard;		
	private static final long serialVersionUID = 1l;
	
	GUI_Playboard()
	{
		this.setBackground(Color.WHITE);
		try 
		{
			this.board = ImageIO.read(getClass().getResourceAsStream("/NMM.png"));
		}catch(IOException e){
			e.printStackTrace();
		}
		this.toDraw = board;
		colorsMenOnBoard = new Boolean[24];
	}
	
	/**
	 * The purpose of this method is to draw the token placement on the board.
	 * <p>
	 * Every time a token is placed by either NAO or its enemy, this method is
	 * called to redraw the board with the current placement.<br>
	 * The redrawn board image is saved in the <Code>toDraw</Code> attribute.
	 * @param old the play board to draw the placement of the tokens on
	 */
	void redraw(BufferedImage old)
	{
		BufferedImage img = new BufferedImage(old.getWidth(), old.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(old, 0, 0, null);
		for(int i = 0; i < 24; i++) 
		{
			if(colorsMenOnBoard[i] == null)
			{
				g2d.setColor(color[2]);
				g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
			}
			else if(colorsMenOnBoard[i] == true)
			{
				g2d.setColor(color[0]);
				g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
			else
			{
				g2d.setColor(color[1]);
				g2d.fillOval(x[i], y[i], WIDTH, HEIGHT);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		}
		this.toDraw = img;
	}
	
	/**
	 * Getter method to access the image of the play board
	 * @return the BufferedImage of the play board
	 * @see BufferedImage
	 */
	BufferedImage getImage()
	{
		return this.board;
	}
	
	/**
	 * Setter method to set the image of the play board
	 * @param img the image to set
	 * @see BufferedImage
	 */
	void setImage(BufferedImage img)
	{
		this.board = img;
	}
	
	/**
	 * This method overwrites the attribute <Code>colorsMenOnBoard</Code> with the 
	 * <Code>Boolean</Code> array representation of the current assignment on the board
	 * @param colors the <Code>Boolean</Code> array representation of the assignment on the board
	 * @see GUI_Playboard#colorsMenOnBoard
	 */
	void setPlayboard(Boolean[] colors)
	{
		this.colorsMenOnBoard = colors;
		this.redraw(this.getImage());
		this.drawing();
	}
	
	/**
	 * This method calls the repaint method because the repaint
	 * method can not be called outside of this class.
	 * <p>
	 * The repaint method is needed to update the frame and
	 * repaint its components.
	 * @see #repaint()
	 */
	void drawing()
	{
		repaint();
	}
	
	/**
	 * {@inheritDoc}
	 * @param g the Graphics object to draw
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(this.toDraw, 0, 0,460,460,null);
	}
}
