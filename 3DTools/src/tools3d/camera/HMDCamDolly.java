package tools3d.camera;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.jogamp.newt.opengl.GLWindow;

import java2.awt.GraphicsDevice;
import java2.awt.GraphicsEnvironment;
import tools3d.navigation.AvatarCollisionInfo;
import tools3d.navigation.NavigationProcessorInterface;
import tools3d.ovr.OculusRift03;

public class HMDCamDolly extends BranchGroup implements IDolly, NavigationProcessorInterface
{
	private AvatarCollisionInfo avatarCollisionInfo;

	private TransformGroup bodyNeckGroup = new TransformGroup();

	private TransformGroup oculusGroup = new TransformGroup();

	private TransformGroup leftVPTransformGroup = new TransformGroup();

	private TransformGroup rightVPTransformGroup = new TransformGroup();

	private TransformGroup hudTransformGroup = new TransformGroup();

	private LocatableViewPlatform leftViewPlatform = new LocatableViewPlatform();

	private LocatableViewPlatform rightViewPlatform = new LocatableViewPlatform();

	private Transform3D leftEyeTransform = new Transform3D();

	private Transform3D rightEyeTransform = new Transform3D();

	private Transform3D hudTransform = new Transform3D();

	private Transform3D oculusTransform = new Transform3D();

	private Transform3D bodyNeckTransform = new Transform3D();

	private Quat4f currentRot = new Quat4f();

	private Vector3f currentTrans = new Vector3f();

	private float halfIPD = 0.032f;

	private static OculusRift03 or = new OculusRift03();

	private View leftView;

	private View rightView;

	public static OculusRift03 getOculusRift()
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
		
		bodyNeckGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		leftViewPlatform.setShortCutGroup(bodyNeckGroup);
		rightViewPlatform.setShortCutGroup(bodyNeckGroup);

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
		//System.out.println("leftView.getLastFrameDuration() " +leftView.getLastFrameDuration());

		//NOTE frameDuration appears to be crap
		// leftView.getLastFrameDuration() is a small number based on GL calls only
		// we use about as much again in general java code per frame  , and we are doing 2 renders per behavior
		// hence the whole thing x4
		if (or.isInitialized())
		{
			or.poll(((leftView.getLastFrameDuration() / 1000f) * lastFramePortionToPredict) * 4);
			//-System.out.println("poll with " + (((leftView.getLastFrameDuration() / 1000f) * lastFramePortionToPredict) * 4));
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
	
	/**
	 *
	 *TODO: the F9 bit full screen etc
	Press F9 or F11 to switch rendering to the Oculus Rift.
	F9 - Switches to hardware full-screen mode. This will give best possible latency, but may blink
	monitors as the operating system changes display settings. If no image shows up in the Rift, then press
	F9 again to cycle to the next monitor.
	F11 - Instantly switches the rendering window to the Rift portion of the desktop. This mode has higher
	latency and no vsync, but is convenient for development.
	 
	*/
	public void sendToRift()
	{ 
		//TODO:...
		System.out.println("looking for " + or.getHmdDesc().displayDeviceName);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(leftView.getCanvas3D(0).getGLWindow());
		for (GraphicsDevice gd : ge.getScreenDevices())
		{
			System.out.println("I've got " + gd.getIDstring());
			System.out.println(" " + gd.toString());
		}

		//using desktop location instead
		GLWindow w = leftView.getCanvas3D(0).getGLWindow();
		w.setPosition(or.getHmdDesc().windowsPosX, or.getHmdDesc().windowsPosY);
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
