package tools3d.occulsion.frustum;

// Standard imports
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector4d;

/**
 
 *
 * Because Java3D can have multiple canvases that view a single scenegraph,
 * the view frustum must be a bit more complex than the traditional case. It
 * must take into account that every canvas has its own viewplatform, and they
 * may not be looking at the same thing. That means, for culling, they will
 * need to assemble the union of all the spaces for each canvas.
 * <p>
 *
 * The frustum is for the previous Java3D frame that has just been rendered.
 *
 
 */
public class ViewFrustum extends Frustum
{
	/** All the canvases that this frustum belongs to */
	private Canvas3D[] canvases;

	// Working vars for projection handling */
	private Transform3D leftInverseProjection;

	private Transform3D rightInverseProjection;

	/**
	 * Create a new instance that operates on just a single canvas.
	 *
	 * @param canvas The canvas to use for this frustum
	 */
	public ViewFrustum(Canvas3D canvas)
	{
		super(1);

		canvases = new Canvas3D[1];
		canvases[0] = canvas;

		init();
	}

	/**
	 * Creates new ViewFrustum that represents the collection of all canvases.
	 *
	 * @param canvasList The list of canvases to view
	 */
	public ViewFrustum(Canvas3D[] canvasList)
	{
		super(canvasList.length);

		canvases = canvasList;

		init();
	}

	/**
	 * Perform common initialisation routines.
	 */
	private void init()
	{
		leftInverseProjection = new Transform3D();
		rightInverseProjection = new Transform3D();
	}

	/**
	 * Request from the renderer-specific canvas the inverse projection
	 * matrix for the given canvasId.
	 *
	 * @param id The ID of the canvas
	 * @param matrix The matrix to copy the data into
	 */
	protected void getInverseWorldProjection(int id, Matrix4d matrix)
	{
		canvases[id].getInverseVworldProjection(leftInverseProjection, rightInverseProjection);
		leftInverseProjection.get(matrix);
	}

	protected void computeFrustumPlanes(int canvasId)
	{
		super.computeFrustumPlanes(canvasId);
	}

	/** 
	* Tests for intersection of box and frustum.
	* @param frustum  
	* @return true if they intersect 
	*/
	public boolean intersect(BoundingBox box)
	{
		for (int c = 0; c < numCanvases; c++)
		{
			Canvas3DFrustum frustum = frustums[c];
			Point3d upper = new Point3d();
			box.getUpper(upper);
			Point3d lower = new Point3d();
			box.getLower(lower);

			// check if all box points out any frustum plane  
			int i = 5;
			while (i >= 0)
			{
				Vector4d vc = frustum.frustumPlanes[i--];
				if (((upper.x * vc.x + upper.y * vc.y + upper.z * vc.z + vc.w) < 0.0)
						&& ((upper.x * vc.x + lower.y * vc.y + upper.z * vc.z + vc.w) < 0.0)
						&& ((upper.x * vc.x + lower.y * vc.y + lower.z * vc.z + vc.w) < 0.0)
						&& ((upper.x * vc.x + upper.y * vc.y + lower.z * vc.z + vc.w) < 0.0)
						&& ((lower.x * vc.x + upper.y * vc.y + upper.z * vc.z + vc.w) < 0.0)
						&& ((lower.x * vc.x + lower.y * vc.y + upper.z * vc.z + vc.w) < 0.0)
						&& ((lower.x * vc.x + lower.y * vc.y + lower.z * vc.z + vc.w) < 0.0)
						&& ((lower.x * vc.x + upper.y * vc.y + lower.z * vc.z + vc.w) < 0.0))
				{
					// all corners outside this frustum plane
					// System.err.println("*** all corners outside this frustum plane");
					return false;
				}
			}
		}

		return true;
	}

	/**	Projecting points onto planes
	 * 
	 * P(x,y,z) = 3d point which i want to project on the plane.
	Plane Ax + By + Cz  + d  = 0 , so normal n = (A,B,C)
		E(ex,ey,ez) = A point in plane ( eye pos of camera )
	The closest point is along the normal to the plane. So define a point Q that is offset from P along that normal.
	Q = P - n*t
	Then solve for t that puts Q in the plane:
	dot(Q,n) + d = 0
	dot(P-n*t,n) + d = 0
	dot(P,n) - t*dot(n,n) = -d
	t = (dot(P,n)+d)/dot(n,n)
	Where dot((x1,y1,z1),(x2,y2,z2)) = x1*x2 + y1*y2 + z1*z2
	check if 2 coplanar faces overlap
	now converting colinear points into 2d point
	to transfrom into 2d points on plane I need the 2 axes vectors 
	(or at least 2 points using the cross of normal to make another axis)	
	*/
	/*public boolean canSeeFaceThroughFaces(Vector3d[] face, Vector3d[][] throughFaces)
	{
		//As seen in Frustum .frustumPlanes[4] is eye (front) plane
		Vector4d eyePlane = frustums[0].frustumPlanes[4];
		Vector3d[] projectedFace = projectFace(face, eyePlane);
		// now to compare with the throughFAces to see if there is in fact overlap of all
	//	for( int i = 0 ; i < throughFaces.length;i++)
		{
			//TODO: maybe finish this one day?
		}
		

		return false;

	}*/

	/**
	 * face is 4 points in projected face is filled wiht 4 points projected onto plane
	 * if null they will be filled
	 * @param face
	 * @param projectedFace
	 */
	/*public static Vector3d[] projectFace(Vector3d[] face, Vector4d plane)
	{
		return projectFace(face, plane, new Vector3d[4]);
	}*/

	/*public static Vector3d[] projectFace(Vector3d[] face, Vector4d plane, Vector3d[] projectedFace)
	{
		Vector3d n = new Vector3d(plane.x, plane.y, plane.z);
		double d = plane.w;

		for (int i = 0; i < 4; i++)
		{
			if (projectedFace[i] != null)
			{
				projectedFace[i].set(face[i]);
			}
			else
			{
				projectedFace[i] = new Vector3d(face[i]);
			}

			Vector3d temp = new Vector3d();
			temp.set(n);

			double t = (projectedFace[i].dot(n) + d) / n.dot(n);
			temp.scale(t);

			projectedFace[i].sub(temp);
		}

		return projectedFace;
	}*/
}
