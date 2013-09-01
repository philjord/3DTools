/*
 * Created on Jan 3, 2004
 */
package tools3d.camera.old;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.universe.VisualPhysicalUniverse;
import tools3d.utils.scenegraph.LocationUpdateListener;

import com.sun.j3d.utils.universe.MultiTransformGroup;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author Administrator
 *
 */
public class BirdsEyeCam implements LocationUpdateListener
{
	// front and back clips, should be ratio of no more than 3000
	public static float FRONT_CLIP = 1f;

	public static float BACK_CLIP = 8000f;

	private Viewer viewer;

	private ViewingPlatform viewingPlatform;

	private TransformGroup locationTransformGroup;

	private TransformGroup hoverTransformGroup;

	private static double MAX_HEIGHT = 7000;

	private static double HEIGHT_DEFAULT = 50;

	private double height = HEIGHT_DEFAULT;

	private double zTilt = 0;

	public BirdsEyeCam(VisualPhysicalUniverse universe, Canvas3D canvas3D)
	{
		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		viewer = new Viewer(canvas3D);
		
		// create the viewingplatfrom with 2 transforms (loc and hover)
		viewingPlatform = new ViewingPlatform(2);
		viewer.setViewingPlatform(viewingPlatform);

		// Note the tansform are already attached to each other
		MultiTransformGroup mtg = viewingPlatform.getMultiTransformGroup();
		locationTransformGroup = mtg.getTransformGroup(0);
		hoverTransformGroup = mtg.getTransformGroup(1);

		// set up the view
		viewer.getView().setFrontClipPolicy(View.VIRTUAL_EYE);
		viewer.getView().setBackClipDistance(BACK_CLIP);
		viewer.getView().setFrontClipDistance(FRONT_CLIP);

		// set max frame rate to 25
		viewer.getView().setMinimumFrameCycleTime(40);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		viewingPlatform.setPlatformGeometry(pg);

		// set up the Cam
		setCameraPosition();

		//actually add our viewer to the universe
		universe.addViewingPlatform(viewingPlatform);
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
