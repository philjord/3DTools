package tools3d.utils;

import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Transform3DSoper
{
	public static String outputTransform(Transform3D t)
	{
		if (t != null)
		{
			Vector3f v = new Vector3f();
			t.get(v);
			return "Transform " + outputTrans(t) + " :: " + outputRot(t);
		}
		else
		{
			return "null";
		}
	}

	public static String outputTrans(Transform3D t)
	{
		if (t != null)
		{
			Vector3f v = new Vector3f();
			t.get(v);
			return "Trans:" + v;
		}
		else
		{
			return "null";
		}
	}

	public static String outputRot(Transform3D t)
	{
		if (t != null)
		{
			Quat4f v = new Quat4f();
			t.get(v);
			return "Quat:" + v;
		}
		else
		{
			return "null";
		}
	}
}
