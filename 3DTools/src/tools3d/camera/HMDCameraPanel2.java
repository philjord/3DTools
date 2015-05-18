package tools3d.camera;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.View;
import javax.vecmath.Point3d;

import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.ovr.OculusRift03;
import tools3d.universe.VisualPhysicalUniverse;

public class HMDCameraPanel2 extends CameraPanel
{
	private Canvas3D2D canvas3D2Db;

	private OculusRift03 occ = new OculusRift03();

	public HMDCameraPanel2(VisualPhysicalUniverse universe)
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
		//Note do not use until // chosenCaps.setStereo((gct.getStereo() == GraphicsConfigTemplate.PREFERRED));
		//in joglpipeline is reversed (big job)

		GraphicsConfiguration config = template.getBestConfiguration(gc);

		canvas3D2D = new Canvas3D2D(config);
		canvas3D2D.setStereoEnable(true);
		canvas3D2D.setMonoscopicViewPolicy(View.LEFT_EYE_VIEW);
		camera = new Camera(canvas3D2D);

		canvas3D2Db = new Canvas3D2D(config);
		canvas3D2Db.setStereoEnable(true);
		canvas3D2Db.setMonoscopicViewPolicy(View.RIGHT_EYE_VIEW);

		View view = canvas3D2D.getView();
		view.setTrackingEnable(true);

		// note shared!
		//canvas3D2D.getScreen3D().setPhysicalScreenHeight(0.1);
		//canvas3D2D.getScreen3D().setPhysicalScreenWidth(0.1);
		
		//TODO: the images are being render seperated by physical screen distances!
		// possibly on the OR this would be fine, but I suspect it's not pulling real screen physicals 
		// because when I set the screen3D physical down 0.01 it goes crazy
		// notice nothing report physical size anywhere

		// odd my eye! are miles appart! see above
		PhysicalBody physicalBody = new PhysicalBody(new Point3d(-0.33, 0, 0), new Point3d(0.33, 0, 0));
		physicalBody.setNominalEyeOffsetFromNominalScreen(0.1);
		view.setPhysicalBody(physicalBody);
		view.setViewPolicy(View.HMD_VIEW);
		view.setUserHeadToVworldEnable(true);
		view.addCanvas3D(canvas3D2Db);
		PhysicalEnvironment pe = view.getPhysicalEnvironment();
		occ.init();
	//	pe.addInputDevice(occ);
	//	pe.setSensor(0, occ.getSensor(0)); // added at 0 which is head tracking index, so tracking availible = true
		//pe.setCoexistenceCenterInPworldPolicy(View.NOMINAL_HEAD);
		//pe.setCoexistenceToTrackerBase(t);
		//CanvasViewCache computeView() vworldToTrackerBase is fulled with maness
		// OK I see a major poiint! having a headless universe and a HMD has cause things to work a bit,
		// and has masked the total failures of teh HMD code here, haha because they were both using the same screen 3D!!

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
			super.startRendering();
			if (canvas3D2Db.getParent() != this)
			{
				add(canvas3D2Db);
				validate();
			}
			canvas3D2Db.startRenderer();
		}

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
