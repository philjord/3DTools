package old.navigation.rules.deprecated;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools.WeakListenerList;
import tools3d.navigation.AvatarCollisionInfo;
import tools3d.navigation.GravityEvent;
import tools3d.navigation.GravityListener;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import old.geography.HeightMap;

public class NavigationPositionRuleGravity implements NavigationPositionRule
{
	private AvatarCollisionInfo avatarCollisionInfo;

	private static float gravityPickerRadiusRatio = 0.75f;

	private static final Vector3f Y_DOWN = new Vector3f(0, -1, 0);

	//private static final Vector3d Y_UP = new Vector3d(0, 1, 0);

	// private static final double DEFAULT_AVATAR_HEIGHT = 1.8f;

	// if the ground dist is greater than this then no gravity is applied, the correction is simply made
	private static final float NON_FREEFALL_DROP_PER_SECOND = -0.10f;

	// actual max rise will be this of 45 degree (rise equals dist forwards) which is greater
	//TODO: if 45 no good make a multiplier 2 = 60 0.5 = 30 etc
	private static final float MAX_RISE_PER_SECOND = 2.50f;

	// NEVER let grav be positive
	private static final float GRAVITY = -9.8f;

	// TODO: add terminal velocity to fall method
	// private static final double TERMINAL_VELOCITY = -100f;
	// private static final long timeToTerminalVelocity = (long) ((TERMINAL_VELOCITY / GRAVITY) / 1000d);

	/** The height of the avatar above the terrain */
	// private double avatarHeight = DEFAULT_AVATAR_HEIGHT;
	/** The shape3D to do the terrain height on */
	private HeightMap navigationGeometry;

	private WeakListenerList<GravityListener> gravityListeners = new WeakListenerList<GravityListener>();

	public boolean freeFalling = false;

	private float freeFallInitialVelocity = 0;

	//Nano seconds
	public long startFreeFallTime = 0;

	//Nano seconds
	public long endLastFrameTime = 0;

	/** A collision tool initialised with the colliables branchgroup when recieved */
	private PickTool collisionPickTool;

	private GravityEvent gravityEvent = new GravityEvent();

	private boolean active = false;

	public NavigationPositionRuleGravity(AvatarCollisionInfo avatarCollisionInfo)
	{
		setAvatarCollisionInfo(avatarCollisionInfo);
	}

	public NavigationPositionRuleGravity(HeightMap navigationGeometry, AvatarCollisionInfo avatarCollisionInfo)
	{
		setGroundShape3D(navigationGeometry);
		setAvatarCollisionInfo(avatarCollisionInfo);
	}

	public void addGravityListener(GravityListener gravityListener)
	{
		gravityListeners.add(gravityListener);
	}

	public void removeGravityListener(GravityListener gravityListener)
	{
		gravityListeners.remove(gravityListener);
	}

