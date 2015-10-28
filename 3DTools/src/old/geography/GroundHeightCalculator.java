package old.geography;

import javax.media.j3d.Geometry;
import javax.media.j3d.PickRay;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;

/**
 * @author pj
 *
 */
public class GroundHeightCalculator
{
	//static gravity direction vector
	private static final Vector3d Y_DOWN = new Vector3d(0, -1, 0);

	// check for height from a position above our heads in case we are below the triangle
	// this allows for a ray in one direction not a line (is that faster?)
	private static final double OVER_HEIGHT_Y = 1000;

	public static synchronized double getGroundY(double x, double z, Shape3D groundShape3D)
	{
		Geometry geometry = groundShape3D.getGeometry(0);
		if (geometry != null && geometry instanceof TriangleStripArray)
		{
			return getGroundY(x, z, (TriangleStripArray) geometry);
		}
		else
		{
			new Exception("Geometry is NULL or not a triangle strip array!").printStackTrace();
		}
		return Double.MIN_VALUE;
	}

	public static synchronized void getGroundNormal(Vector3d normal, double x, double z, Shape3D groundShape3D)
	{
		//Let's just assume the first geometry is the one we want, fuck it.
		Geometry geometry = groundShape3D.getGeometry(0);
		if (geometry != null && geometry instanceof TriangleStripArray)
		{
			getGroundNormal(normal, x, z, (TriangleStripArray) geometry);
		}
		else
		{
			new Exception("Geometry is NULL or not a triangle strip array!").printStackTrace();
			normal.set(0, 0, 0);
		}
	}

	public static synchronized double getGroundY(double x, double z, TriangleStripArray ground)
	{
		PickResult result = getGroundPickResult(x, z, ground);
		if (result != null && result.numIntersections() > 0)
		{
			PickIntersection intersection = result.getIntersection(0);
			return OVER_HEIGHT_Y - intersection.getDistance();
		}
		else
		{
			return Double.MIN_VALUE;
		}
	}

	//deburners
	private static Vector3d edge1 = new Vector3d();

	private static Vector3d edge2 = new Vector3d();

	/**
	 *  store resuiltant normal in normal vector
	 * @param normal
	 * @param x
	 * @param z
	 * @param ground
	 * @return
	 */
	public static synchronized void getGroundNormal(Vector3d normal, double x, double z, TriangleStripArray ground)
	{
		PickResult result = getGroundPickResult(x, z, ground);

		if (result != null && result.numIntersections() > 0)
		{
			PickIntersection intersection = result.getIntersection(0);

			Point3d[] cos = intersection.getPrimitiveCoordinates();
			edge1.sub(cos[1], cos[0]);
			edge2.sub(cos[2], cos[0]);

			normal.cross(edge1, edge2);

			//	flip it to point "up" because the windings might be not CCW
			if (normal.y < 0)
			{
				normal.negate();
			}
			normal.normalize();
		}
		else
		{
			normal.set(0, 0, 0);
		}
	}

	//	Deburners
	private static Point3f vert0 = new Point3f();

	private static Point3f vert1 = new Point3f();

	private static Point3f vert2 = new Point3f();

	private static Point3f vert3 = new Point3f();

	private static Point3d groundPoint = new Point3d();

	private static Shape3D intersectShape = new Shape3D();

	private static PickRay pickRay = new PickRay();

	private static TriangleArray tris = new TriangleArray(6, TriangleArray.COORDINATES);

	// no virtualworld transform
	private static Transform3D VWTransform3D = new Transform3D();

	private static synchronized PickResult getGroundPickResult(double x, double z, TriangleStripArray ground)
	{
		// lets check from way the hell above our selves in case we are below 
		// the ground, this value is removed at the end
		groundPoint.set(x, OVER_HEIGHT_Y, z);

		float[] coords = ground.getCoordRefFloat();

		int numStrips = ground.getNumStrips();
		int[] strips = new int[numStrips];
		ground.getStripVertexCounts(strips);

		int numVertex = strips[0];

		//use the first 3 vertexes to work out the size of each strip
		vert0.set(coords[0], coords[1], coords[2]);
		vert1.set(coords[3], coords[4], coords[5]);
		vert2.set(coords[6], coords[7], coords[8]);

		float stripDist = vert1.z - vert0.z;
		float vertDist = vert2.x - vert0.x;

		// lets find the strip that applies 	
		// now this is tricky, but basically I need 0,0 to be the bottom left corner of these verts, not the center	
		int stripIdx = (int) Math.floor((groundPoint.z - vert0.z) / stripDist);
		// and get the vert idx
		int vertIdx = (int) Math.floor((groundPoint.x - vert0.x) / vertDist);

		// we will simply check both triangles at the intersection of the indexes
		int firstVert = ((stripIdx * numVertex) + (vertIdx * 2)) * 3;

		// make sure we don't go out of bounds
		if (firstVert + 11 >= coords.length)
			return null;

		vert0.set(coords[firstVert], coords[firstVert + 1], coords[firstVert + 2]);
		vert1.set(coords[firstVert + 3], coords[firstVert + 4], coords[firstVert + 5]);
		vert2.set(coords[firstVert + 6], coords[firstVert + 7], coords[firstVert + 8]);
		vert3.set(coords[firstVert + 9], coords[firstVert + 10], coords[firstVert + 11]);

		tris.setCoordinate(0, vert0);
		tris.setCoordinate(1, vert1);
		tris.setCoordinate(2, vert2);
		tris.setCoordinate(3, vert1);
		tris.setCoordinate(4, vert2);
		tris.setCoordinate(5, vert3);

		//TODO: this one below produces bad intersections? why?
		//tris.setCoordinates(0, coords, firstVert / 3, 4);
		intersectShape.setGeometry(tris);

		pickRay.set(groundPoint, Y_DOWN);

		return new PickResult(intersectShape, VWTransform3D, pickRay);
	}

}
