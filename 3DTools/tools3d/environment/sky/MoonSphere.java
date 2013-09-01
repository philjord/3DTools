package tools3d.environment.sky;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

public class MoonSphere extends BranchGroup
{
	private TransformGroup pos = new TransformGroup();

	public MoonSphere()
	{
		Color3f eColor = new Color3f(1.0f, 1.0f, 1.0f);

		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(0, -100, 0));
		pos.setTransform(t3d);
		addChild(pos);

		// Create a Sphere object, generate one copy of the sphere,
		// and add it into the scene graph.
		Material m = new Material(new Color3f(), eColor, new Color3f(), new Color3f(), 1.0f);
		Appearance a = new Appearance();
		m.setLightingEnable(true);
		a.setMaterial(m);
		Sphere sph = new Sphere(2.0f, Primitive.GENERATE_NORMALS, 80, a);
		pos.addChild(sph);

		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}

	public void setPosition(Vector3f posVec)
	{
		Transform3D t = new Transform3D();
		t.set(posVec);
		pos.setTransform(t);
	}
}
