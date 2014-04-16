package tools3d.navigation.rules.deprecated;

import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;
import tools3d.navigation.rules.deprecated.picking.CorrectCylinderPicker;
import tools3d.navigation.rules.deprecated.picking.IntersectionUtils;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
/**
 * @Deprecated use jnifbullet charactercontroller
 * @author philip
 */
@Deprecated 
public class NavigationPositionRuleCollision implements NavigationPositionRule
{
	private AvatarCollisionInfo avatarCollisionInfo;

	/** A collision tool initialised with the colliables branchgroup when recieved */
	private PickTool collisionPickTool;

	private boolean active = false;

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public NavigationPositionRuleCollision(AvatarCollisionInfo avatarCollisionInfo)
	{
		setAvatarCollisionInfo(avatarCollisionInfo);
	}

	public NavigationPositionRuleCollision(AvatarCollisionInfo avatarCollisionInfo, BranchGroup physicalBranch)
	{
		setPhysicalBranch(physicalBranch);
		setAvatarCollisionInfo(avatarCollisionInfo);
	}

	public void setPhysicalBranch(BranchGroup physicalBranch)
	{
		collisionPickTool = new PickTool(physicalBranch);
		collisionPickTool.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	// deburner
	private Point3f testLocation = new Point3f();

	private Vector3f trans = new Vector3f();

	private Point3f bottom = new Point3f();

	private Point3f top = new Point3f();

	private Point3f hips = new Point3f();

	private final Vector3f noMove = new Vector3f();

	private PickResult[] pickResults;

/*	Big fat note to future phil, and this took ages to work out!!!
	 * PickResult does the actual cylinder to triangle intersection work, check out intersectTSA method
	 * which eventually gets to intersectCylinder on a single triangle and a cylinder
	 * I think I see this class as checking firstly if the central line segment of teh cylinder is actually 
	 * poking through the triangle and if so that's your intersect, otherwise (and here's the interesting bit)
	 * it checks to see if any of the 3 lines round the outside of the triangle are with the radius of the cylinder
	 * Which is a pure 2d system is fine, but in 3d this means the cylinder acts as a capsule (a rounded ended cylinder)
	 * in any case where it is close to the lines, but as flat in all other cases.
	 * 
	 * The solution is probably to chekc intersections for being inside the half space defined by one of the cylinder 
	 * (point and normal being the vector to the other end)  and then the  other end  
	 * 
	 * 
	 * On a different note, walkings sideways has different collsions? have I got a forward concept jammed in somewhere?
	 * 
	 * Also note if you want bounds only picking use the pickfast package next to picking
	 * 
	 */

	private Vector3f origDesiredTrans = new Vector3f();

	@Override
	public Vector3f applyRule(Vector3f desiredTranslation, Vector3f currentLocation)
	{
		if (!desiredTranslation.equals(noMove))
		{
			origDesiredTrans.set(desiredTranslation);
			float length = desiredTranslation.length();

			// 3 times as many segmetns as our radius
			int segments = (int) Math.ceil((length / avatarCollisionInfo.getAvatarXZRadius()) * 2);
			float segmentLength = length / segments;

			trans.set(desiredTranslation);

			desiredTranslation.set(noMove);

			boolean done = false;
			ArrayList<Vector3d> netNormalContributors = new ArrayList<Vector3d>();
			for (int i = 1; i <= segments && !done; i++)
			{
				trans.normalize();
				trans.scale(i * segmentLength);

				testLocation.add(currentLocation, trans);
				bottom.set(testLocation);
				bottom.y += avatarCollisionInfo.getAvatarYStepOverHeight();
				top.set(testLocation);
				top.y += avatarCollisionInfo.getAvatarYHeight();
				hips.set(testLocation);
				hips.y += avatarCollisionInfo.getAvatarYStepOverHeight() / 2;

				pickResults = CorrectCylinderPicker.pickAll(collisionPickTool, new Point3d(bottom), new Point3d(top), avatarCollisionInfo
						.getAvatarXZRadius());

				if (pickResults != null)
				{
					for (PickResult pickResult : pickResults)
					{
						if (pickResult.numIntersections() > 0)
						{
							for (int j = 0; j < pickResult.numIntersections(); j++)
							{
								PickIntersection pickIntersection = pickResult.getIntersection(j);
								//TODO: possibly only intersections in the half space of the desired direction are allowed to stop us
								// that way "exiting" shapes is allowed?

								Vector3d triNormal = IntersectionUtils.getNormal(pickIntersection.getPrimitiveCoordinatesVW(), new Point3f(hips));
								if (triNormal != null)
								{
									netNormalContributors.add(triNormal);
								}

								// must break and the trans will have been set by the previous
								// loop or no move if this is the first i
								done = true;
							}
						}
					}
				}
				else
				{
					desiredTranslation.set(trans);
				}
			}
			Vector3d netNormal = new Vector3d();
			origDesiredTrans.y = 0;
			origDesiredTrans.normalize();

			Vector3d bestN = null;
			double bestNDot = Float.NEGATIVE_INFINITY;

		//	System.out.println("");
			for (Vector3d netNormalContributor : netNormalContributors)
			{
				netNormalContributor.y = 0;
				netNormalContributor.normalize();
	//			System.out.println("netNormalContributor " + netNormalContributor);
	//			System.out.println("origDesiredTrans " + origDesiredTrans);
				double nDot = netNormalContributor.dot(new Vector3d(origDesiredTrans));
	//			System.out.println("nDot " + nDot);
				// make sure it is a normal "pushing against" not pointing with
				if (nDot < 0)
				{
					// is this the least opposite to me
					if (nDot > bestNDot)
					{
						bestN = netNormalContributor;
						bestNDot = nDot;
					}

					netNormal.x += netNormalContributor.x / netNormalContributors.size();
					//netNormal.y += netNormalContributor.y / netNormalContributors.size(); // we don't want y in the slidy
					netNormal.z += netNormalContributor.z / netNormalContributors.size();
				}
			}

			if (bestN != null)
			{
	//			System.out.println("bestN " + bestN);
			}

			// now lets add a tiny bit of this vector back into the avatar movement? might make a crappy jitter effect?
			netNormal.normalize();
			//TODO: I suspect this is where wall sliding should happen?

			// How to do slidy:
			// If I have bumped into triangles and all of them have normals that are pointing with a certain amount of the dot of
			// our desired direction then possibly try another test slightly in the direction of the difference vector?

			// let's start by testing out what we are hitting as a normal to our desired and see if there is a simple case of a small dot

		}

		return desiredTranslation;
	}
}
