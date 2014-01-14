package tools3d.camera;

//do new dolly for hmd non pivot round left eye
// sort out why eye aren't pointing straight?

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;
import tools3d.navigation.NavigationProcessorInterface;
import tools3d.utils.scenegraph.LocationUpdateListener;
import de.fruitfly.ovr.HMDInfo;
import de.fruitfly.ovr.OculusRift;

public class HMDCamDolly extends BranchGroup implements LocationUpdateListener, NavigationProcessorInterface
{
	private AvatarCollisionInfo avatarCollisionInfo;

	private TransformGroup leftVPTransformGroup = new TransformGroup();

	private TransformGroup rightVPTransformGroup = new TransformGroup();

	private ViewPlatform leftViewPlatform = new ViewPlatform();

	private ViewPlatform rightViewPlatform = new ViewPlatform();

	private Transform3D leftEyeTransform = new Transform3D();

	private Transform3D rightEyeTransform = new Transform3D();

	private static OculusRift or = new OculusRift();

	private Transform3D oculusTransform = new Transform3D();

	private Vector3f currentAvartarHeadPoint = new Vector3f();

	private Quat4f currentRot = new Quat4f();

	private Vector3f currentTrans = new Vector3f();

	public static OculusRift getOculusRift()
	{
		if (!or.isInitialized())
			or.init();

		return or;
	}

	public HMDCamDolly(AvatarCollisionInfo avatarCollisionInfo)
	{
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(BranchGroup.ALLOW_DETACH);
		leftViewPlatform.setActivationRadius(62f);
		rightViewPlatform.setActivationRadius(62f);

		addChild(leftVPTransformGroup);
		leftVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		leftVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		leftVPTransformGroup.addChild(leftViewPlatform);

		addChild(rightVPTransformGroup);
		rightVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rightVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rightVPTransformGroup.addChild(rightViewPlatform);

		// create the viewingplatfrom with 4 transforms (lc, yaw, headpos, pitch)
		setAvatarCollisionInfo(avatarCollisionInfo);

		HMDInfo hmdInfo = or.getHMDInfo();
		System.out.println(hmdInfo);

		//float viewCenter = hmdInfo.HScreenSize * 0.25f;
		// View transformation translation in world units.
		float halfIPD2 = hmdInfo.InterpupillaryDistance * 0.5f;
		//Matrix4f viewLeft = Matrix4f::Translation(halfIPD, 0, 0) * viewCenter;
		//Matrix4f viewRight= Matrix4f::Translation(-halfIPD, 0, 0) * viewCenter;

		leftEyeTransform.set(new Vector3f(-halfIPD2, 0f, 0f));
		rightEyeTransform.set(new Vector3f(halfIPD2, 0f, 0f));

	}

	float halfIPD = 0.033f;

	public void changeIPD(float mult)
	{
		halfIPD *= mult;

		leftEyeTransform.set(new Vector3f(-halfIPD, 0f, 0f));
		rightEyeTransform.set(new Vector3f(halfIPD, 0f, 0f));
		System.out.println("halfIPD = " + halfIPD);

		update();
	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	// deburner
	private Transform3D tempTransform = new Transform3D();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{

		// only update if new things have happened
		if (!currentRot.epsilonEquals(rot, 0.0001f) || !currentTrans.epsilonEquals(trans, 0.005f))
		{
			update();
			//recall for next iter
			currentRot.set(rot);
			currentTrans.set(trans);
		}
	}

	private void update()
	{
		currentAvartarHeadPoint.set(currentTrans);
		currentAvartarHeadPoint.y += avatarCollisionInfo.getCameraAbovePelvisHeight() + 0.7f; //TODO: get this right for oblivion
		tempTransform.set(currentRot, currentAvartarHeadPoint, 1f);
		if ((tempTransform.getType() & Transform3D.CONGRUENT) == 0)
		{
			System.out.println("bad trans for YawPitchCamDolly, check on headcam for difference");
		}
		else
		{
			tempTransform.mul(oculusTransform);
			tempTransform.mul(leftEyeTransform);
			leftVPTransformGroup.setTransform(tempTransform);
			tempTransform.set(currentRot, currentAvartarHeadPoint, 1f);
			tempTransform.mul(oculusTransform);
			tempTransform.mul(rightEyeTransform);
			rightVPTransformGroup.setTransform(tempTransform);
		}

	}

	public ViewPlatform getLeftViewPlatform()
	{
		return leftViewPlatform;
	}

	public ViewPlatform getRightViewPlatform()
	{
		return rightViewPlatform;
	}

	@Override
	public void process(long frameDuration)
	{
		if (or.isInitialized())
		{
			or.poll();
			oculusTransform.setEuler(new Vector3d(or.getPitch(), or.getYaw(), or.getRoll()));
			update();
			//System.out.println("Yaw: " + or.getYaw() + " Pitch: " + or.getPitch() + " Roll: " + or.getRoll());
		}
	}

	@Override
	public boolean isActive()
	{
		return true;
	}

	@Override
	public void setActive(boolean b)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void changeRotation(double scaledDeltaY, double scaledDeltaX)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationPerSec(float f, float g)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setZChange(float fastForwardRate)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setXChange(float f)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setYChange(float verticalRate)
	{
		throw new UnsupportedOperationException();
	}

}
