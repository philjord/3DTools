package tools3d.hud.hudelements;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOnElapsedTime;

import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUDElement;
import tools3d.hud.HUDElementContainer;
import tools3d.utils.Utils3D;

public class HUDFPSCounter extends HUDElementContainer
{
	// how many frames to wait for rendering of
	public static int FRAME_SAMPLE = 5;

	public static int TIME_SAMPLE = 500;

	private long currtime = 0;

	private long lasttime = 0;

	private long deltatime;

	private BranchGroup behaviorBranchGroup = new BranchGroup();

	private FramesBehavior framesBehavior = new FramesBehavior();

	private TimeBehavior timeBehavior = new TimeBehavior();

	private HUDElement bgElement;

	private HUDElement textElement;

	private Color fpsTextColor = new Color(0.2f, 0.3f, 0.4f, 1f);

	private Font fpsTextFont = new Font("Arial", Font.PLAIN, 14);

	public HUDFPSCounter(Canvas3D2D canvas)
	{
		this();
		addToCanvas(canvas);
	}

	public HUDFPSCounter()
	{
		framesBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(framesBehavior);

		timeBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(timeBehavior);

		bgElement = new HUDElement(35, 25);
		bgElement.setLocation(5, 5);
		bgElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.4f));
		bgElement.getGraphics().fillRoundRect(0, 0, 35, 25, 15, 15);
		add(bgElement);
		textElement = new HUDElement(30, 20);
		textElement.setLocation(8, 11);
		add(textElement);
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		canvas.addElement(bgElement);
		canvas.addElement(textElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		canvas.removeElement(bgElement);
		canvas.removeElement(textElement);
	}

	private int numOfFrames = 0;

	private long timeOfFrames = 0;

	private class FramesBehavior extends Behavior
	{
		private WakeupOnElapsedFrames wakeUp = new WakeupOnElapsedFrames(0);

		public void initialize()
		{
			wakeupOn(wakeUp);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void processStimulus(Enumeration critera)
		{
			currtime = System.currentTimeMillis();
			deltatime = currtime - lasttime;
			lasttime = System.currentTimeMillis();

			numOfFrames++;
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

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void processStimulus(Enumeration critera)
		{
			// time is in millisec, so multiply by 1000 to get frames/sec
			double fps = numOfFrames / (timeOfFrames / 1000.0);

			fps = fps * 10;
			fps = Math.rint(fps);

			textElement.clear();
			textElement.getGraphics().setColor(fpsTextColor);
			textElement.getGraphics().setFont(fpsTextFont);
			textElement.getGraphics().drawString("" + ((int) fps / 10) + "." + ((int) fps % 10), 0, 10);

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
