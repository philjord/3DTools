package tools3d.camera;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;
import tools3d.navigation.NavigationProcessorInterface;
import de.fruitfly.ovr.OculusRift;

public class HMDCamDolly extends BranchGroup implements IDolly, NavigationProcessorInterface
{
	private AvatarCollisionInfo avatarCollisionInfo;

	private TransformGroup bodyNeckGroup = new TransformGroup();

	private TransformGroup oculusGroup = new TransformGroup();

	private TransformGroup leftVPTransformGroup = new TransformGroup();

	private TransformGroup rightVPTransformGroup = new TransformGroup();

	private TransformGroup hudTransformGroup = new TransformGroup();

	private ViewPlatform leftViewPlatform = new ViewPlatform();

	private ViewPlatform rightViewPlatform = new ViewPlatform();

	private Transform3D leftEyeTransform = new Transform3D();

	private Transform3D rightEyeTransform = new Transform3D();

	private Transform3D hudTransform = new Transform3D();

	private Transform3D oculusTransform = new Transform3D();

	private Transform3D bodyNeckTransform = new Transform3D();

	private Quat4f currentRot = new Quat4f();

	private Vector3f currentTrans = new Vector3f();

	private float halfIPD = 0.032f;

	private static OculusRift or = new OculusRift();

	private View leftView;

	private View rightView;

	public static OculusRift getOculusRift()
	{
		if (!or.isInitialized())
			or.init();

		return or;
	}

	public HMDCamDolly(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;

		setCapability(BranchGroup.ALLOW_DETACH);
		leftViewPlatform.setActivationRadius(62f);
		rightViewPlatform.setActivationRadius(62f);

		addChild(bodyNeckGroup);
		bodyNeckGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		bodyNeckGroup.addChild(oculusGroup);
		oculusGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		oculusGroup.addChild(leftVPTransformGroup);
		leftVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		leftVPTransformGroup.addChild(leftViewPlatform);

		oculusGroup.addChild(rightVPTransformGroup);
		rightVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rightVPTransformGroup.addChild(rightViewPlatform);

		oculusGroup.addChild(hudTransformGroup);
		hudTransformGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		hudTransformGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		hudTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		System.out.println(or.getHMDInfo());
		setIPD(or.getHMDInfo().InterpupillaryDistance * 0.5f);

		hudTransform.set(new Vector3f(0.17f, -0.38f, -0.10f));
		hudTransformGroup.setTransform(hudTransform);

	}

	public void changeIPD(float mult)
	{
		halfIPD *= mult;
		setIPD(halfIPD);
	}

	private void setIPD(float ipd)
	{
		halfIPD = ipd;
		System.out.println("halfIPD = " + halfIPD);

		leftEyeTransform.set(new Vector3f(-halfIPD, 0f, -0.05f));// a bit forward
		leftVPTransformGroup.setTransform(leftEyeTransform);
		rightEyeTransform.set(new Vector3f(halfIPD, 0f, -0.05f));// a bit forward
		rightVPTransformGroup.setTransform(rightEyeTransform);
	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	// deburner
	private Vector3f neckPoint = new Vector3f();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{

		// only update if new things have happened
		if (!currentRot.epsilonEquals(rot, 0.0001f) || !currentTrans.epsilonEquals(trans, 0.005f))
		{
			neckPoint.set(currentTrans);
			neckPoint.y += avatarCollisionInfo.getCameraAbovePelvisHeight();

			bodyNeckTransform.set(currentRot, neckPoint, 1f);
			bodyNeckGroup.setTransform(bodyNeckTransform);
			//recall for next iter
			currentRot.set(rot);
			currentTrans.set(trans);
		}
	}

	public void attachViews(View _leftView, View _rightView)
	{
		this.leftView = _leftView;
		this.rightView = _rightView;
		leftView.attachViewPlatform(leftViewPlatform);
		rightView.attachViewPlatform(rightViewPlatform);

	}

	public static float lastFramePortionToPredict = 0.5f;

	private Vector3d newTrans = new Vector3d();

	private Vector3d lastTrans = new Vector3d();

	@Override
	public void process(long frameDuration)
	{
		if (or.isInitialized())
		{
			or.poll((leftView.getLastFrameDuration() / 1000f) * lastFramePortionToPredict);
			newTrans.set(or.getPitch(), or.getYaw(), or.getRoll());
			if (!newTrans.epsilonEquals(lastTrans, 0.0001f))
			{
				oculusTransform.setEuler(newTrans);
				oculusGroup.setTransform(oculusTransform);
				//System.out.println("Yaw: " + or.getYaw() + " Pitch: " + or.getPitch() + " Roll: " + or.getRoll());
				lastTrans.set(newTrans);
			}
		}
	}

	public void reset()
	{
		or.reset();
		setIPD(or.getHMDInfo().InterpupillaryDistance * 0.5f);
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

	public void setHudShape(BranchGroup hudShapeRoot)
	{
		// make sure it's up in front of the ld eyes in all
		hudTransformGroup.addChild(hudShapeRoot);
	}

}
