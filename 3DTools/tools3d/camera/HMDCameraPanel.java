package tools3d.camera;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.View;
import javax.swing.JPanel;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.universe.VisualPhysicalUniverse;

import com.sun.j3d.utils.universe.ViewingPlatform;

public class HMDCameraPanel extends JPanel
{
	private VisualPhysicalUniverse universe;

	private Canvas3D2D canvas3D2D;
	private Canvas3D2D canvas3D2Db;

	private Camera camera;

	private Dolly currentDolly;

	private boolean isRendering = false;

	public HMDCameraPanel(VisualPhysicalUniverse universe)
	{
		this.universe = universe;

		setLayout(new GridLayout(1, 1));//1,2

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

		canvas3D2D = new Canvas3D2D(config);

		camera = new Camera(canvas3D2D);
		
		//TODO: stereo gear?
		//canvas3D2D.setStereoEnable(true);
		//canvas3D2D.getView().setViewPolicy(View.HMD_VIEW);
		
		//canvas3D2Db = new Canvas3D2D(config);
		//canvas3D2D.getView().addCanvas3D(canvas3D2Db);

	}

	public void setPhysicalsVisible(boolean visible)
	{
		if (visible)
		{
			universe.setViewForPhysicalBranch(canvas3D2D.getView());
		}
		else
		{
			universe.clearViewForPhysicalBranch();
		}
	}

	public void setFreeDolly(Dolly newDolly)
	{
		if (currentDolly != null)
		{
			universe.removeViewingPlatform(currentDolly);
		}
		currentDolly = newDolly;
		// it is assumed to be added to the scene graph itself
		// universe.addViewingPlatform(currentDolly);
		camera.setAvatar(null);
		camera.setViewingPlatform(currentDolly);

		// camera.setAvatar(new SimpleAvatar("media/models/eyeAvatar.ac"));

	}

	public void setDolly(Dolly newDolly)
	{
		if (currentDolly != null)
		{
			universe.removeViewingPlatform(currentDolly);
		}
		currentDolly = newDolly;
		universe.addViewingPlatform(currentDolly);
		camera.setAvatar(null);
		camera.setViewingPlatform(currentDolly);
		//camera.setAvatar(new SimpleAvatar("media/models/eyeAvatar.ac"));
	}

	/** 
	 * NOTE instead of Dolly just a vahnilla vp
	 * @param viewingPlatform
	 */
	public void setViewingPlatform(ViewingPlatform viewingPlatform)
	{
		if (currentDolly != null)
		{
			universe.removeViewingPlatform(currentDolly);
		}
		currentDolly = null;
		universe.addViewingPlatform(viewingPlatform);
		camera.setAvatar(null);
		camera.setViewingPlatform(viewingPlatform);
	}

	public void clearDolly()
	{
		camera.setViewingPlatform(null);
		universe.removeViewingPlatform(currentDolly);
	}

	public void stopRendering()
	{
		System.out.println("NEVER CALL THIS METHOD DAMN IT! it is a major memory leak; find a solution!");
		//new Exception("called by").printStackTrace();

		// maybe try Canvas3D.stopRenderer()
		canvas3D2D.stopRenderer();

		// stop rendering by removing the canvas
		if (this.isAncestorOf(canvas3D2D))
		{
			remove(canvas3D2D);
			//remove(canvas3D2Db);
			isRendering = false;
		}
	}

	public void startRendering()
	{
		if (!isRendering)
		{
			if (canvas3D2D.getParent() != this)
			{
				 
				// start rendering by adding the canvas
				add(canvas3D2D);
				//add(canvas3D2Db);
				validate();
			}

			isRendering = true;
			canvas3D2D.startRenderer();
			//canvas3D2Db.startRenderer();
		}
	}

	public boolean isRendering()
	{
		return isRendering;
	}

	public Canvas3D2D getCanvas3D2D()
	{
		return canvas3D2D;
	}

	public Dolly getCurrentDolly()
	{
		return currentDolly;
	}

	public void setSceneAntialiasingEnable(boolean aaRequired)
	{
		if (canvas3D2D != null && canvas3D2D.getView() != null)
		{
			System.out.println("setSceneAntialiasingEnable " + aaRequired);
			canvas3D2D.getView().setSceneAntialiasingEnable(aaRequired);
		}
	}

}
