package org.jogamp.java3d;

import javax.vecmath.Point3f;

import org.jogamp.java3d.Node;
import org.jogamp.java3d.NodeRetained;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.ViewPlatform;
import org.jogamp.java3d.ViewPlatformRetained;

public class J3dUtil
{
	public static void checkJarLoadVersion()
	{
	//	try
		{
			//boolean b = JoglesPipeline.TOOLS3D_MARKER;
			// I must hang on to b otherwise this call doesn't happen
			//b = !!b;// haha!
		}
	//	catch (Error e)
		{
		//	System.out.println("Bad load order jogl java3d is loaded ahead of 3Dtools.jar!");
		}
	}

	public static void getViewPosition(ViewPlatform vp, Point3f viewPosition)
	{
		viewPosition.x = (float) ((ViewPlatformRetained) vp.retained).schedSphere.center.x;
		viewPosition.y = (float) ((ViewPlatformRetained) vp.retained).schedSphere.center.y;
		viewPosition.z = (float) ((ViewPlatformRetained) vp.retained).schedSphere.center.z;
	}

	public static void getCurrentLocalToVworld(Node node, Transform3D out)
	{
		out.set(((NodeRetained) node.retained).getCurrentLocalToVworld());
	}
}
