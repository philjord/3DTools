package awt.tools3d.mixed3d2d.hud.hudelements;

import java.awt.Font;
import java.awt.Color;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupOnElapsedTime;

import awt.tools3d.mixed3d2d.hud.HUDElement;
import awt.tools3d.mixed3d2d.hud.HUDElementContainer;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.utils.Utils3D;

public class HUDPhysicsState extends HUDElementContainer
{
	// how many frames to wait for rendering of
	public static int TIME_SAMPLE = 500;

	private BranchGroup behaviorBranchGroup = new BranchGroup();

	private TimeBehavior timeBehavior = new TimeBehavior();

	private HUDElement bgElement;

	private HUDElement textElement;

	private Color fpsTextColor = new Color(0.2f, 0.3f, 0.4f, 1f);

	private Font fpsTextFont = new Font("Arial", Font.PLAIN, 10);

	private HUDPhysicsStateData hudPhysicsStateData;

	public HUDPhysicsState(Canvas3D2D canvas)
	{
		this();
		addToCanvas(canvas);
	}

	public HUDPhysicsState()
	{
		timeBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		behaviorBranchGroup.addChild(timeBehavior);

		bgElement = new HUDElement(155, 25);
		bgElement.setLocation(5, 185);
		bgElement.getGraphics().setColor(new Color(0.5f, 1f, 1f, 0.4f));
		bgElement.getGraphics().fillRoundRect(0, 0, 155, 25, 15, 15);
		add(bgElement);
		textElement = new HUDElement(140, 20);
		textElement.setLocation(8, 191);
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
			textElement.clear();
			textElement.getGraphics().setColor(fpsTextColor);
			textElement.getGraphics().setFont(fpsTextFont);
			if (hudPhysicsStateData != null)
			{
				textElement.getGraphics().drawString(
						"Os " + hudPhysicsStateData.getNumCollisionObjects() + ":" + hudPhysicsStateData.getAverageStepTimeMS()
								+ "ms", 0, 10);
			}
			// Set the trigger for the behavior
			wakeupOn(wakeUp);

		}
	}

	public BranchGroup getBehaviorBranchGroup()
	{
		return behaviorBranchGroup;
	}

	public HUDPhysicsStateData getHudPhysicsStateData()
	{
		return hudPhysicsStateData;
	}

	public void setHudPhysicsStateData(HUDPhysicsStateData hudPhysicsStateData)
	{
		this.hudPhysicsStateData = hudPhysicsStateData;
	}

	public static interface HUDPhysicsStateData
	{
		public int getAverageStepTimeMS();

		public int getNumCollisionObjects();
	}

}
