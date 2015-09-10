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
 Currently only one callback per frame (sounds ok)
 
*/
public class QueuingCallbackBehavior extends Behavior
{
	private ArrayList<Object> queue = new ArrayList<Object>();

	private CallBack callBack;

	private boolean newestOnly = false;

	private WakeupOnElapsedFrames wakeupFrame1 = new WakeupOnElapsedFrames(1, true);

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

	@Override
	public void initialize()
	{
		wakeupOn(wakeupFrame1);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processStimulus(Enumeration criteria)
	{
		synchronized (queue)
		{
			if (queue.size() > 0)
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
			}
		}

		wakeupOn(wakeupFrame1);
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
