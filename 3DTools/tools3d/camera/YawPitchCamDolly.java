package tools3d.camera;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.PlatformGeometry;

public class YawPitchCamDolly extends Dolly
{
	/**
	 * This camera assumes it is attached inside teh scene graph and moves with teh object attached to
	 *
	 */
	public YawPitchCamDolly()
	{
		// create the viewingplatfrom with 2 transforms ( yaw, pitch)
		super(1);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		pg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPlatformGeometry(pg);
	}

	// deburner
	private Transform3D tempTransform = new Transform3D();

	@Override
	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		tempTransform.set(rot, trans, 1f);
		if ((tempTransform.getType() & Transform3D.CONGRUENT) == 0)
		{
			System.out.println("bad trans for YawPitchCamDolly, check on headcam for difference");
		}
		else
		{
			getViewPlatformTransform().setTransform(tempTransform);
		}
	}

}
