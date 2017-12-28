package tools3d.camera;

import org.jogamp.java3d.Transform3D;
import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

public class YawPitchCamDolly extends Dolly
{
	/**
	 * This camera assumes it is attached inside the scene graph and moves with the object attached to
	 *
	 */
	public YawPitchCamDolly()
	{
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
