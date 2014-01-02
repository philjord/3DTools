package tools3d.camera;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.View;
import javax.vecmath.Point3d;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.universe.VisualPhysicalUniverse;

public class HMDCameraPanel extends CameraPanel
{
	private Canvas3D2D canvas3D2Db;

	public HMDCameraPanel(VisualPhysicalUniverse universe)
	{
		this.universe = universe;

		setLayout(new GridLayout(1, 2));

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
		template.setStereo(GraphicsConfigTemplate.PREFERRED);//can't say required! kills AA

		GraphicsConfiguration config = template.getBestConfiguration(gc);

		canvas3D2D = new Canvas3D2D(config);
		canvas3D2D.setStereoEnable(true);
		canvas3D2D.setMonoscopicViewPolicy(View.LEFT_EYE_VIEW);
		camera = new Camera(canvas3D2D);

		canvas3D2Db = new Canvas3D2D(config);
		canvas3D2Db.setStereoEnable(true);
		canvas3D2Db.setMonoscopicViewPolicy(View.RIGHT_EYE_VIEW);	
		
		canvas3D2D.getView().setTrackingEnable(true);
		canvas3D2D.getView().setPhysicalBody(new PhysicalBody(new Point3d(-0.033, 0, 0), new Point3d(0.033, 0, 0)));
		canvas3D2D.getView().setViewPolicy(View.HMD_VIEW);
		canvas3D2D.getView().addCanvas3D(canvas3D2Db);
	}

	public void stopRendering()
	{
		super.stopRendering();
		if (canvas3D2Db.isRendererRunning())
		{
			canvas3D2Db.stopRenderer();
			if (this.isAncestorOf(canvas3D2Db))
			{
				remove(canvas3D2Db);
			}
		}
	}

	public void startRendering()
	{

		if (!isRendering())
		{
			if (canvas3D2Db.getParent() != this)
			{
				add(canvas3D2Db);
				validate();
			}
			canvas3D2Db.startRenderer();
		}
		super.startRendering();
	}

	public Canvas3D2D getCanvas3D2Db()
	{
		return canvas3D2Db;
	}

	public void setSceneAntialiasingEnable(boolean aaRequired)
	{
		super.setSceneAntialiasingEnable(aaRequired);
		if (canvas3D2Db != null && canvas3D2Db.getView() != null)
		{
			canvas3D2Db.getView().setSceneAntialiasingEnable(aaRequired);
		}
	}

}