	public void setGroundShape3D(HeightMap navigationGeometry)
	{
		this.navigationGeometry = navigationGeometry;
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

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * +ve is up -ve is down
	 * 
	 * @param velocityChange
	 *            the velocity change
	 */
	public void applyVelocity(double velocityChange)
	{
		// TODO: this is no working nicely for small velocity amount 3.5 and when not moving
		// only jump if we aren't falling
		if (!freeFalling)
		{
			// just make teh event look like a free fall, with a big dist
			startFreeFall((float) velocityChange, 100);
		}
		else
		{
			System.out.println("can't apply free falling now");
		}
	}

	/**
	 * A valid vector based on the passed in one after rule applied.
	 */
	// deburner
	private Point3f desiredLocation = new Point3f();

	private Point3f desiredLocationOverStep = new Point3f();

	private Vector3f returnVec = new Vector3f();

	private final Vector3f noMove = new Vector3f();

	private PickResult pickResult;

	@Override
	public synchronized Vector3f applyRule(Vector3f desiredTranslation, Vector3f currentLocation)
	{

		long timeNow = System.nanoTime();
		returnVec.set(desiredTranslation);

		if (navigationGeometry != null || collisionPickTool != null)
		{
			// Where are we going to be soon?
			desiredLocation.add(currentLocation, desiredTranslation);

			try
			{
				float groundY = Float.NEGATIVE_INFINITY;

				if (collisionPickTool != null)
				{

					// now that the avatar position is defined as being on the ground, not at head height
					// this needs to take account of that properly

					// lets see if we are on a physical item
					desiredLocationOverStep.set(desiredLocation);
					// our foot step is 0.5 of a meter we will only check from there down
					desiredLocationOverStep.y += avatarCollisionInfo.getAvatarYStepOverHeight();
					//collisionPickTool.setShapeRay(new Point3d(desiredLocationOverStep), new Vector3d(Y_DOWN));
					collisionPickTool.setShapeCylinderRay(new Point3d(desiredLocationOverStep), new Vector3d(Y_DOWN),
							avatarCollisionInfo.getAvatarXZRadius() * gravityPickerRadiusRatio);

					pickResult = collisionPickTool.pickClosest();
					if (pickResult != null)
					{

						PickIntersection pickIntersection = pickResult.getClosestIntersection(new Point3d(desiredLocationOverStep));
						groundY = desiredLocationOverStep.y - (float) pickIntersection.getDistance();
					}
				}

				// ask the ground grid for true ground level at this point
				if (groundY == Float.MIN_VALUE && navigationGeometry != null)
				{
					groundY = navigationGeometry.getGroundY(desiredLocation.x, desiredLocation.z);					 		
				}

				if (groundY != Float.NEGATIVE_INFINITY)// check for no ground below
				{
					float heightAboveTerrain = desiredLocation.y - groundY;

					float terrainStep = -heightAboveTerrain; // (avatarHeight - heightAboveTerrain);

					if (freeFalling)
					{
						fallDownWard(returnVec, terrainStep);
					}
					else
					{
						// Do we need to adjust the height?
						if (heightAboveTerrain > 0.001 || heightAboveTerrain < -0.001) // avatarHeight)
						{
							long timeSinceLastUpdate = timeNow - endLastFrameTime;
							double timeSinceLastUpdateInSeconds = 1000000000d / timeSinceLastUpdate;
							// note time is in nano seconds
							double requiredHeightChangePerSecond = timeSinceLastUpdateInSeconds * terrainStep;

							double desiredMovePerScond = desiredTranslation.length() * timeSinceLastUpdateInSeconds;

							// is it a step up?
							if (terrainStep > 0)
							{
								// this allows for a 45 degree rise, a mulitiple is needed to change the slope
								double actualMaxRise = MAX_RISE_PER_SECOND < desiredMovePerScond ? desiredMovePerScond
										: MAX_RISE_PER_SECOND;

								// limit to the max rise amount per second
								if (requiredHeightChangePerSecond > actualMaxRise)
								{
									// set terrainStep to be the ratio of the current change versus the max
									terrainStep = (float) (terrainStep * (actualMaxRise / requiredHeightChangePerSecond));
								}
								returnVec.y = terrainStep;

							}
							else
							{
								// very small adjustments will simply be made
								// is the change less than zero but greater than the "free" drop system
								if (requiredHeightChangePerSecond > NON_FREEFALL_DROP_PER_SECOND)
								{
									returnVec.y = terrainStep;
								}
								else
								{
									fallDownWard(returnVec, terrainStep);
								}
							}
						}
						else
						{
							// translation is ok, no change needed
							returnVec.set(desiredTranslation);
						}
					}
				}
				else
				{
					// when no terrain below us do not free fall
					//new Exception("GroundHeightCalculator.getDistanceFromGround returned Float.POSITIVE_INFINITY").printStackTrace();
					returnVec.set(noMove);
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				// no move allowed
				returnVec.set(noMove);
			}
		}

		endLastFrameTime = timeNow;
		return returnVec;
	}

	private synchronized void startFreeFall(float intitialVelocity, float terrainDist)
	{
		if (!freeFalling)
		{
			freeFalling = true;
			freeFallInitialVelocity = intitialVelocity;
			startFreeFallTime = endLastFrameTime;

			gravityEvent.set(GravityEvent.TAKE_OFF_EVENT, intitialVelocity, terrainDist);
			for (int i = 0; i < gravityListeners.size(); i++)
			{
				gravityListeners.get(i).gravityEventOccured(gravityEvent);
			}
		}
	}

	private synchronized void stopFreeFall(double velocityAtImpact)
	{
		if (freeFalling)
		{
			freeFalling = false;
			freeFallInitialVelocity = 0;
			startFreeFallTime = endLastFrameTime;

			// we have landed lets do some interesting things
			gravityEvent.set(GravityEvent.LANDED_EVENT, velocityAtImpact, 0);
			for (int i = 0; i < gravityListeners.size(); i++)
			{
				gravityListeners.get(i).gravityEventOccured(gravityEvent);
			}
		}
	}

	/**
	 * Some equations s=dist, v=velocity, t=time, a=acceleration G is fixed, t is invarient, s and v are varient v = g*t
	 * s = v*t s = s0 + v0*t +1/2g*t*t t = root[2s/a] v^2 = v0^2 + 2a(s - s0) v = v0 + at I have yet to add terminal
	 * velocity, which will break all calcs in to two parts the first bit up until velocity reached, then the time after
	 * that
	 * 
	 * http://hypertextbook.com/physics/mechanics/motion-equations/
	 * 
	 * @param terrainStep
	 */
	private void fallDownWard(Vector3f fallReturnVec, float terrainDist)
	{
		if (!freeFalling)
		{
			// we've just stepped off something
			startFreeFall(0, terrainDist);
		}

		long timeNow = System.nanoTime();
		long timeTotalToStartOfFrame = endLastFrameTime - startFreeFallTime;
		long timeTotalToEndOfFrame = timeNow - startFreeFallTime;
		double timeToStartSecs = timeTotalToStartOfFrame / 1000000000d;
		double timeToEndSecs = timeTotalToEndOfFrame / 1000000000d;

		// s = s0 + v0*t +1/2g*t*t
		double distToStartOfFrame = (freeFallInitialVelocity * timeToStartSecs) + (float) (0.5f * GRAVITY * Math.pow(timeToStartSecs, 2));
		double distToEndOfFrame = (freeFallInitialVelocity * timeToEndSecs) + (float) (0.5f * GRAVITY * Math.pow(timeToEndSecs, 2));
		double distStartToEndOfFrame = distToEndOfFrame - distToStartOfFrame;

		// now find which is higher out of velocity and terrainDist
		if (terrainDist > distStartToEndOfFrame)
		{
			// using v^2 = v0^2 + 2a(s - s0)
			double startOfFrameVelocity = freeFallInitialVelocity + (GRAVITY * timeToStartSecs);
			double velocityAtImpact = (float) Math.sqrt(Math.pow(startOfFrameVelocity, 2) + (2 * GRAVITY * terrainDist));

			stopFreeFall(velocityAtImpact);
			fallReturnVec.y = terrainDist;
		}
		else
		{
			// we haven't landed yet lets just update the y position
			fallReturnVec.y = (float) distStartToEndOfFrame;
		}
	}
}
