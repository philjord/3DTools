package tools3d.utils;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnElapsedTime;

/**
 * @author Administrator
 *
 */
public class TimedRunnableBehavior extends Behavior
{
	private Runnable callback;

	private int loopCount = 1;

	private WakeupCondition criterion;

	public TimedRunnableBehavior(long delay, int loopCount, Runnable callback)
	{
		this.callback = callback;
		this.loopCount = loopCount;
		criterion = new WakeupOnElapsedTime(delay);
		setSchedulingBounds(Utils3D.defaultBounds);
		setEnable(true);
	}

	public void initialize()
	{
		wakeupOn(criterion);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processStimulus(Enumeration criteria)
	{
		callback.run();
		if (loopCount == -1)
		{
			wakeupOn(criterion);
		}
		else
		{
			loopCount -= 1;
			if (loopCount > 0)
			{
				wakeupOn(criterion);
			}
		}
	}

}
