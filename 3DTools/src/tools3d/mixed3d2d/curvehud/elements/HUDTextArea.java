package tools3d.mixed3d2d.curvehud.elements;

import java.awt.Font;
import java.awt.Color;

import javax.swing.JLabel;

import awt.tools3d.mixed3d2d.hud.HUDElement;
import awt.tools3d.mixed3d2d.hud.HUDElementContainer;
import tools3d.mixed3d2d.Canvas3D2D;

public class HUDTextArea extends HUDElementContainer
{

	private HUDElement textElement;

	private String text;

	private Color textColor = new Color(0.9f, 0.9f, 0.9f, 1f);

	private Color background = new Color(0.0f, 0.0f, 0.0f, 0f);

	private Font textFont = new Font("Arial", Font.PLAIN, 14);

	public HUDTextArea()
	{
		textElement = new HUDElement(1, 1);
		textElement.setLocation(1, 1);
		add(textElement);
	}

	public HUDTextArea(Color background, Color textColor, Font textFont)
	{
		this();
		this.textColor = textColor;
		this.background = background;
		this.textFont = textFont;
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		canvas.addElement(textElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		canvas.removeElement(textElement);
	}

	public void setText(String newText)
	{
		if (newText != text)
		{
			text = newText;
			// TODO: if I could get any JComponet to paint it's be groovy
			// let's work out how big we need to be and set ourselves to that using a JTextArea
			JLabel ta = new JLabel(newText);
			ta.setFont(textFont);
			ta.setForeground(textColor);

			ta.setSize(ta.getPreferredSize().width + 15, ta.getPreferredSize().height);

			textElement.setSize(ta.getPreferredSize().width + 15, ta.getPreferredSize().height + 1);

			textElement.clear();
			textElement.getGraphics().setColor(background);
			textElement.getGraphics().fillRoundRect(0, 0, ta.getPreferredSize().width + 15, ta.getPreferredSize().height, 5, 5);

			ta.getUI().paint(textElement.getGraphics(), ta);
		}
	}

	public void setLocation(int x, int y)
	{
		textElement.setLocation(x, y);
	}

	public void setBackground(Color color)
	{
		background = color;
	}

	public void setTextColor(Color color)
	{
		textColor = color;
	}

	public void setTextFont(Font font)
	{
		textFont = font;
	}

}
