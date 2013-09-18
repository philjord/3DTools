package tools3d.navigation.rules.deprecated.picking;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

public class IntersectionUtils
{

	/**
	 * This get a normal from the given triangle, towards the given direction, it ignores the tri winding rule.
	 * @param point3ds
	 * @param pointAt
	 * @return
	 */
	public static Vector3d getNormal(Point3d[] coordinates, Point3f pointAt)
	{

		Vector3d vec0 = new Vector3d();
		Vector3d vec1 = new Vector3d();
		Vector3d pNrm = new Vector3d();

		double pNrmDotrDir = 0.0;

		int k = 0;
		int l = 0;

		// Compute plane normal.
		for (int i = 0; i < coordinates.length; i++)
		{
			if (i != coordinates.length - 1)
			{
				l = i + 1;
			}
			else
			{
				l = 0;
			}
			vec0.x = coordinates[l].x - coordinates[i].x;
			vec0.y = coordinates[l].y - coordinates[i].y;
			vec0.z = coordinates[l].z - coordinates[i].z;
			if (vec0.length() > 0.0)
			{
				break;
			}
		}

		for (int j = l; j < coordinates.length; j++)
		{
			if (j != coordinates.length - 1)
			{
				k = j + 1;
			}
			else
			{
				k = 0;
			}
			vec1.x = coordinates[k].x - coordinates[j].x;
			vec1.y = coordinates[k].y - coordinates[j].y;
			vec1.z = coordinates[k].z - coordinates[j].z;
			if (vec1.length() > 0.0)
			{
				break;
			}
		}

		pNrm.cross(vec0, vec1);

		// no normal can be found
		if ((vec1.length() == 0) || (pNrm.length() == 0))
		{
			return null;
		}

		// It is possible that Quad is degenerate to Triangle 
		// at this point
		Vector3d dirVec = new Vector3d(pointAt);
		dirVec.sub(coordinates[0]);
		pNrmDotrDir = pNrm.dot(new Vector3d(dirVec));

		// Ray is parallel to plane. 
		if (pNrmDotrDir < 0.0)
		{
			pNrm.negate();
		}

		pNrm.normalize();

		return pNrm;
	}

	public static Point3d getTriangleCentroid(Point3d[] coordinates)
	{
		Point3d centroid = new Point3d((coordinates[0].x + coordinates[1].x + coordinates[2].x) / 3,
				(coordinates[0].y + coordinates[1].y + coordinates[2].y) / 3, (coordinates[0].z + coordinates[1].z + coordinates[2].z) / 3);
		return centroid;
	}
}
