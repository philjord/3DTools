package tools3d.camera.old;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.camera.Dolly;
import tools3d.geography.HeightMap;
import tools3d.navigation.AvatarCollisionInfo;
import tools3d.utils.YawPitch;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.MultiTransformGroup;
import com.sun.j3d.utils.universe.PlatformGeometry;

public class OldTrailerCamDolly extends Dolly
{

	private AvatarCollisionInfo avatarCollisionInfo;

	private TransformGroup locationTransformGroup;

	private TransformGroup yawTransformGroup;

	private TransformGroup headTranslationTransformGroup;

	private TransformGroup cameraPitchTransformGroup;

	private TransformGroup trailorTranslationTransformGroup;

	private BranchGroup camBG = new BranchGroup();

	private BranchGroup transVWorldBG = new BranchGroup();

	private TransformGroup camTransformGroup;

	public OldTrailerCamDolly(AvatarCollisionInfo avatarCollisionInfo)
	{
		super(1);
		setAvatarCollisionInfo(avatarCollisionInfo);
		camTransformGroup = getViewPlatformTransform();

		// create the viewingplatfrom with 5 transforms (loc, yaw, headpos, pitch, trailer, collisionSquash)
		MultiTransformGroup m = new MultiTransformGroup(5);

		transVWorldBG.setCapability(BranchGroup.ALLOW_DETACH);
		transVWorldBG.addChild(m.getTransformGroup(0));

		locationTransformGroup = m.getTransformGroup(0);
		yawTransformGroup = m.getTransformGroup(1);
		headTranslationTransformGroup = m.getTransformGroup(2);
		cameraPitchTransformGroup = m.getTransformGroup(3);
		trailorTranslationTransformGroup = m.getTransformGroup(4);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		pg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPlatformGeometry(pg);

		// set head
		Transform3D headTranslationTransform = new Transform3D();
		headTranslationTransform.set(new Vector3d(0, avatarCollisionInfo.getCameraAbovePelvisHeight(), 0));
		headTranslationTransformGroup.setTransform(headTranslationTransform);

		// set trailor out the back (+ve z)

		trailorTranslationTransform.set(new Vector3d(0, 0, 20));
		trailorTranslationTransformGroup.setTransform(trailorTranslationTransform);

		camBG.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		trailorTranslationTransformGroup.addChild(camBG);

		// I do this to enable the getLocalToVworld call below
		// NOTE I have the code working in the ModelUtil class! woot!
		addChild(transVWorldBG);

	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	/**
	 * @see tools3d.utils.scenegraph.LocationUpdateListener#locationUpdated(Quat4f, Vector3f)
	 */
	// deburner
	private Transform3D locationTransform = new Transform3D();

	private Transform3D yawTransform = new Transform3D();

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

		avartarHeadPoint.y += avatarCollisionInfo.getCameraAbovePelvisHeight();

		setGroundY(cameraPoint);
		cameraPoint.sub(getCollisionAdjustment(cameraPoint, avartarHeadPoint));

		camTrans.lookAt(cameraPoint, avartarHeadPoint, Y_UP);

		// NOTE invert has doc to say it is slow
		try
		{
			camTrans.invert();
			camTransformGroup.setTransform(camTrans);
		}
		catch (SingularMatrixException e)
		{
			//System.out.println("SingularMatrixException in " + this);
		}

	}

	private static final Vector3d Y_UP = new Vector3d(0, 1, 0);

	private static final double MIN_CAM_HEIGHT = 1;

	// deburners
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
		// default, must be not on top of itself
		returnVec.set(0, 0, 0.1);
		if (collisionPickTool != null)
		{
			collisionPickTool.setShapeSegment(avatar, camera);

			// notice looking for closest to avatar
			pickResult = collisionPickTool.pickClosest();

			if (pickResult != null)
			{
				PickIntersection pi = pickResult.getIntersection(0);
				// camera can't be on top of avatar
				if (pi.getDistance() > 0)
				{
					returnVec.sub(avatar, camera);
					returnVec.scale((returnVec.length() - pi.getDistance()) / returnVec.length());
					returnVec.negate();
				}
			}
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
					// new Exception("GroundHeightCalculator.getDistanceFromGround returned Double.MIN_VALUE")
					// .printStackTrace();
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				// ignore
			}
		}
	}
}
