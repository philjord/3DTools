package tools3d.camera;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;

import com.sun.j3d.utils.universe.PlatformGeometry;

public class HeadCamDolly extends Dolly
{
	private AvatarCollisionInfo avatarCollisionInfo;

	public HeadCamDolly(AvatarCollisionInfo avatarCollisionInfo)
	{
		// create the viewingplatfrom with 4 transforms (loc, yaw, headpos, pitch)
		super(1);
		setAvatarCollisionInfo(avatarCollisionInfo);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		pg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPlatformGeometry(pg);
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
				getViewPlatformTransform().setTransform(tempTransform);
			}
			
			//recall for next iter
			prevRot.set(rot);
			prevTrans.set(trans);
		}
	}

}
