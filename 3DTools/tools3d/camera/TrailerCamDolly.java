package tools3d.camera;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;
import tools3d.utils.Utils3D;

import com.sun.j3d.utils.universe.PlatformGeometry;

public class TrailerCamDolly extends Dolly
{
	private static final Vector3d Y_UP = new Vector3d(0, 1, 0);

	private static final float TRAIL_MAX_DIST = 15;

	// amount to come in off any hits to stop "sideways" frustrum view holes
	private static final float TRAIL_DIST_MARGIN = 0.25f;

	private static final float TRAIL_MAX_FRACTION_DELTA_PER_SEC = 0.75f;

	private TrailorCamTimer timer = new TrailorCamTimer();

	private AvatarCollisionInfo avatarCollisionInfo;

	private TrailorCamCollider trailorCamCollider;

	private long lastUpdateTime = 0;

	private float lastSetHitFraction = 0;

	private float currentHitFraction = 0;

	private Point3d currentAvartarHeadPoint = new Point3d();

	private Vector3d currentCameraMaxVector = new Vector3d();

	public TrailerCamDolly(AvatarCollisionInfo avatarCollisionInfo, TrailorCamCollider trailorCamCollider)
	{
		super(1);
		this.trailorCamCollider = trailorCamCollider;
		setAvatarCollisionInfo(avatarCollisionInfo);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		pg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPlatformGeometry(pg);

		// add the picking timer to the universe
		timer.setSchedulingBounds(Utils3D.defaultBounds);
		timer.setEnable(true);
		addChild(timer);
	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	// deburner
	private Transform3D tempRotTransform = new Transform3D();

	private Point3d tempCameraPoint = new Point3d();

	private Vector3d tempCameraVector = new Vector3d();

	private Quat4f prevRot = new Quat4f();

	private Vector3f prevTrans = new Vector3f();

	//Note we set teh locations values up, but process does the work, in case teh world changes without us moving (a door opens)
	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		// only update if new things have happened
		if (!prevRot.epsilonEquals(rot, 0.0001f) || !prevTrans.epsilonEquals(trans, 0.005f))
		{
			// set head point
			currentAvartarHeadPoint.set(trans);
			currentAvartarHeadPoint.y += avatarCollisionInfo.getCameraAbovePelvisHeight();

			tempRotTransform.set(rot);
			currentCameraMaxVector.set(0, 0, 1);// use a normal
			tempRotTransform.transform(currentCameraMaxVector);

			// set max camera vector
			currentCameraMaxVector.scale(TRAIL_MAX_DIST);
			//recall for next iter
			prevRot.set(rot);
			prevTrans.set(trans);
		}

	}

	//deburner
	private Transform3D tempCamTrans = new Transform3D();

	public synchronized void processCamera()
	{
		long dt = (System.nanoTime() - lastUpdateTime) / 1000000;
		float maxFractionForDT = TRAIL_MAX_FRACTION_DELTA_PER_SEC * (dt / 1000f);

		// set hit fraction
		float newHitFraction = trailorCamCollider.getCollisionFraction(currentAvartarHeadPoint, currentCameraMaxVector);

		// if it's bad just use previous hit fraction
		if (!Float.isNaN(newHitFraction))
		{
			currentHitFraction = newHitFraction;
		}

		// come in a tiny bit so we see through wall to the side less
		if (currentHitFraction <= 1.0f)
		{
			float cameraVectorLen = currentHitFraction * TRAIL_MAX_DIST;
			cameraVectorLen -= TRAIL_DIST_MARGIN;
			currentHitFraction = cameraVectorLen / TRAIL_MAX_DIST;
		}

		float hitFractionDelta = currentHitFraction - lastSetHitFraction;

		// clamp the max out ward delta by time (inwards is instant)
		hitFractionDelta = hitFractionDelta > maxFractionForDT ? maxFractionForDT : hitFractionDelta;
		currentHitFraction = hitFractionDelta + lastSetHitFraction;

		// scale the camera vector
		tempCameraVector.set(currentCameraMaxVector);
		tempCameraVector.scale(currentHitFraction);

		// make a camerapoint
		tempCameraPoint.add(currentAvartarHeadPoint, tempCameraVector);

		// look at head from camera
		tempCamTrans.lookAt(tempCameraPoint, currentAvartarHeadPoint, Y_UP);
		tempCamTrans.invert();//cos lookAt is not for view platforms?

		// set the actual view platform 
		getViewPlatformTransform().setTransform(tempCamTrans);

		// remember state
		lastSetHitFraction = currentHitFraction;
		lastUpdateTime = System.nanoTime();
	}

	public static interface TrailorCamCollider
	{
		// returns the scale of the camera vector for cloest hit
		public float getCollisionFraction(Point3d lookAt, Vector3d cameraVector);
	}

	private class TrailorCamTimer extends Behavior
	{
		private WakeupOnElapsedFrames FPSWakeUp = new WakeupOnElapsedFrames(0);

		@Override
		public void initialize()
		{
			wakeupOn(FPSWakeUp);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void processStimulus(Enumeration critera)
		{
			processCamera();

			// Set the trigger for the behavior
			wakeupOn(FPSWakeUp);
		}
	}

}
