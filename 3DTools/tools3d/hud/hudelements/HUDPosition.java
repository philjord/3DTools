package tools3d.hud.hudelements;

import java.awt.Color;
import java.awt.Font;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUDElement;
import tools3d.hud.HUDElementContainer;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class HUDPosition extends HUDElementContainer implements LocationUpdateListener
{
	private HUDElement bgElement;

	private HUDElement textElement;

	private Color fpsTextColor = new Color(0.2f, 0.3f, 0.4f, 1f);

	private Font fpsTextFont = new Font("Arial", Font.PLAIN, 10);

	public HUDPosition(Canvas3D2D canvas)
	{
		this();
		addToCanvas(canvas);
	}

	public HUDPosition()
	{
		bgElement = new HUDElement(120, 50);
		bgElement.setLocation(5, 35);
		bgElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.4f));
		bgElement.getGraphics().fillRoundRect(0, 0, 120, 30, 15, 15);
		add(bgElement);

		textElement = new HUDElement(110, 10);
		textElement.setLocation(15, 45);
		add(textElement);

	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		canvas.addElement(bgElement);
		canvas.addElement(textElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		canvas.removeElement(bgElement);
		canvas.removeElement(textElement);
	}

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		textElement.clear();
		textElement.getGraphics().setColor(fpsTextColor);
		textElement.getGraphics().setFont(fpsTextFont);
		textElement.getGraphics().drawString("" + (int) trans.x + ", " + (int) trans.y + ", " + (int) trans.z, 0, 10);
	}

}
