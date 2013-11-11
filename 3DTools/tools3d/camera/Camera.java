package tools3d.camera;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.View;

import tools3d.audio.JOALMixer;

import com.sun.j3d.utils.universe.Viewer;

//TODO: Camera really doesn't need to extend the utility class viewer
public class Camera extends Viewer
{
	// front and back clips, should be ratio of no more than 3000 
	//note the below  is at 50000!
	public static float FRONT_CLIP = 0.1f;

	public static float BACK_CLIP = 5000f;

	private double FOV = 65;

	private JOALMixer mixer = null;

	public Camera(Canvas3D canvas3D)
	{
		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		super(canvas3D);

		getView().setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

		// set up the view
		getView().setFrontClipPolicy(View.VIRTUAL_EYE);
		getView().setBackClipDistance(BACK_CLIP);
		getView().setFrontClipDistance(FRONT_CLIP);
		getView().setMinimumFrameCycleTime(15);// max 66fps

		// default in View = double fov = 45.0 * Math.PI / 180.0;
		// 45 is too "zoomed", 60 seems more natural, but perhaps even more might be better, possibly up to the 90 mark?
		// COD4 on 4:3 screen uses 65 but on 16:9 uses 81
		System.out.println("FOV set to  " + FOV);
		double fov = FOV * Math.PI / 180.0;
		getView().setFieldOfView(fov);

		//other wise restricted access exception
		if (getView().getUserHeadToVworldEnable())
		{
			// create and adds a joalmixer as the audio device
			mixer = new JOALMixer(getPhysicalEnvironment());
			boolean success = mixer.initialize();

			if (!success)
			{
				System.out.println("Open AL failed to init");
				// remove the audio device
				getPhysicalEnvironment().setAudioDevice(null);
			}
		}

	}

}
