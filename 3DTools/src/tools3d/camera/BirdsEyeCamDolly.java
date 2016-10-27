package tools3d.camera;

import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.jogamp.java3d.Group;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;

import com.sun.j3d.utils.universe.MultiTransformGroup;
import com.sun.j3d.utils.universe.PlatformGeometry;

public class BirdsEyeCamDolly extends Dolly
{
	private TransformGroup locationTransformGroup;

	private TransformGroup hoverTransformGroup;

	private static double MAX_HEIGHT = 7000;

	private static double HEIGHT_DEFAULT = 40;

	private double height = HEIGHT_DEFAULT;

	private double zTilt = 0;

	public BirdsEyeCamDolly()
	{
		// create the viewingplatfrom with 2 transforms (loc and hover)
		super(2);

		// Note the tansform are already attached to each other
		MultiTransformGroup m = getMultiTransformGroup();
		locationTransformGroup = m.getTransformGroup(0);
		hoverTransformGroup = m.getTransformGroup(1);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		pg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPlatformGeometry(pg);

		// set up the Cam
		setCameraPosition();

	}

	//deburner
	private Transform3D locationTransform = new Transform3D();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		// copy the location position
		locationTransform.set(trans);
		locationTransformGroup.setTransform(locationTransform);
	}

	//deburners
	private Point3d eyePoint = new Point3d();

	private Point3d center = new Point3d();

	private Vector3d eye = new Vector3d();

	private Transform3D hoverTrans = new Transform3D();

	private Vector3d up = new Vector3d();

	private void setCameraPosition()
	{
		eye.set(0, 50, zTilt);
		eye.normalize();
		eye.scale(height);
		eyePoint.set(eye);
		// NOTE z axis is "up"	
		up.set(0, 0, (zTilt < 0 ? 1 : -1));
		hoverTrans.lookAt(eyePoint, center, up);
		hoverTrans.invert();
		hoverTransformGroup.setTransform(hoverTrans);
	}

	public void zoomOut()
	{
		if (height < MAX_HEIGHT)
		{
			height = height * 1.1;
			setCameraPosition();
		}
	}

	public void zoomIn()
	{
		if (height > 5)
		{
			height = height * 0.9;
			setCameraPosition();
		}
	}

	public void tiltUp()
	{
		if (zTilt < 200)
		{
			zTilt += 10;
			setCameraPosition();
		}
	}

	public void tiltDown()
	{
		if (zTilt > -200)
		{
			zTilt -= 10;
			setCameraPosition();
		}
	}

}
