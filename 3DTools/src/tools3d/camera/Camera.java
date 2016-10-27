package tools3d.camera;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.View;

import com.sun.j3d.utils.universe.Viewer;

import tools3d.audio.JOALMixer;

//TODO: Camera really doesn't need to extend the utility class viewer
// but I'd need to reproduce the ViewingPlatfrom business too
public class Camera extends Viewer
{
	// front and back clips, for 16bit should be ratio of no more than 3000:1 
	// for 24 can be anything up to 50000:1
	public static float FRONT_CLIP = 0.1f;

	public static float BACK_CLIP = 5000f;

	public static long MIN_FRAME_CYCLE_TIME = 15;// max 66fps

	private double FOV = 80;

	public static JOALMixer mixer = null;

	public Camera(Canvas3D canvas3D)
	{
		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		super(canvas3D);

		getView().setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

		// set up the view
		getView().setFrontClipPolicy(View.VIRTUAL_EYE);
		getView().setFrontClipDistance(FRONT_CLIP);
		getView().setBackClipDistance(BACK_CLIP);
		getView().setMinimumFrameCycleTime(MIN_FRAME_CYCLE_TIME);
		 

		// default in View = double fov = 45.0 * Math.PI / 180.0;
		// 45 is too "zoomed", 60 seems more natural, but perhaps even more might be better, possibly up to the 90 mark?
		// COD4 on 4:3 screen uses 65 but on 16:9 uses 81
		System.out.println("FOV set to  " + FOV);
		double fov = FOV * Math.PI / 180.0;
		getView().setFieldOfView(fov);

		//other wise restricted access exception
		if (mixer == null && getView().getUserHeadToVworldEnable())
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

	/**
	 * Intel and mobile cards sometime provide 16bit zbuffers, so the ratio of front to back
	 * in those case needs to be 1:1000, 24 bit can probably survive 1:100000
	 * TODO: detect and report the zbuffer depth
	 */
	public void setClips(float front, float back)
	{
		getView().setFrontClipDistance(front);
		getView().setBackClipDistance(back);

	}

}
