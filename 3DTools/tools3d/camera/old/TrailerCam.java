/*
 * Created on Jan 3, 2004
 */
package tools3d.camera.old;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.geography.HeightMap;
import tools3d.hud.HUD;
import tools3d.universe.VisualPhysicalUniverse;
import tools3d.utils.YawPitch;
import tools3d.utils.scenegraph.LocationUpdateListener;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.MultiTransformGroup;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author Administrator
 *
 */
public class TrailerCam implements LocationUpdateListener
{
	// front and back clips, should be ratio of no more than 3000
	public static float FRONT_CLIP = 0.5f; // why not 19 or more, cos it looks crap?

	public static float BACK_CLIP = 300f;

	private Viewer viewer;

	private ViewingPlatform viewingPlatform;

	private TransformGroup locationTransformGroup;

	private TransformGroup yawTransformGroup;

	private TransformGroup headTranslationTransformGroup;

	private TransformGroup cameraPitchTransformGroup;

	private TransformGroup trailorTranslationTransformGroup;

	private BranchGroup camBG = new BranchGroup();

	private TransformGroup camTransformGroup;

	public TrailerCam(VisualPhysicalUniverse universe, Canvas3D canvas3D)
	{
		// create a viewer with the given canvas, physical environment and physical body are defaulted
		viewer = new Viewer(canvas3D);

		//viewer.setAvatar(new SimpleMD3Avatar("media/models/eyeAvatar.ac"));

		viewingPlatform = new ViewingPlatform(1);
		viewer.setViewingPlatform(viewingPlatform);

		camTransformGroup = viewingPlatform.getViewPlatformTransform();

		// create the viewingplatfrom with 5 transforms (loc, yaw, headpos, pitch, trailer, collisionSquash)		
		MultiTransformGroup mtg = new MultiTransformGroup(5);
		BranchGroup bg = new BranchGroup();
		bg.addChild(mtg.getTransformGroup(0));

		locationTransformGroup = mtg.getTransformGroup(0);
		yawTransformGroup = mtg.getTransformGroup(1);
		headTranslationTransformGroup = mtg.getTransformGroup(2);
		cameraPitchTransformGroup = mtg.getTransformGroup(3);
		trailorTranslationTransformGroup = mtg.getTransformGroup(4);

		// set up the view
		viewer.getView().setFrontClipPolicy(View.VIRTUAL_EYE);
		viewer.getView().setBackClipDistance(BACK_CLIP);
		viewer.getView().setFrontClipDistance(FRONT_CLIP);

		// set max frame rate to 25
		viewer.getView().setMinimumFrameCycleTime(40);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		viewingPlatform.setPlatformGeometry(pg);

		// set head
		headTranslationTransform.set(new Vector3d(0, 0.9, 0));
		headTranslationTransformGroup.setTransform(headTranslationTransform);

		// set trailor out the back (+ve z)
		trailorTranslationTransform.set(new Vector3d(0, 0, 20));
		trailorTranslationTransformGroup.setTransform(trailorTranslationTransform);

		camBG.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		trailorTranslationTransformGroup.addChild(camBG);

		// I do this to enable the getLocalToVworld call below
		// NOTE I have the code working in the ModelUtil class! woot!
		// or take a look at teh getfulltree code in j3dniavobject in j3dnif	

		universe.addToBehaviorBranch(bg);

		//actually add our viewer to the universe
		universe.addViewingPlatform(viewingPlatform);
	}

	public void setHUD(HUD hud)
	{
		viewingPlatform.getPlatformGeometry().addChild(hud);
	}

	/**
	 * @see tools3d.utils.scenegraph.LocationUpdateListener#locationUpdated(Quat4f, Vector3f)
	 */
	//deburner
	private Transform3D locationTransform = new Transform3D();

	private Transform3D yawTransform = new Transform3D();

	private Transform3D headTranslationTransform = new Transform3D();

	private Transform3D cameraPitchTransform = new Transform3D();

	private Transform3D trailorTranslationTransform = new Transform3D();

	private YawPitch newYawPitch = new YawPitch();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{

		newYawPitch.set(rot);

		// copy the location position
		locationTransform.set(trans);
		locationTransformGroup.setTransform(locationTransform);

		// set yaw
		yawTransform.rotY(newYawPitch.getYaw());
		yawTransformGroup.setTransform(yawTransform);

		// set pitch
		cameraPitchTransform.rotX(newYawPitch.getPitch());
		cameraPitchTransformGroup.setTransform(cameraPitchTransform);

		camBG.getLocalToVworld(viewTrans);
		viewTrans.get(tempVector);
		cameraPoint.set(tempVector);
		avartarHeadPoint.set(trans);

		avartarHeadPoint.y += 2.0f;

		setGroundY(cameraPoint);
		cameraPoint.sub(getCollisionAdjustment(cameraPoint, avartarHeadPoint));

		camTrans.lookAt(cameraPoint, avartarHeadPoint, Y_UP);
		camTrans.invert();
		camTransformGroup.setTransform(camTrans);

	}

	private static final Vector3d Y_UP = new Vector3d(0, 1, 0);

	private static final double MIN_CAM_HEIGHT = 1;

	//	deburners
	private Vector3d returnVec = new Vector3d();

	private PickResult pickResult;

	private Transform3D viewTrans = new Transform3D();

	private Transform3D camTrans = new Transform3D();

	private Vector3d tempVector = new Vector3d();

	private Point3d cameraPoint = new Point3d();

	private Point3d avartarHeadPoint = new Point3d();

	private HeightMap navigationGeometry;

	private PickTool collisionPickTool;

	public void setPhysicals(BranchGroup physicalBranch, HeightMap navigationGeometry)
	{
		collisionPickTool = new PickTool(physicalBranch);
		collisionPickTool.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
		this.navigationGeometry = navigationGeometry;
	}

	public Vector3d getCollisionAdjustment(Point3d camera, Point3d avatar)
	{
		if (collisionPickTool != null)
		{

			collisionPickTool.setShapeSegment(avatar, camera);

			//notice looking for closest to avatar
			pickResult = collisionPickTool.pickClosest();

			if (pickResult != null)
			{
				PickIntersection pi = pickResult.getIntersection(0);
				returnVec.sub(avatar, camera);
				returnVec.scale((returnVec.length() - pi.getDistance()) / returnVec.length());
				returnVec.negate();
			}
			else
			{
				returnVec.set(0, 0, 0);
			}
		}
		else
		{
			returnVec.set(0, 0, 0);
		}

		return returnVec;
	}

	public void setGroundY(Point3d camLocation)
	{
		if (navigationGeometry != null)
		{
			double groundY = navigationGeometry.getGroundY(camLocation.x, camLocation.z);

			try
			{
				if (groundY != Double.MIN_VALUE)
				{
					// Do we need to adjust the height? 	
					if (groundY + MIN_CAM_HEIGHT > camLocation.y)
					{
						camLocation.y = groundY + MIN_CAM_HEIGHT;
					}
				}
				else
				{
					//	new Exception("GroundHeightCalculator.getDistanceFromGround returned Double.MIN_VALUE")
					//	.printStackTrace();
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				// ignore
			}
		}
	}
}
