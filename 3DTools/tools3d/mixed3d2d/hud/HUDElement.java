package tools3d.mixed3d2d.hud;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class HUDElement extends HUDElementContainer
{
	private BufferedImage bufferedImage;
 

	private Graphics2D graphics;
	
 
	private int width = -1;

	private int height = -1;

	public HUDElement(int width, int height)
	{
		setSize(width, height);
	}

	public void setSize(int newWidth, int newHeight)
	{
		if (width != newWidth || height != newHeight)
		{
			width = newWidth == 0 ? 1 : newWidth;
			height = newHeight == 0 ? 1 : newHeight;
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			graphics = bufferedImage.createGraphics();
		}
	}

	public void clear()
	{
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = bufferedImage.createGraphics();
	}

	public Graphics2D getGraphics()
	{
		return graphics;
	}

	public BufferedImage getBufferedImage()
	{
		return bufferedImage;
	}

}
