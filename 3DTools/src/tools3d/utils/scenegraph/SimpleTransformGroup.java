package tools3d.utils.scenegraph;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.utils.Utils3D;

/**
 * @author pj
 *
 */
public class SimpleTransformGroup extends TransformGroup
{
	private Transform3D tempTransform = new Transform3D();

	public SimpleTransformGroup()
	{
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}

	public SimpleTransformGroup(Vector3f translation)
	{
		this();
		setTransform(Utils3D.createTransform(translation));
	}

	public SimpleTransformGroup(BranchGroup group)
	{
		this();
		addChild(group);
	}

	public SimpleTransformGroup(Vector3f translation, BranchGroup group)
	{
		this(translation);
		addChild(group);
	}

	public SimpleTransformGroup(Vector3f translation, Vector3f lookAt)
	{
		this();
		setTransform(createTransform3D(new Point3f(translation), new Point3f(lookAt), true));
	}

	public SimpleTransformGroup(Vector3f translation, Vector3f lookAt, BranchGroup group)
	{
		this(translation, lookAt);
		addChild(group);
	}

	// Point3f versions of the above
	public SimpleTransformGroup(Point3f translation)
	{
		this(new Vector3f(translation));
	}

	public SimpleTransformGroup(Point3f translation, BranchGroup group)
	{
		this(new Vector3f(translation), group);
	}

	public SimpleTransformGroup(Point3f translation, Point3f lookAt)
	{
		this(new Vector3f(translation), new Vector3f(lookAt));
	}

	public SimpleTransformGroup(Point3f translation, Point3f lookAt, BranchGroup group)
	{
		this(new Vector3f(translation), new Vector3f(lookAt), group);
	}

	public void setTranslation(Vector3f translation)
	{
		getTransform(tempTransform);
		tempTransform.setTranslation(translation);
		super.setTransform(tempTransform);
	}

	public void setTransform(Vector3d translation, AxisAngle4d rotation)
	{
		tempTransform.setRotation(rotation);
		tempTransform.setTranslation(translation);
		super.setTransform(tempTransform);
	}

	public void setTransform(Vector3d translation, Quat4d rotation)
	{
		tempTransform.setRotation(rotation);
		tempTransform.setTranslation(translation);
		super.setTransform(tempTransform);
	}

	public void setTransform(Transform3D transform)
	{
		super.setTransform(transform);
	}

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
