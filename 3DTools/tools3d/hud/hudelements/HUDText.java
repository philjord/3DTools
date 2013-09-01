package tools3d.hud.hudelements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUDElement;
import tools3d.hud.HUDElementContainer;

public class HUDText extends HUDElementContainer
{
	private HUDElement textElement;

	private HUDElement textShadowElement;

	private Color textColor = new Color(0.9f, 0.9f, 0.9f, 1f);

	private Color textShadowColor = new Color(0.3f, 0.3f, 0.4f, 0.5f);

	private Font textFont = new Font("Arial", Font.PLAIN, 24);

	public HUDText(Canvas3D2D canvas, Rectangle rec)
	{
		addToCanvas(canvas);
		textElement = new HUDElement(rec.width, rec.height);
		textElement.setLocation(rec.x, rec.y);
		add(textElement);

		textShadowElement = new HUDElement(rec.width, rec.height);
		textShadowElement.setLocation(rec.x + 1, rec.y + 1);
		add(textShadowElement);

	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		canvas.addElement(textElement);
		canvas.addElement(textShadowElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		canvas.removeElement(textElement);
		canvas.removeElement(textShadowElement);
	}

	public void setText(String newText)
	{
		textElement.clear();
		textElement.getGraphics().setColor(textColor);
		textElement.getGraphics().setFont(textFont);
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
}
