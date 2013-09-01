package tools3d.navigation.picking;

import java.util.ArrayList;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PickBounds;
import javax.media.j3d.PickCylinderSegment;
import javax.media.j3d.PickShape;
import javax.media.j3d.SceneGraphPath;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

/**
 * Big fat note to future phil,  this took ages to work out!!!
 * PickResult does the actual cylinder to triangle intersection work, check out intersectTSA method
 * which eventually gets to intersectCylinder on a single triangle and a cylinder
 * I think I see this class as checking firstly if the central line segment of teh cylinder is actually 
 * poking through the triangle and if so that's your intersect, otherwise (and here's the interesting bit)
 * it checks to see if any of the 3 lines round the outside of the triangle are with the radius of the cylinder
 * Which is a pure 2d system is fine, but in 3d this means the cylinder acts as a capsule (a rounded ended cylinder)
 * in any case where it is close to the lines, but as flat in all other cases.
 * 
 * The solution is probably to check intersections for being inside the half space defined by one end of the cylinder 
 * (point and normal being the vector to the other end)  and then the other end  
 * 
 * 
 * I find that pickGeomAllIntersect in PickResult also has a bug, where the return value is pr (an intermediate value)
 * not newpr as it should be
 * I have replace the call to pickAll with a call of my own version of pickGeomAllIntersect
 *  
 * I have confirmed that the SceneGrpahPath for the example problem is definately not found by the bounds test versus the cylinder segment
 * So I will use a big boundingsphere which surely must be accurate (please lord) and then a specific cylinder
 * 
 */

public class CorrectCylinderPicker
{
	/**
	 * Note the picktool should have had a BranchGroup or Locale set before calling this
	 * 
	 * Note that teh return pick result can have intersections that are with the capsule cylinder, not with the flat.
	 * But at least one fo them will be with the flat clyinder.
	 * 
	 * 
	 * @param pt
	 * @param start
	 * @param end
	 * @param radius
	 * @return
	 * @throws Exception 
	 */
	//TODO:I'd like to fully replace pickresult and pick intersection to remove these bum results but don't have time now
	public static PickResult[] pickAll(PickTool pt, Point3d start, Point3d end, float radius)
	{

		pt.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
		pt.setShapeCylinderSegment(new Point3d(start), new Point3d(end), radius);

		Vector3d direction = new Vector3d(end);
		direction.sub(start);
		direction.scale(0.5);
		Point3d center = new Point3d(start);
		center.add(direction);

		//direction.length gets it to the end of the ray, + radius should include all of cylinder 
		PickBounds pickBounds = new PickBounds(new BoundingSphere(center, direction.length() + radius));

		PickResult[] pickResults = pickGeomAllIntersect(pt.getBranchGroup(), new PickCylinderSegment(start, end, radius), pickBounds);

		if (pickResults != null)
		{
			ArrayList<PickResult> validFlatEndPickResults = new ArrayList<PickResult>();

			for (PickResult pr : pickResults)
			{
				for (int i = 0; i < pr.numIntersections(); i++)
				{
					// check for before start
					// dot product is probably the guy
					Point3d ip = pr.getIntersection(i).getPointCoordinatesVW();
					if (isWithinDoubleHalfSpace(start, end, ip))
					{

						// the ip is "beside" the cylinder segment, from the end face
						validFlatEndPickResults.add(pr);

						break;
					}

				}
			}
			if (validFlatEndPickResults.size() > 0)
			{
				PickResult[] ret = new PickResult[validFlatEndPickResults.size()];
				for (int i = 0; i < validFlatEndPickResults.size(); i++)
				{
					ret[i] = validFlatEndPickResults.get(i);
				}

				return ret;
			}
		}

		return null;

	}

	/**
	 * Any one wanting to inspect all pickintersetions needs to filter out with this, the bum ones.
	 * @param start
	 * @param end
	 * @param ip
	 * @return
	 */
	public static boolean isWithinDoubleHalfSpace(Point3d start, Point3d end, Point3d ip)
	{
		Vector3d temp1 = new Vector3d();
		Vector3d temp2 = new Vector3d();
		Vector3d temp3 = new Vector3d();
		Vector3d temp4 = new Vector3d();

		// check for before start
		// dot product is probably the guy

		temp1.set(ip);
		temp1.sub(start);

		temp2.set(end);
		temp2.sub(start);

		double dot1 = temp1.dot(temp2);

		if (dot1 >= 0)
		{
			// the ip is "beside" the cylinder segment, from the start face

			// check for beyond end
			temp3.set(ip);
			temp3.sub(end);

			temp4.set(start);
			temp4.sub(end);
			double dot2 = temp3.dot(temp4);
			if (dot2 >= 0)
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * This methods uses a seperate (large more accurate sphere) shape for the first pass bounds, 
	 * it MUST encompass the specific pickshape geometry.
	 * 
	 * It also fixes a bug where in returned result was not trimmed by geometry tests
	 */
	private static PickResult[] pickGeomAllIntersect(BranchGroup bg, PickShape pickShape, PickShape boundsTestShape)
	{
		int cnt = 0;

		// First pass, get by bounds only
		SceneGraphPath[] sgps = bg.pickAll(boundsTestShape);

		if (sgps == null)
			return null; // no match

		// Second pass, check to see if geometries intersected
		boolean found[] = new boolean[sgps.length];

		PickResult[] pr = new PickResult[sgps.length];
		for (int i = 0; i < sgps.length; i++)
		{
			pr[i] = new PickResult(sgps[i], pickShape);
			if (pr[i].numIntersections() > 0)
			{
				found[i] = true;
				cnt++;
			}
		}

		if (cnt == 0)
			return null; // no match

		PickResult[] newpr = new PickResult[cnt];
		cnt = 0; // reset for reuse.
		for (int i = 0; i < sgps.length; i++)
		{
			if (found[i] == true)
				newpr[cnt++] = pr[i];
		}

		return newpr;
	}

}
