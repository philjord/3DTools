package tools3d.utils.scenegraph;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.WakeupOnElapsedFrames;

import tools3d.utils.Utils3D;

/** 
 
 TODO: perhaps a longer delay between wakeups? time elapse 100ms?
 
 
*/
public class QueuingCallbackBehavior extends Behavior
{
	private ArrayList<Object> queue = new ArrayList<Object>();

	private CallBack callBack;

	private boolean newestOnly = false;

	private long maxElapsedTimeForCalls = -1;// in ms -1 for disable

	private WakeupOnElapsedFrames wakeup = new WakeupOnElapsedFrames(1, true);

	public QueuingCallbackBehavior(CallBack callBack, boolean newestOnly)
	{
		this.callBack = callBack;
		this.newestOnly = newestOnly;
		this.setSchedulingBounds(Utils3D.defaultBounds);
		this.setEnable(true);
	}

	public QueuingCallbackBehavior()
	{
		this.newestOnly = false;
		this.setSchedulingBounds(Utils3D.defaultBounds);
		this.setEnable(true);
	}

	public CallBack getCallBack()
	{
		return callBack;
	}

	public void setCallBack(CallBack callBack)
	{
		this.callBack = callBack;
	}

	public boolean isNewestOnly()
	{
		return newestOnly;
	}

	public void setNewestOnly(boolean newestOnly)
	{
		this.newestOnly = newestOnly;
	}

	public long getMaxElapsedTimeForCalls()
	{
		return maxElapsedTimeForCalls;
	}

	public void setMaxElapsedTimeForCalls(long maxElapsedTimeForCalls)
	{
		this.maxElapsedTimeForCalls = maxElapsedTimeForCalls;
	}

	@Override
	public void initialize()
	{
		wakeupOn(wakeup);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processStimulus(Enumeration criteria)
	{
		long start = System.nanoTime();
		synchronized (queue)
		{
			while (queue.size() > 0)
			{
				Object parameter = null;
				if (newestOnly)
				{
					parameter = queue.get(queue.size() - 1);
					queue.clear();
				}
				else
				{
					parameter = queue.remove(0);
				}
				try
				{
					callBack.run(parameter);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				if (maxElapsedTimeForCalls > 0 && ((System.nanoTime() - start) / 1000000) > maxElapsedTimeForCalls)
					break;
			}
		}

		wakeupOn(wakeup);
	}

	public void addToQueue(Object parameter)
	{
		synchronized (queue)
		{
			queue.add(parameter);
		}
	}

	public static interface CallBack
	{
		public void run(Object parameter);
	}

	public static class StructureUpdate
	{
		public enum TYPE
		{
			ADD, REMOVE
		};

		public Group parent = null;

		public Node child = null;
	}
}
