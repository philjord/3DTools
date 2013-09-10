package tools3d.mixed3d2d.hud.hudelements;

import java.awt.Color;
import java.awt.Font;

import tools.Time;
import tools.image.SimpleImageLoader;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.mixed3d2d.hud.HUDElement;
import tools3d.mixed3d2d.hud.HUDElementContainer;

public class HUDCalendarClock extends HUDElementContainer
{
	private HUDElement textElement;

	private Color fpsTextColor = new Color(0.2f, 0.3f, 0.4f, 1f);

	private Font fpsTextFont = new Font("Arial", Font.PLAIN, 10);

	private HUDElement handsElement;

	private HUDElement bgElement = new HUDElement(120, 50);

	private HUDElement compassElement = new HUDElement(50, 50);

	public HUDCalendarClock(Canvas3D2D canvas)
	{
		this();
		addToCanvas(canvas);
	}

	public HUDCalendarClock()
	{
		bgElement.setLocation(5, 220);
		bgElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.4f));
		bgElement.getGraphics().fillRoundRect(0, 0, 120, 30, 15, 15);
		add(bgElement);
		textElement = new HUDElement(110, 10);
		textElement.setLocation(15, 230);
		add(textElement);

		compassElement.setLocation(5, 250);
		compassElement.getGraphics().drawImage(SimpleImageLoader.getImage("media/images/rolex5.gif"), 0, 0, 50, 50, null);
		add(compassElement);

		handsElement = new HUDElement(50, 50);
		handsElement.setLocation(5, 250);
		add(handsElement);
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		canvas.addElement(bgElement);
		canvas.addElement(textElement);
		canvas.addElement(compassElement);
		canvas.addElement(handsElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		canvas.removeElement(bgElement);
		canvas.removeElement(textElement);
		canvas.removeElement(compassElement);
		canvas.removeElement(handsElement);
	}

	public void setTime(long elapsedMS)
	{

		String date = Time.getDateString(elapsedMS);

		textElement.clear();
		textElement.getGraphics().setColor(fpsTextColor);
		textElement.getGraphics().setFont(fpsTextFont);
		textElement.getGraphics().drawString(date, 0, 10);

		int minuteOfHour = Time.getMinuteOfHour(elapsedMS);
		int hourOfDay = Time.getHourOfDay(elapsedMS);
		int minuteOfDay = (hourOfDay * 60) + minuteOfHour;

		float littleHandRot = (float) minuteOfDay / (float) (24 * 60);
		littleHandRot = littleHandRot * (float) Math.PI * 2f * 2f; // note 12 hour clock
		float bigHandRot = (float) minuteOfHour / (float) 60;
		bigHandRot = bigHandRot * (float) Math.PI * 2f;

		handsElement.clear();
		handsElement.getGraphics().setColor(new Color(1f, 1f, 1f));
		handsElement.getGraphics().drawLine(25, 25, (int) (Math.sin(bigHandRot) * 17) + 25, (int) (Math.cos(bigHandRot) * -17) + 25);

		handsElement.getGraphics().drawLine(25, 25, (int) (Math.sin(littleHandRot) * 12) + 25, (int) (Math.cos(littleHandRot) * -12) + 25);
	}

}
