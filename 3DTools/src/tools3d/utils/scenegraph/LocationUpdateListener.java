package tools3d.utils.scenegraph;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public interface LocationUpdateListener
{
	public void locationUpdated(Quat4f rot, Vector3f trans);
}
