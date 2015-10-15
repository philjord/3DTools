import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

public final class SSCCE
{

	public static void main(String[] args)
	{
		SimpleUniverse universe = new SimpleUniverse();
		BranchGroup group = new BranchGroup();
		group.setCapability(Group.ALLOW_CHILDREN_WRITE);
		group.setCapability(Group.ALLOW_CHILDREN_EXTEND);

		Transform3D viewTransform = new Transform3D();
		viewTransform.lookAt(new Point3d(0, 10, 200), new Point3d(), new Vector3d(0, 1, 0));
		viewTransform.invert();
		universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTransform);
		universe.getViewer().getView().setBackClipDistance(1000);

		// lights
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
		Color3f light1Color = new Color3f(.7f, .7f, .7f);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		group.addChild(light1);
		Color3f ambientColor = new Color3f(.4f, .4f, .4f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		group.addChild(ambientLightNode);
		universe.addBranchGraph(group);

		// 2 shared components, shared amoungst all sub-branch nodes
		Material mat = new Material();
		ColoringAttributes ca = new ColoringAttributes();

		// prepare scenegraph 
		// assume this occurs in a seperate thread, with lots of disk access etc. 
		BranchGroup root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_DETACH);

		for (int i = 0; i < 10000; i++)
		{
			TransformGroup tg = new TransformGroup();
			Transform3D t = new Transform3D();
			t.setTranslation(new Vector3d((i % 30) - 15, 0, -(i / 30)));
			tg.setTransform(t);

			Shape3D shape = new Shape3D();
			TriangleArray tri = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
			tri.setCoordinate(0, new Point3f(0.5f, 0.0f, 0.0f));
			tri.setCoordinate(1, new Point3f(0.0f, 0.5f, 0.0f));
			tri.setCoordinate(2, new Point3f(-0.5f, 0.0f, 0.0f));
			tri.setColor(0, new Color3f((float) Math.random(), 0.0f, 0.0f));
			tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
			tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));

			Appearance app = new Appearance();
			app.setMaterial(mat);
			app.setColoringAttributes(ca);
			PolygonAttributes pa = new PolygonAttributes();
			pa.setCullFace(PolygonAttributes.CULL_NONE);
			app.setPolygonAttributes(pa);
			shape.setAppearance(app);
			tg.addChild(shape);
			root.addChild(tg);
		}
		root.compile();

		//Rendering started, add and remove are now from "live" graph
		// assume this occurs in a behaviour to work around bug 193

		long start = System.currentTimeMillis();
		group.addChild(root);

		System.out.println("add complete in " + (System.currentTimeMillis() - start) + "ms");

		// now detach the whole lot in a single simple call
		start = System.currentTimeMillis();
		root.detach();
		System.out.println("remove complete in " + (System.currentTimeMillis() - start) + "ms");

	}
}
