package bug;

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
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

public final class SSCCE
{
	/**SSCCE I need
			// mip map issue max level check
			// renderingattributes needed all round if stencil on
			// removeChild slowness (users)
			// postrender transform on texture
			// possibly the capsule shape intersect code?
			// reset of resolution bust the world - removeNotify
			// switches not working with links under them
			// problem that requesting a depth buffer can give back a very small depth buffer (on mac) like 8 or 16bit
			// IndexRow of GeometryInfo slowness issue
			// setTransparency in TA has no clamp or range check and bad values result is CTD TransparencyAttributes.SCREEN_DOOR
	*/

	//CTD for removeNotify
	public static void main(String[] args)
	{

		SimpleUniverse universe = new SimpleUniverse();
		BranchGroup group = new BranchGroup();

		Transform3D viewTransform = new Transform3D();
		viewTransform.lookAt(new Point3d(0, 10, 200), new Point3d(), new Vector3d(0, 1, 0));
		viewTransform.invert();
		universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTransform);
		universe.getViewer().getView().setBackClipDistance(1000);

		// lights
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
		Color3f ambientColor = new Color3f(.4f, .4f, .4f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		group.addChild(ambientLightNode);

		Shape3D shape = new Shape3D();
		TriangleArray tri = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		tri.setCoordinate(0, new Point3f(5f, 0.0f, 0.0f));
		tri.setCoordinate(1, new Point3f(0.0f, 5f, 0.0f));
		tri.setCoordinate(2, new Point3f(-5f, 0.0f, 0.0f));
		tri.setColor(0, new Color3f((float) Math.random(), 0.0f, 0.0f));
		tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
		tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));
		shape.setAppearance(new Appearance());
		shape.setGeometry(tri);
		group.addChild(shape);

		universe.addBranchGraph(group);
		
		//JFrame f = (JFrame) universe.getViewer().getCanvas3D().getParent().getParent().getParent().getParent().getParent();
		//f.validate();
		
		try
		{
			Thread.sleep(5000);
		}
		catch (Exception e)
		{
		}
// ok this doesn't crash nothing yet, need to push it harder?
	//	f.removeNotify();
	///	f.setUndecorated(true);
	//f.addNotify();

		try
		{
			Thread.sleep(5000);
		}
		catch (Exception e)
		{
		}
	}

	// remove slowness - not yet working
	public static void main2(String[] args)
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

		// not detached
		BranchGroup root1 = new BranchGroup();
		for (int i = 0; i < 5000; i++)
		{
			root1.addChild(buildGroup(mat, ca, i));
		}
		root1.compile();

		// build detrachable sub graph
		BranchGroup root2 = new BranchGroup();
		root2.setCapability(BranchGroup.ALLOW_DETACH);
		for (int i = 5000; i < 10000; i++)
		{
			root2.addChild(buildGroup(mat, ca, i));
		}
		root2.compile();

		//Rendering started, add and remove are now from "live" graph
		// assume this occurs in a behaviour to work around bug 193

		long start = System.currentTimeMillis();
		group.addChild(root1);
		group.addChild(root2);

		System.out.println("add complete in " + (System.currentTimeMillis() - start) + "ms");

		try
		{
			Thread.sleep(500);
		}
		catch (Exception e)
		{
		}

		// now detach the whole lot in a single simple call
		start = System.currentTimeMillis();
		root2.detach();
		System.out.println("remove complete in " + (System.currentTimeMillis() - start) + "ms");

		
	}

	private static Group buildGroup(Material mat, ColoringAttributes ca, int i)
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
		shape.setGeometry(tri);

		Appearance app = new Appearance();
		app.setMaterial(mat);
		app.setColoringAttributes(ca);
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(pa);
		shape.setAppearance(app);
		tg.addChild(shape);

		return tg;
	}
}
