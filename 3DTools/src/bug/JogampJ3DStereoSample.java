package bug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformAWTBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.ConfiguredUniverse;

public class JogampJ3DStereoSample
{

	//TO MAKE GO JoglPipeline
/*	  AWTGraphicsConfiguration awtConfig =
	    		(AWTGraphicsConfiguration)Canvas3D.graphicsConfigTable.get(cv.graphicsConfiguration).getPrivateData();

	    QueryCanvas canvas = new QueryCanvas(awtConfig, querier);
	    
//PJPJPJPJPJ
			GraphicsEnvironment lge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice sd = lge.getDefaultScreenDevice();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		sd = ge.getScreenDevices()[2];
	    Frame f = new Frame("",sd.getDefaultConfiguration());
      f.setUndecorated(true);
      f.setLayout(new BorderLayout());
	       f.setLocation(sd.getDefaultConfiguration().getBounds().x, f.getY());

	    Frame f = new Frame();
      f.setUndecorated(true);
      f.setLayout(new BorderLayout());	   */
      
      
	private static boolean CHECK_SCREEN_UPDATES = true;

	/**
	 * This is a simple example showing how to use a 3D-stereoscopic canvas with Java3D.
	 * This works also very nice with Jogamp's Java3D implementation. 
	 * Tested with J3D Version 1.6_pre11 and Java 8 u60 and Java 7 u80-b15.
	 * Thanks go to Julien Gousse, Harvey Harrison and all other contributors for their great work!!!
	 * 
	 * @author Bjorn Sommer for CELLmicrocosmos.org
	 */
	public static void main(String[] args)
	{

		// create universe and Canvas3D with stereo support
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
		GraphicsEnvironment lge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice sd = lge.getDefaultScreenDevice();

		System.out.println("getDefaultScreenDevice " + sd);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice gd : ge.getScreenDevices())
		{
			System.out.println("I've got " + gd.getIDstring());
			System.out.println(" " + gd.toString());
			System.out.println("" + gd.getDisplayMode().getWidth());
			System.out.println("" + gd.getDisplayMode().getHeight());

		}
		sd = ge.getScreenDevices()[2];
		System.out.println("sd= " + sd);

		GraphicsConfiguration config = sd.getBestConfiguration(template);
		Canvas3D canvas3D = !CHECK_SCREEN_UPDATES ? new Canvas3D(config) : new Canvas3D(config) {
			private static final long serialVersionUID = 500419811339553034L;

			// this is a nice place to force e.g. an offscreen canvas or 2D labels on the 3D screen to be updated 
			// - and yes, this also works perfect with Jogamp's J3D!
			public void postRender()
			{
				super.postRender();
				System.out.print("update...");
			}
		};

		if (canvas3D.getStereoAvailable())
		{
			System.out.println("Stereo available");
		}
		else
		{
			System.out.println("Okay, Stereo is not working. \n" + "If you have an NVIDIA Quadro graphics or similar professional graphics, check out that stereo is activated in the driver.\n"
					+ "Example: 1. NVIDIA Control Panel -> 3D Settings -> Manage 3D Settings -> Global Settings -> Stereo: Enable -> On \n"
					+ "            Note: You might have to swap the eyes here in the settings - depends on the configuration."
					+ "         2. NVIDIA Control Panel -> Stereoscopic 3D -> Set up stereoscopic 3D -> Enable Stereo 3D\n"
					+ "            Note: This setting is optional and depends on your configuration/driver. \n"
					+ "                  It might be also that this checkbox has to be deactivated - although this\n"
					+ "                  theoretically does not make any sense. But it debends on the hardware configuration.\n\n"
					+ "If you do not have a (semi-)professional graphics card, this will usually not work at all. \n"
					+ "Then, you need e.g. a 3D TV and do the side-by-side rendering with Java3D on your own - \n" + "but this makes only sense in full screen mode.");
		}

		// create an object
		BranchGroup bg1 = new BranchGroup();
		TransformGroup tg1 = new TransformGroup();
		Transform3D t3d1 = new Transform3D();
		t3d1.set(new Vector3d(0, 0, 0));
		t3d1.rotX(Math.toRadians(45));
		tg1.setTransform(t3d1);
		ColorCube colorCube = new ColorCube(0.3);

		// create a light
		BranchGroup bg2 = new BranchGroup();
		TransformGroup tg2 = new TransformGroup();
		Transform3D t3d2 = new Transform3D();
		t3d2.set(new Vector3d(0, 0, 0));
		tg2.setTransform(t3d2);
		AmbientLight aLight = new AmbientLight();
		aLight.setEnable(true);
		aLight.setColor(new Color3f(1, 1, 1));

		Dimension dimension = new Dimension(400, 400);

		// create the panel and add the Canvas3D
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add("Center", canvas3D);
		canvas3D.setVisible(true);
		canvas3D.setSize(dimension);

		// create the universe
		ConfiguredUniverse universe = new ConfiguredUniverse(canvas3D);

		// add object to scene
		tg1.addChild(colorCube);
		bg1.addChild(tg1);
		universe.addBranchGraph(bg1);

		// add light to scene
		tg2.addChild(aLight);
		bg2.addChild(tg2);
		universe.addBranchGraph(bg2);

		// set the position of the view
		Transform3D t3dVP = new Transform3D(new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 3, 0, 0, 0, 1));
		universe.getViewingPlatform().getViewPlatformTransform().setTransform(t3dVP);
		;

		// create the panel and frame
		panel.setSize(dimension);
		panel.setVisible(true);
		JFrame f = new JFrame("", sd.getDefaultConfiguration());
		f.setSize(dimension);
		f.add(panel);

		GraphicsConfiguration gc = sd.getDefaultConfiguration();
		f.setBounds(gc.getBounds());

		f.setVisible(true);

		// set the eye positions
		Point3d eyePosRight = new Point3d(-0.01, 0, 0);
		Point3d eyePosLeft = new Point3d(0.01, 0, 0);
		canvas3D.setLeftManualEyeInImagePlate(eyePosLeft);
		canvas3D.setRightManualEyeInImagePlate(eyePosRight);
		canvas3D.getView().getPhysicalBody().setLeftEyePosition(eyePosLeft);
		canvas3D.getView().getPhysicalBody().setRightEyePosition(eyePosRight);

		// add a behavior
		//  note: I experienced here, without the behavior, that Java likes to show a white screen instead of the image
		OrbitBehavior orbitBehavior = new OrbitBehavior(canvas3D,
				ViewPlatformAWTBehavior.KEY_LISTENER | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.REVERSE_TRANSLATE | OrbitBehavior.PROPORTIONAL_ZOOM);
		orbitBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		universe.getViewingPlatform().setViewPlatformBehavior(orbitBehavior);
	}

}