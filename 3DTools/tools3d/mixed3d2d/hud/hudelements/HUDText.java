package tools3d.mixed3d2d.hud.hudelements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.mixed3d2d.hud.HUDElement;
import tools3d.mixed3d2d.hud.HUDElementContainer;

public class HUDText extends HUDElementContainer
{
	private HUDElement textElement;

	private HUDElement textShadowElement;

	private Color textColor = new Color(0.9f, 0.9f, 0.9f, 1f);

	private Color textGreyColor = textColor.darker();

	private Color textShadowColor = new Color(0.3f, 0.3f, 0.4f, 0.5f);

	private Font textFont;

	private Canvas3D2D currentCanvas;

	public HUDText(Canvas3D2D canvas3d, Rectangle rectangle)
	{
		this(canvas3d, rectangle, 24);
	}

	public HUDText(Canvas3D2D canvas3d, Rectangle rectangle, int fontSize)
	{
		textFont = new Font("Arial", Font.PLAIN, fontSize);

		textElement = new HUDElement(rectangle.width, rectangle.height);
		textElement.setLocation(rectangle.x, rectangle.y);
		add(textElement);

		textShadowElement = new HUDElement(rectangle.width, rectangle.height);
		textShadowElement.setLocation(rectangle.x + 1, rectangle.y + 1);
		add(textShadowElement);

		addToCanvas(canvas3d);
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		if (currentCanvas != null)
		{
			removeFromCanvas();
		}
		currentCanvas = canvas;
		canvas.addElement(textElement);
		canvas.addElement(textShadowElement);
	}

	public void removeFromCanvas()
	{
		if (currentCanvas != null)
		{
			currentCanvas.removeElement(textElement);
			currentCanvas.removeElement(textShadowElement);
			currentCanvas = null;
		}
	}

	public void setText(String newText)
	{
		textElement.clear();
		textElement.getGraphics().setColor(textColor);
		textElement.getGraphics().setFont(textFont);
		//TODO: must centralise the text
		textElement.getGraphics().drawString(newText, 0, 35);

		textShadowElement.clear();
		textShadowElement.getGraphics().setColor(textShadowColor);
		textShadowElement.getGraphics().setFont(textFont);
		textShadowElement.getGraphics().drawString(newText, 0, 35);
	}

	public void setLocation(int x, int y)
	{
		textElement.setLocation(x, y);
		textShadowElement.setLocation(x + 1, y + 1);
	}

	public void setTextGreyed(String newText)
	{
		textElement.clear();
		textElement.getGraphics().setColor(textGreyColor);
		textElement.getGraphics().setFont(textFont);
		textElement.getGraphics().drawString(newText, 0, 35);

		textShadowElement.clear();

	}
}
