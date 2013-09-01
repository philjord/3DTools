package tools3d.hud.old;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUDElement;

public class HUDTextArea
{
	private HUDElement textElement;

	private String text;

	private Color textColor = new Color(0.9f, 0.9f, 0.9f, 1f);

	private Color background = new Color(0.0f, 0.0f, 0.0f, 0f);

	private Font textFont = new Font("Arial", Font.PLAIN, 14);

	public HUDTextArea(Canvas3D2D canvas)
	{
		textElement = new HUDElement(1, 1);
		textElement.setLocation(1, 1);
		canvas.addElement(textElement);
	}

	public HUDTextArea(Canvas3D2D canvas, Color background, Color textColor, Font textFont)
	{
		this(canvas);
		this.textColor = textColor;
		this.background = background;
		this.textFont = textFont;
	}

	public void setText(String newText)
	{
		if (newText != text)
		{
			text = newText;
			JLabel ta = new JLabel(newText);
			ta.setFont(textFont);
			ta.setForeground(textColor);

			ta.setSize(ta.getPreferredSize().width, ta.getPreferredSize().height);

			textElement.setSize(ta.getPreferredSize().width + 1, ta.getPreferredSize().height + 1);

			textElement.clear();
			textElement.getGraphics().setColor(background);
			textElement.getGraphics().fillRoundRect(0, 0, ta.getPreferredSize().width, ta.getPreferredSize().height, 5, 5);

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
