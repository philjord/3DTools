package tools3d.occulsion.frustum;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.vecmath.Point4d;

public class PortalFrustum extends ViewFrustum
{

	public PortalFrustum(Canvas3D canvas)
	{
		super(canvas);
	}

	/**
	 * Don't use this use setBackFrustumPoints
	 * @see tools3d.occulsion.frustum.ViewFrustum#computeFrustumPlanes(int)
	 */
	protected void computeFrustumPlanes(int canvasId)
	{
		//disabled for now, setBackFrustumPoints only
	}

	public void setBackFrustumPoints(Point4d[] backFrustumPoints)
	{

		frustumPoints[0].set(-1.0, -1.0, 1.0, 1.0); // lower-left-front
		frustumPoints[1].set(-1.0, 1.0, 1.0, 1.0); // upper-left-front
		frustumPoints[2].set(1.0, 1.0, 1.0, 1.0); // upper-right-front
		frustumPoints[3].set(1.0, -1.0, 1.0, 1.0); // lower-right-front

		//************ these are about to be overwritten, left in only for safety	

		frustumPoints[4].set(-1.0, -1.0, -1.0, 1.0); // lower-left-back
		frustumPoints[5].set(-1.0, 1.0, -1.0, 1.0); // upper-left-back
		frustumPoints[6].set(1.0, 1.0, -1.0, 1.0); // upper-right-back
		frustumPoints[7].set(1.0, -1.0, -1.0, 1.0); // lower-right-back

		//*********************
		for (int c = 0; c < numCanvases; c++)
		{
			getInverseWorldProjection(c, inverseProjection);

			for (int i = 0; i < frustumPoints.length; i++)
			{
				inverseProjection.transform(frustumPoints[i]);
				double w_inv = 1.0 / frustumPoints[i].w;
				frustumPoints[i].x *= w_inv;
				frustumPoints[i].y *= w_inv;
				frustumPoints[i].z *= w_inv;
			}

			frustumPoints[4].set(backFrustumPoints[0]); // lower-left-back
			frustumPoints[5].set(backFrustumPoints[1]); // upper-left-back
			frustumPoints[6].set(backFrustumPoints[2]); // upper-right-back
			frustumPoints[7].set(backFrustumPoints[3]); // lower-right-back

			updatePlanes(c);
		}

	}

}
