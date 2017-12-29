package tools3d.mixed3d2d.curvehud.elements;

import java.util.Iterator;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.java3d.WakeupOnElapsedTime;

import com.jogamp.opengl.hudbasics.graph.demos.ui.Label;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.utils.Utils3D;

public class HUDFPSCounter
{
	//see http://websemantics.co.uk/resources/font_size_conversion_chart/ points to pixels
	//private static double pixelToPnt = 1.4;

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

	private Label fpsLabel;

	public HUDFPSCounter(Canvas3D2D canvas3d2d)
	{
		this();
		addToCanvas(canvas3d2d);
	}

	public HUDFPSCounter()
	{
		framesBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(framesBehavior);

		timeBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(timeBehavior);

	}

	public void addToCanvas(Canvas3D2D canvas3d2d)
	{
		fpsLabel = canvas3d2d.createLabel();
		fpsLabel.setEnabled(true);
		fpsLabel.translate(-0.98f, 0.9f, 0f);
		fpsLabel.setColor(1f, 1f, 0f, 1f);

	}

	private int numOfFrames = 0;

	private long timeOfFrames = 0;

	private class FramesBehavior extends Behavior
	{
		private WakeupOnElapsedFrames wakeUp = new WakeupOnElapsedFrames(0);

		@Override
		public void initialize()
		{
			wakeupOn(wakeUp);
		}

		@Override
		public void processStimulus(Iterator<WakeupCriterion> critera)
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

		@Override
		public void initialize()
		{
			wakeupOn(wakeUp);
		}

		@Override
		public void processStimulus(Iterator<WakeupCriterion> critera)
		{
			// time is in millisec, so multiply by 1000 to get frames/sec
			double fps = numOfFrames / (timeOfFrames / 1000.0);

			/*	textElement.clear();
				textElement.getGraphics().setColor(fpsTextColor);
				textElement.getGraphics().setFont(fpsTextFont);
				//note round to int
				textElement.getGraphics().drawString("" + ((int) Math.rint(fps * 10) / 10), 0, textElement.getHeight() - 5);
			*/

			String newText = "" + ((int) Math.rint(fps * 10) / 10);
			if (fpsLabel != null && !fpsLabel.getText().equals(newText))
				fpsLabel.setText(newText);

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
