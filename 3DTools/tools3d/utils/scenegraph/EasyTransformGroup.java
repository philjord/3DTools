package tools3d.utils.scenegraph;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.GMatrix;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

/**
 * @author Administrator
 *
 */
public class EasyTransformGroup extends TransformGroup
{
	private Transform3D transform;

	public EasyTransformGroup()
	{
		transform = new Transform3D();
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(double[] matrix)
	{
		transform = new Transform3D(matrix);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(float[] matrix)
	{
		transform = new Transform3D(matrix);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(GMatrix m1)
	{
		transform = new Transform3D(m1);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Matrix3d m1, Vector3d t1, double s)
	{
		transform = new Transform3D(m1, t1, s);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Matrix3f m1, Vector3d t1, double s)
	{
		transform = new Transform3D(m1, t1, s);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Matrix3f m1, Vector3f t1, float s)
	{
		transform = new Transform3D(m1, t1, s);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Matrix4d m1)
	{
		transform = new Transform3D(m1);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Matrix4f m1)
	{
		transform = new Transform3D(m1);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Quat4d q1, Vector3d t1, double s)
	{
		transform = new Transform3D(q1, t1, s);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Quat4f q1, Vector3d t1, double s)
	{
		transform = new Transform3D(q1, t1, s);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Quat4f q1, Vector3f t1, float s)
	{
		transform = new Transform3D(q1, t1, s);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public EasyTransformGroup(Transform3D t1)
	{
		transform = new Transform3D(t1);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setTransform(transform);
	}

	public void add(Transform3D t1)
	{
		transform.add(t1);
		setTransform(transform);
	}

	public void add(Transform3D t1, Transform3D t2)
	{
		transform.add(t1, t2);
		setTransform(transform);
	}

	public double determinant()
	{
		return transform.determinant();
	}

	public boolean epsilonEquals(Transform3D t1, double epsilon)
	{
		return transform.epsilonEquals(t1, epsilon);
	}

	public boolean equals(java.lang.Object o1)
	{
		return transform.equals(o1);
	}

	public boolean equals(Transform3D t1)
	{
		return transform.equals(t1);
	}

	public void frustum(double left, double right, double bottom, double top, double near, double far)
	{
		transform.frustum(left, right, bottom, top, near, far);
		setTransform(transform);
	}

	public void get(double[] matrix)
	{
		transform.get(matrix);
	}

	public void get(float[] matrix)
	{
		transform.get(matrix);
	}

	public void get(Matrix3d m1)
	{
		transform.get(m1);
	}

	public double get(Matrix3d m1, Vector3d t1)
	{
		return transform.get(m1, t1);
	}

	public void get(Matrix3f m1)
	{
		transform.get(m1);
	}

	public double get(Matrix3f m1, Vector3d t1)
	{
		return transform.get(m1, t1);
	}

	public float get(Matrix3f m1, Vector3f t1)
	{
		return transform.get(m1, t1);
	}

	public void get(Matrix4d matrix)
	{
		transform.get(matrix);
	}

	public void get(Matrix4f matrix)
	{
		transform.get(matrix);
	}

	public void get(Quat4d q1)
	{
		transform.get(q1);
	}

	public double get(Quat4d q1, Vector3d t1)
	{
		return transform.get(q1, t1);
	}

	public void get(Quat4f q1)
	{
		transform.get(q1);
	}

	public double get(Quat4f q1, Vector3d t1)
	{
		return transform.get(q1, t1);
	}

	public float get(Quat4f q1, Vector3f t1)
	{
		return transform.get(q1, t1);
	}

	public void get(Vector3d trans)
	{
		transform.get(trans);
	}

	public void get(Vector3f trans)
	{
		transform.get(trans);
	}

	public boolean getAutoNormalize()
	{
		return transform.getAutoNormalize();
	}

	public int getBestType()
	{
		return transform.getBestType();
	}

	public boolean getDeterminantSign()
	{
		return transform.getDeterminantSign();
	}

	public void getRotationScale(Matrix3d m1)
	{
		transform.getRotationScale(m1);
	}

	public void getRotationScale(Matrix3f m1)
	{
		transform.getRotationScale(m1);
	}

	public double getScale()
	{
		return transform.getScale();
	}

	public void getScale(Vector3d scale)
	{
		transform.getScale(scale);
	}

	public int getType()
	{
		return transform.getType();
	}

	public int hashCode()
	{
		return transform.hashCode();
	}

	public void invert()
	{
		transform.invert();
		setTransform(transform);
	}

	public void invert(Transform3D t1)
	{
		transform.invert(t1);
		setTransform(transform);
	}

	public void lookAt(Point3d eye, Point3d center, Vector3d up)
	{
		transform.lookAt(eye, center, up);
		setTransform(transform);
	}

	public void mul(double scalar)
	{
		transform.mul(scalar);
		setTransform(transform);
	}

	public void mul(double scalar, Transform3D t1)
	{
		transform.mul(scalar, t1);
		setTransform(transform);
	}

	public void mul(Transform3D t1)
	{
		transform.mul(t1);
		setTransform(transform);
	}

	public void mul(Transform3D t1, Transform3D t2)
	{
		transform.mul(t1, t2);
		setTransform(transform);
	}

	public void mulInverse(Transform3D t1)
	{
		transform.mulInverse(t1);
		setTransform(transform);
	}

	public void mulInverse(Transform3D t1, Transform3D t2)
	{
		transform.mulInverse(t1, t2);
		setTransform(transform);
	}

	public void mulTransposeBoth(Transform3D t1, Transform3D t2)
	{
		transform.mulTransposeBoth(t1, t2);
		setTransform(transform);
	}

	public void mulTransposeLeft(Transform3D t1, Transform3D t2)
	{
		transform.mulTransposeLeft(t1, t2);
		setTransform(transform);
	}

	public void mulTransposeRight(Transform3D t1, Transform3D t2)
	{
		transform.mulTransposeRight(t1, t2);
		setTransform(transform);
	}

	public void normalize()
	{
		transform.normalize();
		setTransform(transform);
	}

	public void normalize(Transform3D t1)
	{
		transform.normalize(t1);
		setTransform(transform);
	}

	public void normalizeCP()
	{
		transform.normalizeCP();
		setTransform(transform);
	}

	public void normalizeCP(Transform3D t1)
	{
		transform.normalizeCP(t1);
		setTransform(transform);
	}

	public void ortho(double left, double right, double bottom, double top, double near, double far)
	{
		transform.ortho(left, right, bottom, top, near, far);
		setTransform(transform);
	}

	public void perspective(double fovx, double aspect, double zNear, double zFar)
	{
		transform.perspective(fovx, aspect, zNear, zFar);
		setTransform(transform);
	}

	public void rotX(double angle)
	{
		transform.rotX(angle);
		setTransform(transform);
	}

	public void rotY(double angle)
	{
		transform.rotY(angle);
		setTransform(transform);
	}

	public void rotZ(double angle)
	{
		transform.rotZ(angle);
		setTransform(transform);
	}

	public void scaleAdd(double s, Transform3D t1)
	{
		transform.scaleAdd(s, t1);
		setTransform(transform);
	}

	public void scaleAdd(double s, Transform3D t1, Transform3D t2)
	{
		transform.scaleAdd(s, t1, t2);
		setTransform(transform);
	}

	public void set(AxisAngle4d a1)
	{
		transform.set(a1);
		setTransform(transform);
	}

	public void set(AxisAngle4f a1)
	{
		transform.set(a1);
		setTransform(transform);
	}

	public void set(double scale)
	{
		transform.set(scale);
		setTransform(transform);
	}

	public void set(double[] matrix)
	{
		transform.set(matrix);
		setTransform(transform);
	}

	public void set(double scale, Vector3d v1)
	{
		transform.set(scale, v1);
		setTransform(transform);
	}

	public void set(float[] matrix)
	{
		transform.set(matrix);
		setTransform(transform);
	}

	public void set(float scale, Vector3f v1)
	{
		transform.set(scale, v1);
		setTransform(transform);
	}

	public void set(GMatrix matrix)
	{
		transform.set(matrix);
		setTransform(transform);
	}

	public void set(Matrix3d m1)
	{
		transform.set(m1);
		setTransform(transform);
	}

	public void set(Matrix3d m1, Vector3d t1, double s)
	{
		transform.set(m1, t1, s);
		setTransform(transform);
	}

	public void set(Matrix3f m1)
	{
		transform.set(m1);
		setTransform(transform);
	}

	public void set(Matrix3f m1, Vector3d t1, double s)
	{
		transform.set(m1, t1, s);
		setTransform(transform);
	}

	public void set(Matrix3f m1, Vector3f t1, float s)
	{
		transform.set(m1, t1, s);
		setTransform(transform);
	}

	public void set(Matrix4d m1)
	{
		transform.set(m1);
		setTransform(transform);
	}

	public void set(Matrix4f m1)
	{
		transform.set(m1);
		setTransform(transform);
	}

	public void set(Quat4d q1)
	{
		transform.set(q1);
		setTransform(transform);
	}

	public void set(Quat4d q1, Vector3d t1, double s)
	{
		transform.set(q1, t1, s);
		setTransform(transform);
	}

	public void set(Quat4f q1)
	{
		transform.set(q1);
		setTransform(transform);
	}

	public void set(Quat4f q1, Vector3d t1, double s)
	{
		transform.set(q1, t1, s);
		setTransform(transform);
	}

	public void set(Quat4f q1, Vector3f t1, float s)
	{
		transform.set(q1, t1, s);
		setTransform(transform);
	}

	public void set(Transform3D t1)
	{
		transform.set(t1);
		setTransform(transform);
	}

	public void set(Vector3d trans)
	{
		transform.set(trans);
		setTransform(transform);
	}

	public void set(Vector3d v1, double scale)
	{
		transform.set(v1, scale);
		setTransform(transform);
	}

	public void set(Vector3f trans)
	{
		transform.set(trans);
		setTransform(transform);
	}

	public void set(Vector3f v1, float scale)
	{
		transform.set(v1, scale);
		setTransform(transform);
	}

	public void setAutoNormalize(boolean autoNormalize)
	{
		transform.setAutoNormalize(autoNormalize);
		setTransform(transform);
	}

	public void setEuler(Vector3d euler)
	{
		transform.setEuler(euler);
		setTransform(transform);
	}

	public void setIdentity()
	{
		transform.setIdentity();
		setTransform(transform);
	}

	public void setRotation(AxisAngle4d a1)
	{
		transform.setRotation(a1);
		setTransform(transform);
	}

	public void setRotation(AxisAngle4f a1)
	{
		transform.setRotation(a1);
		setTransform(transform);
	}

	public void setRotation(Matrix3d m1)
	{
		transform.setRotation(m1);
		setTransform(transform);
	}

	public void setRotation(Matrix3f m1)
	{
		transform.setRotation(m1);
		setTransform(transform);
	}

	public void setRotation(Quat4d q1)
	{
		transform.setRotation(q1);
		setTransform(transform);
	}

	public void setRotation(Quat4f q1)
	{
		transform.setRotation(q1);
		setTransform(transform);
	}

	public void setRotationScale(Matrix3d m1)
	{
		transform.setRotationScale(m1);
		setTransform(transform);
	}

	public void setRotationScale(Matrix3f m1)
	{
		transform.setRotationScale(m1);
		setTransform(transform);
	}

	public void setScale(double scale)
	{
		transform.setScale(scale);
		setTransform(transform);
	}

	public void setScale(Vector3d scale)
	{
		transform.setScale(scale);
		setTransform(transform);
	}

	public void setTranslation(Vector3d trans)
	{
		transform.setTranslation(trans);
		setTransform(transform);
	}

	public void setTranslation(Vector3f trans)
	{
		transform.setTranslation(trans);
		setTransform(transform);
	}

	public void setZero()
	{
		transform.setZero();
		setTransform(transform);
	}

	public void sub(Transform3D t1)
	{
		transform.sub(t1);
		setTransform(transform);
	}

	public void sub(Transform3D t1, Transform3D t2)
	{
		transform.sub(t1, t2);
		setTransform(transform);
	}

	public String toString()
	{
		return "ETG " + transform.toString();
	}

	public void transform(Point3d point)
	{
		transform.transform(point);
	}

	public void transform(Point3d point, Point3d pointOut)
	{
		transform.transform(point, pointOut);
	}

	public void transform(Point3f point)
	{
		transform.transform(point);
	}

	public void transform(Point3f point, Point3f pointOut)
	{
		transform.transform(point, pointOut);
	}

	public void transform(Vector3d normal)
	{
		transform.transform(normal);
	}

	public void transform(Vector3d normal, Vector3d normalOut)
	{
		transform.transform(normal, normalOut);
	}

	public void transform(Vector3f normal)
	{
		transform.transform(normal);
	}

	public void transform(Vector3f normal, Vector3f normalOut)
	{
		transform.transform(normal, normalOut);
	}

	public void transform(Vector4d vec)
	{
		transform.transform(vec);
	}

	public void transform(Vector4d vec, Vector4d vecOut)
	{
		transform.transform(vec, vecOut);
	}

	public void transform(Vector4f vec)
	{
		transform.transform(vec);
	}

	public void transform(Vector4f vec, Vector4f vecOut)
	{
		transform.transform(vec, vecOut);
	}

	public void transpose()
	{
		transform.transpose();
		setTransform(transform);
	}

	public void transpose(Transform3D t1)
	{
		transform.transpose(t1);
		setTransform(transform);
	}

}
