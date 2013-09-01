package tools3d.hud.old;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUDElement;

public class HUDText
{

	private HUDElement textWhiteElement;

	private HUDElement textBlackElement;

	private Color textWhiteColor = new Color(1f, 1f, 1f, 1f);

	private Color textBlackColor = new Color(0f, 0f, 0f, 1f);

	private Font textFont;

	private Rectangle rec;

	private int fontSize;

	private Canvas3D2D canvas;

	public HUDText(Canvas3D2D canvas, Rectangle rec, int fontSize)
	{
		this.rec = rec;
		this.fontSize = fontSize;
		this.canvas = canvas;
		textFont = new Font("Arial", Font.PLAIN, fontSize);
		textWhiteElement = new HUDElement(rec.width, rec.height);
		textWhiteElement.setLocation(rec.x, rec.y);
		canvas.addElement(textWhiteElement);
		textBlackElement = new HUDElement(rec.width, rec.height);
		textBlackElement.setLocation(rec.x, rec.y - (int) (fontSize * 1.5));
		canvas.addElement(textBlackElement);
	}

	public void destroy()
	{
		textWhiteElement.clear();
		canvas.removeElement(textWhiteElement);
		textBlackElement.clear();
		canvas.removeElement(textBlackElement);
	}

	public void setText(String newText)
	{
		textWhiteElement.clear();
		textWhiteElement.getGraphics().setColor(textWhiteColor);
		textWhiteElement.getGraphics().setFont(textFont);
		textWhiteElement.getGraphics().drawString(newText, 0, (int) (fontSize * 1.5));

		textBlackElement.clear();
		textBlackElement.getGraphics().setColor(textBlackColor);
		textBlackElement.getGraphics().setFont(textFont);
		textBlackElement.getGraphics().drawString(newText, 0, (int) (fontSize * 1.5));
	}

	public void setTextGreyed(String newText)
	{
		textWhiteElement.clear();
		textWhiteElement.getGraphics().setColor(textWhiteColor.darker());
		textWhiteElement.getGraphics().setFont(textFont);
		textWhiteElement.getGraphics().drawString(newText, 0, (int) (fontSize * 1.5));

		textBlackElement.clear();
		textBlackElement.getGraphics().setColor(textBlackColor.brighter());
		textBlackElement.getGraphics().setFont(textFont);
		textBlackElement.getGraphics().drawString(newText, 0, (int) (fontSize * 1.5));
	}

	public void setLocation(int x, int y)
	{
		rec.x = x;
		rec.y = y;
		textWhiteElement.setLocation(x, y);
		textBlackElement.setLocation(rec.x, rec.y - (int) (fontSize * 1.5));
	}
}
