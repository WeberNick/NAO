package ui.view;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The class which handles the camera stream of the robot NAO to the <Code>GUI_Components</Code> class.
 * @author Nick Weber
 * @version 2.0
 * @see GUI_Components
 */
public class GUI_Camera extends JPanel
{
	/**
	 * The <Code>JLabel</Code> in which the camera stream is displayed.
	 * @see JLabel
	 */
	private JLabel cam_label;
	private static final long serialVersionUID = 1l;
	
	GUI_Camera()
	{
		this.setLayout(null);
		this.setPreferredSize(new Dimension(176,100));
		cam_label = new JLabel();
		cam_label.setBounds(3,3,170,94);
		cam_label.setBackground(Color.WHITE);
		cam_label.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
		this.add(cam_label);
	}
	
	/**
	 * @return the cam_label in which the camera stream is displayed.
	 */
	JLabel getCamLabel()
	{
		return this.cam_label;
	}
	
	/**
	 * This method updates the current image displayed on the <Code>GUI</Code>
	 * @param img the image to be shown on the <Code>GUI</Code>
	 * @see GUI
	 * @see #repaint()
	 */
	void updateImage(BufferedImage img)
	{
		cam_label.setIcon(new ImageIcon(img));
		this.repaint();
	}
	
	/**
	 * This method resizes a given <Code>BufferedImage</Code> image to a new given width and height
	 * @param img the image to resize
	 * @param newW the new width
	 * @param newH the new height
	 * @return dimg the scaled image <Code>img</Code>
	 * @see BufferedImage
	 */
	static BufferedImage resize(BufferedImage img, int newW, int newH) 
	{ 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	} 
}
