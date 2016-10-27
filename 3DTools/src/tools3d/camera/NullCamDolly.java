package tools3d.camera;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.jogamp.java3d.BranchGroup;

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
