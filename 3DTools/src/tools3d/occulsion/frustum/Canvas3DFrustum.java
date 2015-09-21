package tools3d.occulsion.frustum;

import javax.media.j3d.BoundingPolytope;
import javax.vecmath.Point3d;
import javax.vecmath.Vector4d;

/**
 * ViewFrustum planes for a single Canvas3D and determining if
 * a triangle or point is visible.
 * <p>
 *
 * The frustum is for the previous Java3D frame that has just been rendered.
 *
 * @author Paul Byrne, Justin Couch
 * @version $Revision: 1.3 $
 */
public class Canvas3DFrustum
{
	/** The geometry is in the view frustum, either partially or completely */
	static final int IN = 0x1;

	/** The geometry is outside the view frustum */
	static final int OUT = 0x2;

	/** The geometry has been clipped to the view frustum */
	static final int CLIPPED = 0x4;

	/** The planes describing this frustum */
	Vector4d[] frustumPlanes;

	BoundingPolytope bounds = new BoundingPolytope();

	/**
	 * Create a new, default frustum for a canvas.
	 */
	Canvas3DFrustum()
	{
		frustumPlanes = new Vector4d[6];

		frustumPlanes[0] = new Vector4d();
		frustumPlanes[1] = new Vector4d();
		frustumPlanes[2] = new Vector4d();
		frustumPlanes[3] = new Vector4d();
		frustumPlanes[4] = new Vector4d();
		frustumPlanes[5] = new Vector4d();

	}

	/**
	 * Check if the triangle is in the Frustum, in some cases this may
	 * indicate a triangle is in the frustum when it is not, however the
	 * converse is not true.
	 *
	 * @return IN, OUT or CLIPPED
	 */
	int isTriangleInFrustum(Point3d p1, Point3d p2, Point3d p3)
	{
		int out0 = 0;
		int out1 = 0;
		int out2 = 0;

		for (int i = 0; i < 6; i++)
		{
			if (getDistanceFromPlane(p1, frustumPlanes[i]) <= 0.0f)
				out0 |= (1 << i);
			if (getDistanceFromPlane(p2, frustumPlanes[i]) <= 0.0f)
				out1 |= (1 << i);
			if (getDistanceFromPlane(p3, frustumPlanes[i]) <= 0.0f)
				out2 |= (1 << i);
		}

		int ret_val = CLIPPED;

		if (out0 == 0 && out1 == 0 && out2 == 0)
			ret_val = IN;
		else if ((out0 != 0 && out1 != 0 && out2 != 0) && ((out0 & out1 & out2) != 0))
			ret_val = OUT;

		return ret_val;
	}

	/**
	 * Determine if the point is within the frustum.
	 *
	 * @param point The location to check
	 * @return true if the point is in the frustum volume
	 */
	public boolean isPointInFrustum(Point3d point)
	{
		for (int i = 0; i < 6; i++)
		{
			if (getDistanceFromPlane(point, frustumPlanes[i]) <= 0.0f)
				return false;
		}

		return true;
	}

	/**
	 * Convenience method to find how far a point is from the plane
	 *
	 * @param point The point to check
	 * @param plane The vector representing the plane
	 * @return The distance
	 */
	private double getDistanceFromPlane(Point3d point, Vector4d plane)
	{
		double dot = point.x * plane.x + point.y * plane.y + point.z * plane.z;

		//System.out.println( point +"  "+(dot+(float)plane.w) );

		return dot + plane.w;
	}

	public String toString()
	{
		return "" + this.getClass().getSimpleName() + "\n" + frustumPlanes[0] + "\n" + frustumPlanes[1] + "\n" + frustumPlanes[2] + "\n"
				+ frustumPlanes[3] + "\n" + frustumPlanes[4] + "\n" + frustumPlanes[5] + "\n";
	}
}
