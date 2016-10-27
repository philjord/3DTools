package tools3d.camera;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

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
