package tools3d.camera;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class NullCamDolly extends Dolly
{
	public NullCamDolly()
	{
	}

	@Override
	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		// do nothing
	}

	@Override
	public void setHudShape(BranchGroup hudShapeRoot)
	{
		// do nothing
	}

}
