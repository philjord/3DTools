package tools3d.mixed3d2d.curvehud.elements;

import java.awt.Image;
import java.awt.Color;

import awt.tools.image.SimpleImageLoader;
import awt.tools3d.mixed3d2d.hud.HUDElement;
import awt.tools3d.mixed3d2d.hud.HUDElementContainer;
import tools3d.mixed3d2d.Canvas3D2D;

public class HUDImage extends HUDElementContainer
{
	private HUDElement imageElement;

	private int width = 0;

	private int height = 0;

	public HUDImage(Canvas3D2D canvas, String imageName)
	{
		this(imageName);
		addToCanvas(canvas);
	}

	public HUDImage(String imageName, int w, int h)
	{
		this(SimpleImageLoader.getImage(imageName), w, h);
	}

	public HUDImage(Image im, int w, int h)
	{
		if (im != null)
		{
			width = w;
			height = h;

			imageElement = new HUDElement(width, height);
			// setLocation(canvas);
			imageElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.2f));
			// crossHairElement.getGraphics().fillRoundRect(0, 0, 50, 50, 15, 15);
			imageElement.getGraphics().drawImage(im, 0, 0, width, height, null);
			add(imageElement);
		}
		else
		{
			new Throwable("image is null " + im).printStackTrace();
		}
	}

	public HUDImage(String imageName)
	{
		this(SimpleImageLoader.getImage(imageName));
	}

	public HUDImage(Image im)
	{
		if (im != null)
		{
			width = im.getWidth(null);
			height = im.getHeight(null);

			imageElement = new HUDElement(width, height);
			// setLocation(canvas);
			imageElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.2f));
			// crossHairElement.getGraphics().fillRoundRect(0, 0, 50, 50, 15, 15);
			imageElement.getGraphics().drawImage(im, 0, 0, null);
			add(imageElement);
		}
		else
		{
			new Throwable("image is null " + im).printStackTrace();
		}
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		if (canvas != null)
			canvas.addElement(imageElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		if (canvas != null)
			canvas.removeElement(imageElement);
	}

}
