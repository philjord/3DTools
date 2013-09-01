package tools3d.hud.old;

import java.awt.Font;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Color3f;

import tools3d.utils.Utils3D;

import com.sun.j3d.utils.geometry.Text2D;

public class FPSCounter extends BranchGroup
{
	// how many frames to wait for rendering of
	public static int SAMPLE_SIZE = 5;

	private long currtime = 0;

	private long lasttime = 0;

	private long deltatime;

	private Text2D fpsText;

	// TODO: apparently View.getLastFrameDuration gives a good fps value

	public FPSCounter()
	{
		TransformGroup scaleToSize = new TransformGroup();
		Transform3D t = new Transform3D();
		t.setScale(0.05f);
		scaleToSize.setTransform(t);

		fpsText = new Text2D("      ", new Color3f(1f, 1f, 1f), "Courier New", 16, Font.PLAIN);
		fpsText.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_READ);
		fpsText.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		fpsText.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		// setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		addChild(scaleToSize);
		scaleToSize.addChild(fpsText);
		FpsBehavior fpsBehavior = new FpsBehavior();
		fpsBehavior.setSchedulingBounds(Utils3D.defaultBounds);
		fpsBehavior.setEnable(true);
		addChild(fpsBehavior);

	}

	private class FpsBehavior extends Behavior
	{
		private WakeupOnElapsedFrames FPSWakeUp = new WakeupOnElapsedFrames(SAMPLE_SIZE);

		public void initialize()
		{
			wakeupOn(FPSWakeUp);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void processStimulus(Enumeration critera)
		{
			currtime = System.currentTimeMillis();

			deltatime = currtime - lasttime;
			// time is in millisec, so multiply by 1000 to get frames/sec
			double fps = SAMPLE_SIZE / (deltatime / 1000.0);

			fpsText.setString(" " + ((int) fps));

			// System.out.println("Frames/sec : "+ nf.format(fps) );

			lasttime = System.currentTimeMillis();

			// Set the trigger for the behavior
			wakeupOn(FPSWakeUp);

		}
	}

}
