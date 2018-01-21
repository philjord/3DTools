package org.jogamp.java3d;

import org.jogamp.java3d.Node;
import org.jogamp.java3d.NodeRetained;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.ViewPlatform;
import org.jogamp.java3d.ViewPlatformRetained;
import org.jogamp.vecmath.Point3f;

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
	
	
	/**
	 * BOTH Transform3D matrices MUST be Affine!!
	 * same as t.mul(t1); but faster
	 * @param t
	 * @param t1
	 */
	public static void affineTransformMul(Transform3D t, Transform3D t1)
	{
		// we directly read the source arrays to save heap allocations here
		// Note we also write into t.mat at the end
		double[] mat = t.mat;		
		double[] t1Mat = t1.mat;
		double tmp0, tmp1, tmp2, tmp3;
		double tmp4, tmp5, tmp6, tmp7;
		double tmp8, tmp9, tmp10, tmp11;

		if (Double.isNaN(mat[0]))
		{
			new Throwable("Spotted a NaN!").printStackTrace();
		}

		tmp0 = mat[0] * t1Mat[0] + mat[1] * t1Mat[4] + mat[2] * t1Mat[8];
		tmp1 = mat[0] * t1Mat[1] + mat[1] * t1Mat[5] + mat[2] * t1Mat[9];
		tmp2 = mat[0] * t1Mat[2] + mat[1] * t1Mat[6] + mat[2] * t1Mat[10];
		tmp3 = mat[0] * t1Mat[3] + mat[1] * t1Mat[7] + mat[2] * t1Mat[11] + mat[3];
		tmp4 = mat[4] * t1Mat[0] + mat[5] * t1Mat[4] + mat[6] * t1Mat[8];
		tmp5 = mat[4] * t1Mat[1] + mat[5] * t1Mat[5] + mat[6] * t1Mat[9];
		tmp6 = mat[4] * t1Mat[2] + mat[5] * t1Mat[6] + mat[6] * t1Mat[10];
		tmp7 = mat[4] * t1Mat[3] + mat[5] * t1Mat[7] + mat[6] * t1Mat[11] + mat[7];
		tmp8 = mat[8] * t1Mat[0] + mat[9] * t1Mat[4] + mat[10] * t1Mat[8];
		tmp9 = mat[8] * t1Mat[1] + mat[9] * t1Mat[5] + mat[10] * t1Mat[9];
		tmp10 = mat[8] * t1Mat[2] + mat[9] * t1Mat[6] + mat[10] * t1Mat[10];
		tmp11 = mat[8] * t1Mat[3] + mat[9] * t1Mat[7] + mat[10] * t1Mat[11] + mat[11];

		mat[12] = mat[13] = mat[14] = 0;
		mat[15] = 1;

		mat[0] = tmp0;
		mat[1] = tmp1;
		mat[2] = tmp2;
		mat[3] = tmp3;
		mat[4] = tmp4;
		mat[5] = tmp5;
		mat[6] = tmp6;
		mat[7] = tmp7;
		mat[8] = tmp8;
		mat[9] = tmp9;
		mat[10] = tmp10;
		mat[11] = tmp11;		
	}
}
