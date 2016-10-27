package tools3d.utils;

import java.util.Enumeration;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.WakeupCondition;
import org.jogamp.java3d.WakeupOnElapsedTime;

/**
 * @author Administrator
 *
 */
public class TimedRunnableBehavior extends Behavior
{
	private Runnable callback;

	private int loopCount = 1;

	private WakeupCondition criterion;

	public TimedRunnableBehavior(long delay)
	{
		criterion = new WakeupOnElapsedTime(delay);
		setSchedulingBounds(Utils3D.defaultBounds);
		setEnable(false);
	}

	public void start(int loopCount1, Runnable callback1)
	{
		this.callback = callback1;
		this.loopCount = loopCount1;
		setEnable(true);
	}

	public void initialize()
	{
		wakeupOn(criterion);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void processStimulus(Enumeration criteria)
	{
		callback.run();
		wakeupOn(criterion);

		if (loopCount != -1)
		{
			loopCount -= 1;
			if (loopCount <= 0)
			{
				setEnable(false);
			}
		}
	}

}
