package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class MapPanel extends JPanel 
{
	private BufferedImage image;
	
	public MapPanel(BufferedImage image) 
	{
		this.image = image;
	}

	public void paintComponent(Graphics graphics) 
	{
		graphics.drawImage(image, 0, 0, null);
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
}
