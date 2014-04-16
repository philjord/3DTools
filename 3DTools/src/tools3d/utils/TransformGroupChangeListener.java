package tools3d.utils;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupAnd;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.media.j3d.WakeupOnTransformChange;

public class TransformGroupChangeListener extends Behavior
{
	private WakeupCondition wakeUp;

	private TransformGroup source;

	private TransformGroupChangedCallback listener;

	public TransformGroupChangeListener(TransformGroup source, TransformGroupChangedCallback listener)
	{
		this.source = source;
		this.listener = listener;
		if (!source.isLive())
		{
			source.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		}

		wakeUp = new WakeupOnTransformChange(source);
	}

	public TransformGroupChangeListener(TransformGroup source, TransformGroupChangedCallback listener, long minMillisec)
	{
		this.source = source;
		this.listener = listener;
		if (!source.isLive())
		{
			source.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		}

		WakeupOnTransformChange wakeUpTC = new WakeupOnTransformChange(source);
		WakeupOnElapsedTime wakeupET = new WakeupOnElapsedTime(minMillisec);
		wakeUp = new WakeupAnd(new WakeupCriterion[]
		{ wakeUpTC, wakeupET });

	}

	public void initialize()
	{
		wakeupOn(wakeUp);
	}

	@SuppressWarnings("rawtypes")
	public void processStimulus(Enumeration critiria)
	{
		listener.transformGroupChanged(source);

		//reset the wakeup
		wakeupOn(wakeUp);
	}

	public static interface TransformGroupChangedCallback
	{
		public void transformGroupChanged(TransformGroup tg);
	}
}
