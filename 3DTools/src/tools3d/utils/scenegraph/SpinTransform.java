package tools3d.utils.scenegraph;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;

public class SpinTransform extends Behavior
{
	private TransformGroup trans;

	//Calculations for frame duration timing, 
	//used between successive calls to process 
	private long previousFrameEndTime;

	private double currentRot = 0;
	private double speed = 0;

	private WakeupCondition FPSCriterion = new WakeupOnElapsedFrames(0, false);

	/**
	 * speed is in radians per  second, so 1 will turn about 1/6 of a turn per second
	 * 2 will be about 2 radians a seconds, or 1/3 of a turn 
	 * @param trans
	 * @param speed
	 */
	public SpinTransform(TransformGroup trans, double speed)
	{
		this.trans = trans;
		this.speed = speed;
		setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
		setEnable(true);
	}

	public void initialize()
	{
		wakeupOn(FPSCriterion);
	}

	@SuppressWarnings({ "rawtypes" })
	public void processStimulus(Enumeration criteria)
	{
		process();
		wakeupOn(FPSCriterion);
	}

	private void process()
	{

		long timeNow = System.currentTimeMillis();
		long frameDuration = timeNow - previousFrameEndTime;
		currentRot += (frameDuration / 1000d) * speed;
		Transform3D t = new Transform3D();
		t.setRotation(new AxisAngle4d(0, 1, 0, currentRot));
		trans.setTransform(t);
		// record when we last thought about movement
		previousFrameEndTime = timeNow;

	}

}
