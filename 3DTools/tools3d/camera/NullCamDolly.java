package tools3d.camera;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class NullCamDolly extends Dolly
{

	public NullCamDolly()
	{
		// create the viewingplatfrom with 4 transforms (loc, yaw, headpos, pitch)
		super(1);

	}

	@Override
	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		// do nothing

	}

}
