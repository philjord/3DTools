package tools3d.utils;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

/**
 * @author pj
 *
 */
public class PointAtFactory extends Transform3D
{
	private static Vector3d defaultUpVector = new Vector3d(0, 1, 0);

	public static Transform3D createTransform3D(Point3d center, Point3d target)
	{
		Transform3D trans = new Transform3D();
		trans.lookAt(center, target, defaultUpVector);
		trans.invert();
		return trans;
	}

	public static Transform3D createTransform3D(Point3f center, Point3f target)
	{
		return createTransform3D(new Point3d(center), new Point3d(target));
	}

	public static Transform3D createTransform3D(Point3d center, Point3d target, boolean levelInY)
	{
		if (levelInY)
		{
			target.y = center.y;
		}
		Transform3D trans = new Transform3D();
		trans.lookAt(center, target, defaultUpVector);
		trans.invert();
		return trans;
	}

	public static Transform3D createTransform3D(Point3f center, Point3f target, boolean levelInY)
	{
		return createTransform3D(new Point3d(center), new Point3d(target), levelInY);
	}
}
