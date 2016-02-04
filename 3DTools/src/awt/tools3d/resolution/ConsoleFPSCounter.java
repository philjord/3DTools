package awt.tools3d.resolution;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOnElapsedTime;

import tools3d.utils.Utils3D;

public class ConsoleFPSCounter
{
	// how many frames to wait for rendering of
	public static int FRAME_SAMPLE = 10;

	public static int TIME_SAMPLE = 500;

	private long currtime = 0;

	private long lasttime = 0;

	private long deltatime;

	private BranchGroup behaviorBranchGroup = new BranchGroup();

	private FramesBehavior framesBehavior = new FramesBehavior();

	private TimeBehavior timeBehavior = new TimeBehavior();

	public ConsoleFPSCounter()
	{
		framesBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(framesBehavior);

		timeBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(timeBehavior);

	}

	private int numOfFrames = 0;

	private long timeOfFrames = 0;

	private class FramesBehavior extends Behavior
	{
		private WakeupOnElapsedFrames wakeUp = new WakeupOnElapsedFrames(FRAME_SAMPLE);

		public void initialize()
		{
			wakeupOn(wakeUp);
		}

		@SuppressWarnings("rawtypes")
		public void processStimulus(Enumeration critera)
		{
			currtime = System.currentTimeMillis();
			deltatime = currtime - lasttime;
			lasttime = System.currentTimeMillis();

			numOfFrames += FRAME_SAMPLE;
			timeOfFrames += deltatime;

			// Set the trigger for the behavior
			wakeupOn(wakeUp);
		}
	}

	private class TimeBehavior extends Behavior
	{
		private WakeupOnElapsedTime wakeUp = new WakeupOnElapsedTime(TIME_SAMPLE);

		public void initialize()
		{
			wakeupOn(wakeUp);
		}

		@SuppressWarnings("rawtypes")
		public void processStimulus(Enumeration critera)
		{
			// time is in millisec, so multiply by 1000 to get frames/sec
			double fps = numOfFrames / (timeOfFrames / 1000.0);

			fps = fps * 10;
			fps = Math.rint(fps);

			System.out.println("" + ((int) fps / 10) + "." + ((int) fps % 10));

			numOfFrames = 0;
			timeOfFrames = 0;

			// Set the trigger for the behavior
			wakeupOn(wakeUp);

		}
	}

	public BranchGroup getBehaviorBranchGroup()
	{
		return behaviorBranchGroup;
	}

}
