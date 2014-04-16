package tools3d.utils.leafnode;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickTool;

public class SphereWithLightsAndMotion extends BranchGroup
{

	// Constants for type of light to use
	public static final int DIRECTIONAL_LIGHT = 0;

	public static final int POINT_LIGHT = 1;

	public static final int SPOT_LIGHT = 2;

	// Flag indicates type of lights: directional, point, or spot
	// lights.  This flag is set based on command line argument
	private int lightType = POINT_LIGHT;

	public SphereWithLightsAndMotion(int type)
	{
		lightType = type;

		Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f objColor = new Color3f(0.6f, 0.6f, 0.6f);
		Color3f lColor1 = new Color3f(1.0f, 0.0f, 0.0f);
		Color3f lColor2 = new Color3f(0.0f, 1.0f, 0.0f);
		Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);

		Transform3D t;

		// Create a Transformgroup to scale all objects so they
		// appear in the scene.
		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(0.4);
		objScale.setTransform(t3d);
		addChild(objScale);

		// Create a bounds for the lights
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Create a Sphere object, generate one copy of the sphere,
		// and add it into the scene graph.
		Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
		Appearance a = new Appearance();
		m.setLightingEnable(true);
		a.setMaterial(m);
		Sphere sph = new Sphere(1.0f, Primitive.GENERATE_NORMALS, 80, a);
		PickTool.setCapabilities(sph.getShape(Sphere.BODY), PickTool.INTERSECT_TEST);
		objScale.addChild(sph);

		// Create the transform group node for the each light and initialize
		// it to the identity.  Add them to the root
		// of the subgraph.
		TransformGroup l1RotTrans = new TransformGroup();
		l1RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objScale.addChild(l1RotTrans);

		TransformGroup l2RotTrans = new TransformGroup();
		l2RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objScale.addChild(l2RotTrans);

		// Create transformations for the positional lights
		t = new Transform3D();
		Vector3d lPos1 = new Vector3d(0.0, 0.0, 2.0);
		t.set(lPos1);
		TransformGroup l1Trans = new TransformGroup(t);
		l1RotTrans.addChild(l1Trans);

		t = new Transform3D();
		Vector3d lPos2 = new Vector3d(0.5, 0.8, 2.0);
		t.set(lPos2);
		TransformGroup l2Trans = new TransformGroup(t);
		l2RotTrans.addChild(l2Trans);

		// Create Geometry for point lights
		ColoringAttributes caL1 = new ColoringAttributes();
		ColoringAttributes caL2 = new ColoringAttributes();
		caL1.setColor(lColor1);
		caL2.setColor(lColor2);
		Appearance appL1 = new Appearance();
		Appearance appL2 = new Appearance();
		appL1.setColoringAttributes(caL1);
		appL2.setColoringAttributes(caL2);
		Sphere sl1 = new Sphere(0.05f, appL1);
		PickTool.setCapabilities(sl1.getShape(Sphere.BODY), PickTool.INTERSECT_TEST);
		l1Trans.addChild(sl1);
		Sphere sl2 = new Sphere(0.05f, appL2);
		PickTool.setCapabilities(sl2.getShape(Sphere.BODY), PickTool.INTERSECT_TEST);
		l2Trans.addChild(sl2);

		// Create lights
		AmbientLight aLgt = new AmbientLight(alColor);

		Light lgt1 = null;
		Light lgt2 = null;

		Point3f lPoint = new Point3f(0.0f, 0.0f, 0.0f);
		Point3f atten = new Point3f(1.0f, 0.1f, 0.0f);
		Vector3f lDirect1 = new Vector3f(lPos1);
		Vector3f lDirect2 = new Vector3f(lPos2);
		lDirect1.negate();
		lDirect2.negate();

		switch (lightType)
		{
			case DIRECTIONAL_LIGHT:
				lgt1 = new DirectionalLight(lColor1, lDirect1);
				lgt2 = new DirectionalLight(lColor2, lDirect2);
				break;
			case POINT_LIGHT:
				lgt1 = new PointLight(lColor1, lPoint, atten);
				lgt2 = new PointLight(lColor2, lPoint, atten);
				break;
			case SPOT_LIGHT:
				lgt1 = new SpotLight(lColor1, lPoint, atten, lDirect1, 25.0f * (float) Math.PI / 180.0f, 10.0f);
				lgt2 = new SpotLight(lColor2, lPoint, atten, lDirect2, 25.0f * (float) Math.PI / 180.0f, 10.0f);
				break;
		}

		// Set the influencing bounds
		aLgt.setInfluencingBounds(bounds);
		lgt1.setInfluencingBounds(bounds);
		lgt2.setInfluencingBounds(bounds);

		// Add the lights into the scene graph
		objScale.addChild(aLgt);
		l1Trans.addChild(lgt1);
		l2Trans.addChild(lgt2);

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into the
		// scene graph.
		Transform3D yAxis = new Transform3D();
		Alpha rotor1Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);
		RotationInterpolator rotator1 = new RotationInterpolator(rotor1Alpha, l1RotTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rotator1.setSchedulingBounds(bounds);
		l1RotTrans.addChild(rotator1);

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into the
		// scene graph.
		Alpha rotor2Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 1000, 0, 0, 0, 0, 0);
		RotationInterpolator rotator2 = new RotationInterpolator(rotor2Alpha, l2RotTrans, yAxis, 0.0f, 0.0f);
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		rotator2.setSchedulingBounds(bounds);
		l2RotTrans.addChild(rotator2);

	}

}
