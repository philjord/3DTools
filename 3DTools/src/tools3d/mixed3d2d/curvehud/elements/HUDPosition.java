package tools3d.mixed3d2d.curvehud.elements;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.test.junit.graph.demos.ui.Label;

import awt.tools3d.mixed3d2d.hud.HUDElementContainer;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class HUDPosition extends HUDElementContainer implements LocationUpdateListener
{

	private Label posLabel;

	public HUDPosition(Canvas3D2D canvas)
	{
		this();
		addToCanvas(canvas);
	}

	public HUDPosition()
	{
	}

	public void addToCanvas(Canvas3D2D canvas3d2d)
	{
		posLabel = canvas3d2d.createLabel();
		posLabel.setEnabled(true);
		posLabel.translate(-0.98f, 0.8f, 0f);
		posLabel.setColor(0.0f, 0.1f, 0.1f, 0.85f);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{

	}

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		if (posLabel != null)
			posLabel.setText("" + (int) trans.x + ", " + (int) trans.y + ", " + (int) trans.z);
	}

}
