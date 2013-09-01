package tools3d.camera.old;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.audio.JOALMixer;
import tools3d.hud.HUD;
import tools3d.universe.VisualPhysicalUniverse;
import tools3d.utils.YawPitch;
import tools3d.utils.scenegraph.LocationUpdateListener;

import com.sun.j3d.utils.universe.MultiTransformGroup;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class HeadCam implements LocationUpdateListener
{
	// front and back clips, should be ratio of no more than 3000
	public static float FRONT_CLIP = 0.1f;

	public static float BACK_CLIP = 5000f;

	private Viewer viewer;

	private ViewingPlatform viewingPlatform;

	private TransformGroup locationTransformGroup;

	private TransformGroup yawTransformGroup;

	private TransformGroup headTranslationTransformGroup;

	private TransformGroup headPitchTransformGroup;

	public HeadCam(VisualPhysicalUniverse universe, Canvas3D canvas3D)
	{
		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		viewer = new Viewer(canvas3D);

		viewer.getView().setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

		//viewer.setAvatar(new SimpleMD3Avatar("media/models/eyeAvatar.ac"));

		// create the viewingplatfrom with 4 transforms (loc, yaw, headpos, pitch)
		viewingPlatform = new ViewingPlatform(4);
		viewer.setViewingPlatform(viewingPlatform);

		// Note the tansform are already attached to each other
		MultiTransformGroup mtg = viewingPlatform.getMultiTransformGroup();
		locationTransformGroup = mtg.getTransformGroup(0);
		yawTransformGroup = mtg.getTransformGroup(1);
		headTranslationTransformGroup = mtg.getTransformGroup(2);
		headPitchTransformGroup = mtg.getTransformGroup(3);

		//set up the view
		viewer.getView().setFrontClipPolicy(View.VIRTUAL_EYE);
		viewer.getView().setBackClipDistance(BACK_CLIP);
		viewer.getView().setFrontClipDistance(FRONT_CLIP);
		// set max frame rate to 50 
		viewer.getView().setMinimumFrameCycleTime(20);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		viewingPlatform.setPlatformGeometry(pg);

		//actually add our viewer to the universe
		universe.addViewingPlatform(viewingPlatform);

		//create and adds a joalmixer as the audio device  
		JOALMixer mixer = new JOALMixer(viewer.getPhysicalEnvironment());
		mixer.initialize();
	}

	public void setHUD(HUD hud)
	{
		viewingPlatform.getPlatformGeometry().addChild(hud);
	}

	/**
	 * @see tools3d.utils.scenegraph.LocationUpdateListener#locationUpdated(Quat4f, Vector3f)
	 */
	//deburner
	private Transform3D locationTransform = new Transform3D();

	private Transform3D yawTransform = new Transform3D();

	private Transform3D headTranslationTransform = new Transform3D();

	private Transform3D headPitchTransform = new Transform3D();

	private YawPitch newYawPitch = new YawPitch();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{

		// get the yaw and pitch out
		newYawPitch.set(rot);

		// copy the location position
		locationTransform.set(trans);
		locationTransformGroup.setTransform(locationTransform);

		// set yaw
		yawTransform.rotY(newYawPitch.getYaw());
		yawTransformGroup.setTransform(yawTransform);

		// set head
		headTranslationTransform.set(new Vector3d(0, 1.8, 0));
		headTranslationTransformGroup.setTransform(headTranslationTransform);

		// set pitch
		headPitchTransform.rotX(newYawPitch.getPitch());
		headPitchTransformGroup.setTransform(headPitchTransform);
	}

}
