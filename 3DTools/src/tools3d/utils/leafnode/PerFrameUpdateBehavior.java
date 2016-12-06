package tools3d.utils.leafnode;

import java.util.Iterator;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.WakeupCondition;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Point3d;

public class PerFrameUpdateBehavior extends Behavior
{
	private CallBack callBack;

	private WakeupCondition FPSCriterion = new WakeupOnElapsedFrames(0, false);

	public PerFrameUpdateBehavior(CallBack callBack)
	{
		setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
		setEnable(true);
		this.callBack = callBack;
	}

	@Override
	public void initialize()
	{
		wakeupOn(FPSCriterion);
	}

	@Override
	public void processStimulus(Iterator<WakeupCriterion> criteria)
	{
		callBack.update();

		wakeupOn(FPSCriterion);
	}

	public static interface CallBack
	{
		public void update();
	}
}
