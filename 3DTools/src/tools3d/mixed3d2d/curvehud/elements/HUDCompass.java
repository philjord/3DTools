package tools3d.mixed3d2d.curvehud.elements;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.test.junit.graph.demos.ui.Label;

import tools.CompassRotation;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.utils.YawPitch;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class HUDCompass implements LocationUpdateListener
{
	private Label textElementBear;
	private Label textElementAzi;

	public HUDCompass(Canvas3D2D canvas)
	{
		this();
		addToCanvas(canvas);
	}

	public HUDCompass()
	{
		/*bgElement = new HUDElement(80, 50);
		bgElement.setLocation(5, 70);
		bgElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.85f));
		bgElement.getGraphics().fillRoundRect(0, 0, 80, 30, 15, 15);
		add(bgElement);
		
		compassElement = new HUDElement(50, 50);
		compassElement.setLocation(5, 110);
		//compassElement.getGraphics().drawImage(SimpleImageLoader.getImage("media/images/compass.gif"), 0, 0, 50, 50, null);
		add(compassElement);
		compassNeedleElement = new HUDElement(50, 50);
		compassNeedleElement.setLocation(5, 110);
		add(compassNeedleElement);*/
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		textElementBear = canvas.createLabel();
		textElementBear.setEnabled(true);
		textElementBear.translate(-0.98f, 0.74f, 0f);
		textElementBear.setColor(0f, 0f, 0f, 1f);

		textElementAzi = canvas.createLabel();
		textElementAzi.setEnabled(true);
		textElementAzi.translate(-0.98f, 0.7f, 0f);
		textElementAzi.setColor(0f, 0f, 0f, 1f);

		//canvas.addElement(bgElement);
		//canvas.addElement(textElement);
		//canvas.addElement(compassElement);
		//canvas.addElement(compassNeedleElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		//canvas.removeElement(bgElement);
		//canvas.removeElement(textElement);
		//canvas.removeElement(compassElement);
		//canvas.removeElement(compassNeedleElement);
	}

	// deburner
	private String[] bearings = new String[] { "N", "NE", "E", "SE", "S", "SW", "W", "NW", "eh?" };

	private YawPitch yawPitch = new YawPitch();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		if (textElementBear != null)
		{
			yawPitch.set(rot);

			// make it a degree and reverse the rotation (from java3D CCW to compass CW)
			int yawDeg = (int) CompassRotation.wrapToMax(-CompassRotation.radToDeg(yawPitch.getYaw()), 360);

			textElementBear.setText("" + yawDeg + " " + bearings[(yawDeg + 22) % 360 / 45]);
			textElementAzi.setText("Azi " + (int) CompassRotation.radToDeg(yawPitch.getPitch()));

			//double yawRad = -yawPitch.getYaw();
			//compassNeedleElement.clear();
			//compassNeedleElement.getGraphics().drawLine(25, 25, (int) (Math.sin(yawRad) * 20) + 25, (int) (Math.cos(yawRad) * -20) + 25);
		}
	}

}
