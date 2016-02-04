package awt.tools3d.mixed3d2d.hud.hudelements;

import java.awt.Font;
import java.awt.Color;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOnElapsedTime;

import awt.tools3d.mixed3d2d.hud.HUDElement;
import awt.tools3d.mixed3d2d.hud.HUDElementContainer;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.utils.Utils3D;

public class HUDFPSCounter extends HUDElementContainer
{
	//see http://websemantics.co.uk/resources/font_size_conversion_chart/ points to pixels
	private static double pixelToPnt = 1.4;
	
	// how many frames to wait for rendering of
	public static int FRAME_SAMPLE = 5;

	public static int TIME_SAMPLE = 500;
	
	public static int HEIGHT = 50;

	private long currtime = 0;

	private long lasttime = 0;

	private long deltatime;

	private BranchGroup behaviorBranchGroup = new BranchGroup();

	private FramesBehavior framesBehavior = new FramesBehavior();

	private TimeBehavior timeBehavior = new TimeBehavior();

	private HUDElement textElement;

	private Color fpsTextColor = new Color(1f, 1f, 0f, 1f);

	private Font fpsTextFont = new Font("Arial", Font.BOLD, (int)(HEIGHT / pixelToPnt));

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

		textElement = new HUDElement(40, 30);
		textElement.setLocation(0, 0);
		add(textElement);
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		canvas.addElement(textElement);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
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

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
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

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		public void processStimulus(Enumeration critera)
		{
			// time is in millisec, so multiply by 1000 to get frames/sec
			double fps = numOfFrames / (timeOfFrames / 1000.0);

			textElement.clear();
			textElement.getGraphics().setColor(fpsTextColor);
			textElement.getGraphics().setFont(fpsTextFont);
			//note round to int
			textElement.getGraphics().drawString("" + ((int) Math.rint(fps * 10) / 10), 0, textElement.getHeight() - 5);

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
