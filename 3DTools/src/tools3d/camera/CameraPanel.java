package tools3d.camera;

import com.sun.j3d.utils.universe.ViewingPlatform;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.universe.VisualPhysicalUniverse;

public class CameraPanel implements ICameraPanel
{
	protected VisualPhysicalUniverse universe;

	protected Canvas3D2D canvas3D2D;

	protected Camera camera;

	private Dolly currentDolly;

	private boolean isRendering = false;

	/**
	 * mono not HMD
	 * @param universe
	 */
	public CameraPanel(VisualPhysicalUniverse universe)
	{
		this.universe = universe;

		canvas3D2D = new Canvas3D2D();
		camera = new Camera(canvas3D2D);
	}

	/**
	 * HMD only
	 */
	protected CameraPanel()
	{
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

	/**
	 * Must be a dolly
	 * @see tools3d.camera.ICameraPanel#setDolly(tools3d.camera.IDolly)
	 */
	@Override
	public void setDolly(IDolly newDolly)
	{
		if (currentDolly != null)
		{
			universe.removeViewingPlatform(currentDolly);
		}
		currentDolly = (Dolly) newDolly;
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
		if (canvas3D2D.isRendererRunning())
		{
			System.out.println("NEVER CALL THIS METHOD DAMN IT! it is a major memory leak; find a solution!");
			//new Exception("called by").printStackTrace();

			// maybe try Canvas3D.stopRenderer()
			//canvas3D2D.stopRenderer();

			// stop rendering by removing the canvas
			//		if (this.isAncestorOf(canvas3D2D))
			{
				//			remove(canvas3D2D);
				isRendering = false;
			}
		}
	}

	public void startRendering()
	{
		if (!isRendering)
		{
			//		if (canvas3D2D.getParent() != this)
			{

				// start rendering by adding the canvas
				//			add(canvas3D2D);
				//			validate();
				//canvas3D2D.addNotify();
			}

			isRendering = true;
			canvas3D2D.startRenderer();
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

	public void setSceneAntialiasingEnable(boolean aaRequired)
	{
		if (canvas3D2D != null && canvas3D2D.getView() != null)
		{
			System.out.println("setSceneAntialiasingEnable " + aaRequired);
			canvas3D2D.getView().setSceneAntialiasingEnable(aaRequired);
		}
	}

	@Override
	public IDolly getDolly()
	{
		return currentDolly;
	}

	public Camera getCamera()
	{
		return camera;
	}
}
