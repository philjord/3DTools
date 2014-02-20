package tools3d.utils;

import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * @author pj
 * Created on 11/02/2004
 * 
 */
public class Utils3D
{
	//	infinite bounds for fast decision making
	public static BoundingSphere defaultBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY);

	private static Transform3D t = new Transform3D();

	private static Vector3f v = new Vector3f();

	private static Quat4f q = new Quat4f();

	public static synchronized Point3f getPointFromNode(Node node)
	{
		node.getLocalToVworld(t);
		t.get(v);
		return new Point3f(v);
	}

	public static synchronized Vector3f getVectorFromNode(Node node)
	{
		return new Vector3f(getPointFromNode(node));
	}

	public static synchronized TransformGroup createTranslatedTG(Vector3f translation)
	{
		TransformGroup TG = new TransformGroup();
		TG.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		Transform3D transform = new Transform3D();
		transform.setTranslation(translation);
		TG.setTransform(transform);
		return TG;
	}

	public static synchronized Transform3D createTransform(Vector3f translation)
	{
		Transform3D transform = new Transform3D();
		transform.setTranslation(translation);
		return transform;
	}

	public static synchronized Transform3D createTransform(double rotY, Vector3f translation)
	{
		Transform3D transform = new Transform3D();
		transform.rotY(rotY);
		transform.setTranslation(translation);
		return transform;
	}

	public static synchronized Vector3f defactorRotation(Node node, Vector3f trans)
	{
		Vector3f returnVector = new Vector3f(trans);
		node.getLocalToVworld(t);
		t.get(q);
		q.conjugate();
		t.setZero();
		t.set(q);
		t.transform(returnVector);
		return returnVector;
	}

	public static void setAppearance(BranchGroup bg, Color3f ambDiffColor, Color3f specColor, Color3f emissColor, float shininess)
	{

		//TODO: this needs to keep the current textures etc and just alter the new values
		Material material = new Material();
		material.setAmbientColor(ambDiffColor);
		material.setDiffuseColor(ambDiffColor);
		material.setSpecularColor(specColor);
		material.setEmissiveColor(emissColor);
		material.setShininess(shininess);

		material.setLightingEnable(true);
		material.setColorTarget(Material.SPECULAR);

		Appearance appearance = new Appearance();
		appearance.setMaterial(material);

		PolygonAttributes targetPolyAttr = new PolygonAttributes();
		targetPolyAttr.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		appearance.setPolygonAttributes(targetPolyAttr);

		Enumeration<?> enumeration = bg.getAllChildren();
		while (enumeration.hasMoreElements())
		{
			Object o = enumeration.nextElement();
			if (o instanceof Shape3D)
			{
				Shape3D s3d = (Shape3D) o;
				s3d.setAppearance(appearance);
			}
		}
	}

	public static boolean isCongruent(Transform3D t1)
	{
		return ((t1.getType() & Transform3D.CONGRUENT) != 0);
	}

	public static float inRangeAngle(float angle, float range)
	{
		angle += angle < -range ? (range * 2f) : 0;
		angle -= angle > range ? (range * 2f) : 0;
		return angle;
	}

	public static String outputTransform(Transform3D t2)
	{
		if (t2 != null)
		{
			Vector3f v2 = new Vector3f();
			t2.get(v2);
			return "Transform " + outputTrans(t2) + " :: " + outputRot(t2);
		}
		else
		{
			return "null";
		}
	}

	public static String outputTrans(Transform3D t2)
	{
		if (t2 != null)
		{
			Vector3f v2 = new Vector3f();
			t2.get(v2);
			return "Trans:" + v2;
		}
		else
		{
			return "null";
		}
	}

	public static String outputRot(Transform3D t2)
	{
		if (t2 != null)
		{
			Quat4f v2 = new Quat4f();
			t2.get(v2);
			return "Quat:" + v2;
		}
		else
		{
			return "null";
		}
	}
}
