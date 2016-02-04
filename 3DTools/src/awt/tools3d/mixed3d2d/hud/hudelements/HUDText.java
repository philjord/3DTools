package awt.tools3d.mixed3d2d.hud.hudelements;

import java.awt.Font;
import java.awt.Color;
import java.awt.Rectangle;

import awt.tools3d.mixed3d2d.hud.HUDElement;
import awt.tools3d.mixed3d2d.hud.HUDElementContainer;
import tools3d.mixed3d2d.Canvas3D2D;

public class HUDText extends HUDElementContainer
{

	private boolean alignHCenter = true;

	private HUDElement textElement;

	private HUDElement textShadowElement;

	private Color textColor = new Color(1.0f, 1.0f, 0.8f, 1f);

	private Color textGreyColor = textColor.darker();

	private Color textShadowColor = new Color(0.3f, 0.3f, 0.2f, 0.7f);

	private Font textFont;

	private Font greyTextFont;

	private Canvas3D2D currentCanvas;

	private Rectangle rectangle;

	public HUDText(Canvas3D2D canvas3d, Rectangle rectangle)
	{
		this(canvas3d, rectangle, 36);
	}

	public HUDText(Canvas3D2D canvas3d, Rectangle rectangle, int fontSize)
	{
		this(canvas3d, rectangle, fontSize, true);
	}

	public HUDText(Canvas3D2D canvas3d, Rectangle rectangle, int fontSize, boolean alignHCenter)
	{
		this.alignHCenter = alignHCenter;
		this.rectangle = rectangle;
		textFont = new Font("Arial", Font.BOLD, fontSize);
		greyTextFont = new Font("Arial", Font.BOLD | Font.ITALIC, fontSize);

		textShadowElement = new HUDElement(rectangle.width, rectangle.height);
		textShadowElement.setLocation(rectangle.x + 1, rectangle.y + 1);
		add(textShadowElement);

		textElement = new HUDElement(rectangle.width, rectangle.height);
		textElement.setLocation(rectangle.x, rectangle.y);
		add(textElement);

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

	public void setLocation(int x, int y)
	{
		textElement.setLocation(x, y);
		textShadowElement.setLocation(x + 1, y + 1);
	}

	public void setText(String newText)
	{

		int hOffset = 0;
		if (alignHCenter)
		{
			int sw = textElement.getGraphics().getFontMetrics(textFont).stringWidth(newText);
			hOffset = (rectangle.width / 2) - (sw / 2);
		}

		textShadowElement.clear();
		textShadowElement.getGraphics().setColor(textShadowColor);
		textShadowElement.getGraphics().setFont(textFont);
		textShadowElement.getGraphics().drawString(newText, hOffset, 35);

		textElement.clear();
		textElement.getGraphics().setColor(textColor);
		textElement.getGraphics().setFont(textFont);

		textElement.getGraphics().drawString(newText, hOffset, 35);

		//debug outliner
		//textElement.getGraphics().drawRect(0, 0, rectangle.width-1, rectangle.height-1); 

	}

	public void setTextGreyed(String newText)
	{
		int hOffset = 0;
		if (alignHCenter)
		{
			int sw = textElement.getGraphics().getFontMetrics(textFont).stringWidth(newText);
			hOffset = (rectangle.width / 2) - (sw / 2);
		}

		textElement.clear();
		textElement.getGraphics().setColor(textGreyColor);
		textElement.getGraphics().setFont(greyTextFont);
		textElement.getGraphics().drawString(newText, hOffset, 35);

		textShadowElement.clear();

	}
}
