package tools3d.camera;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.View;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import tools3d.audio.JOALMixer;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.universe.VisualPhysicalUniverse;

public class HMDCameraPanel extends JPanel implements ICameraPanel
{
	public static float FRONT_CLIP = 0.1f;

	public static float BACK_CLIP = 5000f;

	protected VisualPhysicalUniverse universe;

	private double FOV = 80;

	private JOALMixer mixer = null;

	private Canvas3D2D leftCanvas3D2D;

	private Canvas3D2D rightCanvas3D2D;

	private OcculusRift occ = new OcculusRift();

	private PhysicalBody physicalBody = null;

	private PhysicalEnvironment physicalEnvironment = null;

	private View leftView = null;

	private View rightView = null;

	private HMDCamDolly currentDolly;// viewPlatform

	private boolean isRendering = false;

	public HMDCameraPanel(VisualPhysicalUniverse universe)
	{
		this.universe = universe;

		setLayout(new GridLayout(1, 2));
		physicalBody = new PhysicalBody(new Point3d(-0.033, 0, 0), new Point3d(0.033, 0, 0));
		physicalEnvironment = new PhysicalEnvironment();

		//This stuff has to be in synch with the ScreenResolution class
		// I must do this in order to enable the stencil buffer
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration[] gc = gd.getConfigurations();
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		//stencil setup stuff
		//template.setStencilSize(8);		
		// we must also set the stencil buffer to clear each frame (madness!)
		// put  -Dj3d.stencilClear=true in your vm arguments!!!  

		// antialiasing REQUIRED is good to have
		template.setSceneAntialiasing(GraphicsConfigTemplate.REQUIRED);

		GraphicsConfiguration config = template.getBestConfiguration(gc);

		leftCanvas3D2D = new Canvas3D2D(config);
		leftCanvas3D2D.setMonoscopicViewPolicy(View.CYCLOPEAN_EYE_VIEW);

		rightCanvas3D2D = new Canvas3D2D(config);
		rightCanvas3D2D.setMonoscopicViewPolicy(View.CYCLOPEAN_EYE_VIEW);

		leftView = createView(leftCanvas3D2D);
		rightView = createView(rightCanvas3D2D);

		//	leftCanvas3D2D.getScreen3D().setPhysicalScreenHeight(0.1);
		//	leftCanvas3D2D.getScreen3D().setPhysicalScreenWidth(0.1);

	}

	private View createView(Canvas3D c)
	{
		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		View view = new View();
		view.addCanvas3D(c);
		view.setPhysicalBody(physicalBody);
		view.setPhysicalEnvironment(physicalEnvironment);
		view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

		// set up the view
		view.setFrontClipPolicy(View.VIRTUAL_EYE);
		view.setBackClipDistance(BACK_CLIP);
		view.setFrontClipDistance(FRONT_CLIP);
		view.setMinimumFrameCycleTime(15);// max 66fps

		// default in View = double fov = 45.0 * Math.PI / 180.0;
		// 45 is too "zoomed", 60 seems more natural, but perhaps even more might be better, possibly up to the 90 mark?
		// COD4 on 4:3 screen uses 65 but on 16:9 uses 81
		System.out.println("FOV set to  " + FOV);
		double fov = FOV * Math.PI / 180.0;
		view.setFieldOfView(fov);

		//other wise restricted access exception
		if (view.getUserHeadToVworldEnable())
		{
			// create and adds a joalmixer as the audio device
			mixer = new JOALMixer(physicalEnvironment);
			boolean success = mixer.initialize();

			if (!success)
			{
				System.out.println("Open AL failed to init");
				// remove the audio device
				physicalEnvironment.setAudioDevice(null);
			}
		}
		return view;
	}

	public void stopRendering()
	{
		if (leftCanvas3D2D.isRendererRunning())
		{
			leftCanvas3D2D.stopRenderer();
			if (this.isAncestorOf(leftCanvas3D2D))
			{
				remove(leftCanvas3D2D);

			}
			rightCanvas3D2D.stopRenderer();
			if (this.isAncestorOf(rightCanvas3D2D))
			{
				remove(rightCanvas3D2D);
			}
			isRendering = false;
		}
	}

	public void startRendering()
	{
		if (!isRendering())
		{
			if (leftCanvas3D2D.getParent() != this)
			{
				add(leftCanvas3D2D);
				add(rightCanvas3D2D);
				validate();
				leftCanvas3D2D.startRenderer();
				rightCanvas3D2D.startRenderer();
			}

			isRendering = true;
		}
	}

	

	public void setPhysicalsVisible(boolean visible)
	{
		if (visible)
		{
			universe.setViewForPhysicalBranch(leftView);
		}
		else
		{
			universe.clearViewForPhysicalBranch();
		}
	}

	public void setHMDCamDolly(HMDCamDolly headCamDolly)
	{
		if (currentDolly != null)
		{
			universe.removeViewingPlatform(currentDolly);
		}
		currentDolly = headCamDolly;
		universe.addViewingPlatform(currentDolly);
		// it is assumed to be added to the scene graph itself
		// universe.addViewingPlatform(currentDolly);		 
		leftView.attachViewPlatform(currentDolly.getLeftViewPlatform());
		rightView.attachViewPlatform(currentDolly.getRightViewPlatform());

	}

		

	public boolean isRendering()
	{
		return isRendering;
	}

	public Canvas3D2D getCanvas3D2D()
	{
		return leftCanvas3D2D;
	}

	

	public void setSceneAntialiasingEnable(boolean aaRequired)
	{
		if (leftView != null)
		{
			System.out.println("setSceneAntialiasingEnable " + aaRequired);
			leftView.setSceneAntialiasingEnable(aaRequired);
			rightView.setSceneAntialiasingEnable(aaRequired);
		}
	}

}
