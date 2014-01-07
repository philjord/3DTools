package tools3d.camera;

//do new dolly for hmd non pivot round left eye
// sort out why eye aren't pointing straight?

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class HMDCamDolly extends BranchGroup implements LocationUpdateListener
{
	private AvatarCollisionInfo avatarCollisionInfo;

	private TransformGroup leftVPTransformGroup = new TransformGroup();

	private TransformGroup rightVPTransformGroup = new TransformGroup();

	private ViewPlatform leftViewPlatform = new ViewPlatform();

	private ViewPlatform rightViewPlatform = new ViewPlatform();
	
	private Transform3D leftEyeTransform = new Transform3D();
	
	private Transform3D rightEyeTransform = new Transform3D();

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
		leftEyeTransform.set(new Vector3f(-0.033f, 0f, 0f));
		addChild(rightVPTransformGroup);
		rightVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rightVPTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rightVPTransformGroup.addChild(rightViewPlatform);
		rightEyeTransform.set(new Vector3f(0.033f, 0f, 0f));

		// create the viewingplatfrom with 4 transforms (loc, yaw, headpos, pitch)
		setAvatarCollisionInfo(avatarCollisionInfo);

	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	// deburner
	private Transform3D tempTransform = new Transform3D();

	
	
	
	private Vector3f currentAvartarHeadPoint = new Vector3f();

	private Quat4f prevRot = new Quat4f();

	private Vector3f prevTrans = new Vector3f();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		
		
		// only update if new things have happened
		if (!prevRot.epsilonEquals(rot, 0.0001f) || !prevTrans.epsilonEquals(trans, 0.005f))
		{
			currentAvartarHeadPoint.set(trans);
			currentAvartarHeadPoint.y += avatarCollisionInfo.getCameraAbovePelvisHeight();
			tempTransform.set(rot, currentAvartarHeadPoint, 1f);
			if ((tempTransform.getType() & Transform3D.CONGRUENT) == 0)
			{
				System.out.println("bad trans for YawPitchCamDolly, check on headcam for difference");
			}
			else
			{
				tempTransform.mul(leftEyeTransform);
				leftVPTransformGroup.setTransform(tempTransform);
				tempTransform.set(rot, currentAvartarHeadPoint, 1f);
				tempTransform.mul(rightEyeTransform);
				rightVPTransformGroup.setTransform(tempTransform);
			}

			//recall for next iter
			prevRot.set(rot);
			prevTrans.set(trans);
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

}
