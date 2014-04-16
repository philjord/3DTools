package tools3d.universe;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Clip;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ExponentialFog;
import javax.media.j3d.Fog;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import tools3d.environment.RainyArea;
import tools3d.environment.sky.SkySphere;
import tools3d.utils.Utils3D;

import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * 
 */
public class DefaultUniverse extends SimpleUniverse
{
	// front and back clips, should be ratio of no more than 3000
	public static float FRONT_CLIP = 0.1f;

	public static float BACK_CLIP = 300f;

	private BranchGroup terrainGroup = new BranchGroup();

	private BranchGroup mainSceneGroup = new BranchGroup();

	private BranchGroup environmentGroup = new BranchGroup();

	private BranchGroup behaviorGroup = new BranchGroup();

	private SkySphere sky = new SkySphere();

	private ExponentialFog fog = new ExponentialFog(new Color3f(1.0f, 1.0f, 1.0f), 0.5f);

	private AmbientLight ambLight;

	private DirectionalLight sunLight;

	private Clip nightClip = new Clip(150f);

	private Clip fogClip = new Clip(100f);

	private BranchGroup nightClipGroup = new BranchGroup();

	private BranchGroup fogClipGroup = new BranchGroup();

	private RainyArea rainyModel;

	public static ViewingPlatform createVWorldPlatform()
	{
		ViewingPlatform viewingPlatform = new ViewingPlatform();
		TransformGroup viewTransGroup = viewingPlatform.getViewPlatformTransform();
		viewTransGroup.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		return viewingPlatform;
	}

	public DefaultUniverse()
	{
		super(createVWorldPlatform(), new Viewer(new Canvas3D(SimpleUniverse.getPreferredConfiguration())));

		//some useful parts of this universe
		//Viewer viewer = getViewer();
		//ViewingPlatform viewingPlatform = getViewingPlatform();		
		//View view = viewer.getView();
		//ViewPlatform viewPlatform = viewingPlatform.getViewPlatform();
		//TransformGroup viewTransGroup = getViewingPlatform().getViewPlatformTransform();

		//set up the view
		getViewer().getView().setFrontClipPolicy(View.VIRTUAL_EYE);
		getViewer().getView().setBackClipDistance(BACK_CLIP);
		getViewer().getView().setFrontClipDistance(FRONT_CLIP);
		// set max frame rate to 50 so server can get in
		getViewer().getView().setMinimumFrameCycleTime(2);

		//TODO: I believe I can remove these calls
		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		getViewingPlatform().setPlatformGeometry(pg);

		getViewer().createAudioDevice();

		// set capabilities for the various branch groups
		mainSceneGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		mainSceneGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		environmentGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		environmentGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		behaviorGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		behaviorGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		terrainGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		terrainGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		terrainGroup.setCapability(BranchGroup.ALLOW_DETACH);
		terrainGroup.setUserData("Terrain Group");

		// initialise the sound system
		//SoundEngine mt = new SoundEngine();
		//behaviorGroup.addChild(mt);

		// put a sky in place
		sky.setApplicationBounds(Utils3D.defaultBounds);
		environmentGroup.addChild(sky);

		// Create ambient light	and add it
		Color3f alColor = new Color3f(0.6f, 0.6f, 0.6f);
		ambLight = new AmbientLight(true, alColor);
		ambLight.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		ambLight.setInfluencingBounds(Utils3D.defaultBounds);
		environmentGroup.addChild(ambLight);

		// Create sun light and add it
		Color3f slColor = new Color3f(1.0f, 1.0f, 1.0f);
		sunLight = new DirectionalLight(slColor, new Vector3f(0.2f, -1.0f, -0.2f));
		sunLight.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		sunLight.setInfluencingBounds(Utils3D.defaultBounds);
		environmentGroup.addChild(sunLight);

		// Add the fog
		fog.setCapability(Fog.ALLOW_INFLUENCING_BOUNDS_WRITE);
		environmentGroup.addChild(fog);

		nightClip.setApplicationBounds(Utils3D.defaultBounds);
		fogClip.setApplicationBounds(Utils3D.defaultBounds);
		nightClipGroup.addChild(nightClip);
		nightClipGroup.setCapability(BranchGroup.ALLOW_DETACH);
		fogClipGroup.addChild(fogClip);
		fogClipGroup.setCapability(BranchGroup.ALLOW_DETACH);

		// initialise the rain model
		//Color3f rainColor = new Color3f(0.6f, 0.6f, 0.65f);
		//Vector3f windVector = new Vector3f(0.2f, 0f, 0f);
		//rainyModel = new RainyArea(14, 0, 25000, rainColor, 4.5f, 0.8f, 50, windVector, 80f, 80f, 20f, 20000);
		//rainyModel.hoverOver(getViewingPlatform().getViewPlatformTransform());
		//environmentGroup.addChild(rainyModel);

		mainSceneGroup.setCapability(BranchGroup.ALLOW_DETACH);
		environmentGroup.setCapability(BranchGroup.ALLOW_DETACH);
		behaviorGroup.setCapability(BranchGroup.ALLOW_DETACH);

		addBranchGraph(mainSceneGroup);
		addBranchGraph(environmentGroup);
		addBranchGraph(behaviorGroup);
		addBranchGraph(terrainGroup);
	}

	public BranchGroup getTerrainBranch()
	{
		return terrainGroup;
	}

	public void addToTerrainBranch(BranchGroup newGroup)
	{
		newGroup.setCapability(BranchGroup.ALLOW_DETACH);
		terrainGroup.addChild(newGroup);
	}

	public BranchGroup getSceneBranch()
	{
		return mainSceneGroup;
	}

	public void addToSceneBranch(BranchGroup newGroup)
	{
		newGroup.setCapability(BranchGroup.ALLOW_DETACH);
		mainSceneGroup.addChild(newGroup);
	}

	public void addToEnvironmentBranch(BranchGroup newGroup)
	{
		newGroup.setCapability(BranchGroup.ALLOW_DETACH);
		environmentGroup.addChild(newGroup);
	}

	public void addToBehaviorBranch(BranchGroup newGroup)
	{
		newGroup.setCapability(BranchGroup.ALLOW_DETACH);
		behaviorGroup.addChild(newGroup);
	}

	public void setNightTime()
	{
		sunLight.setInfluencingBounds(null);
		sky.setNightSky();
		environmentGroup.addChild(nightClipGroup);
	}

	public void setDayTime()
	{
		sunLight.setInfluencingBounds(Utils3D.defaultBounds);
		sky.setDaySky();
		environmentGroup.removeChild(nightClipGroup);
	}

	public void setFogOn()
	{
		fog.setInfluencingBounds(Utils3D.defaultBounds);
		sky.setFogSky();
		environmentGroup.addChild(fogClipGroup);
	}

	public void setFogOff()
	{
		fog.setInfluencingBounds(null);
		sky.setDaySky();
		environmentGroup.removeChild(fogClipGroup);
	}

	public void turnRainOn()
	{
		rainyModel.turnRainOn();
	}

	public void turnRainOff()
	{
		rainyModel.turnRainOff();
	}

}
