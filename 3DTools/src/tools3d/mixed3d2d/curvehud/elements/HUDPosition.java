package tools3d.mixed3d2d.curvehud.elements;

import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

import com.jogamp.graph.ui.shapes.Label;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class HUDPosition implements LocationUpdateListener
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
		posLabel.moveTo(-0.98f, 0.8f, 0f);
		posLabel.setColor(0.0f, 0.1f, 0.1f, 0.85f);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{

	}

	@Override
	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		if (posLabel != null)
			posLabel.setText("" + (int) trans.x + ", " + (int) trans.y + ", " + (int) trans.z);
	}

}
