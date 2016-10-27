package tools3d.utils.scenegraph;

import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

public interface LocationUpdateListener
{
	public void locationUpdated(Quat4f rot, Vector3f trans);
}
