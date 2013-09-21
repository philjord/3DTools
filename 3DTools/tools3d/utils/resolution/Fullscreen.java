package tools3d.utils.resolution;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java3d.nativelinker.Java3dLinker2;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * A demo to illustrate fullscreen exclusive mode with Java3D
 * @author Kevin J. Duling (kevin@duling.us)
 */
public final class Fullscreen

{
	private final GraphicsDevice gd;

	private final JFrame win;

	/**
	 * Default constructor.
	 */
	private Fullscreen()
	{

		DisplayDialog dlg = new DisplayDialog(null);
		dlg.setVisible(true);
		DisplayMode desiredMode = dlg.getDesiredDisplayMode();
		if (desiredMode == null)
			System.exit(0);
		boolean runFullscreen = dlg.fullscreen();

		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		win = new JFrame("Fullscreen Example", config);
		if (runFullscreen)
			win.setUndecorated(true);
		win.setResizable(false);
		win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		Canvas3D canvas3D = new Canvas3D(config);
		win.add(canvas3D);
		canvas3D.setFocusable(true);
		canvas3D.requestFocus();
		canvas3D.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				final int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) || ((keyCode == KeyEvent.VK_C) && e.isControlDown()))
				{
					System.exit(0);
				}
			}
		});

		if (runFullscreen)
		{
			gd.setFullScreenWindow(win);
			if (gd.getFullScreenWindow() == null)
				System.out.println("Did not get fullscreen exclusive mode");
			else
				System.out.println("Got fullscreen exclusive mode");

			if (gd.isDisplayChangeSupported())
			{
				gd.setDisplayMode(desiredMode);
			}
		}
		else
		{
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			if (desiredMode.getWidth() > size.getWidth() || desiredMode.getHeight() > size.getHeight())
			{
				JOptionPane.showMessageDialog(null, "Resizing window to match desktop settings " + size, "Window Too Large",
						JOptionPane.ERROR_MESSAGE);
				win.setSize(size);
			}
			else
			{
				win.setSize(desiredMode.getWidth(), desiredMode.getHeight());
				win.setLocation(new Point((int) (size.getWidth() - win.getWidth()) >> 1, (int) (size.getHeight() - win.getHeight()) >> 1));
			}
			win.setVisible(true);
		}
		SimpleUniverse su = new SimpleUniverse(canvas3D);
		su.getViewingPlatform().setNominalViewingTransform(); // back away from object a little
		su.addBranchGraph(createSceneGraph());

		System.out.println("add VM arg -Dsun.java2d.noddraw=true or no render nothing");

		// don't bother super fast for now
		//ConsoleFPSCounter fps = new ConsoleFPSCounter();
		//su.addBranchGraph(fps.getBehaviorBranchGroup());

	}

	/**
	 * Builds a scenegraph for the application to render.
	 * @return the root level of the scenegraph
	 */
	private static BranchGroup createSceneGraph()
	{
		final BranchGroup objRoot = new BranchGroup();

		// Create a triangle with each point a different color.  Remember to
		// draw the points in counter-clockwise order.  That is the default
		// way of determining which is the front of a polygon.
		//        o (1)
		//       / \
		//      /   \
		// (2) o-----o (0)
		final Shape3D shape = new Shape3D();
		final TriangleArray tri = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		tri.setCoordinate(0, new Point3f(0.5f, 0.0f, 0.0f));
		tri.setCoordinate(1, new Point3f(0.0f, 0.5f, 0.0f));
		tri.setCoordinate(2, new Point3f(-0.5f, 0.0f, 0.0f));
		tri.setColor(0, new Color3f(1.0f, 0.0f, 0.0f));
		tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
		tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));

		// Because we're about to spin this triangle, be sure to draw
		// backfaces.  If we don't, the back side of the triangle is invisible.
		final Appearance ap = new Appearance();
		final PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);
		shape.setAppearance(ap);

		// Set up a simple RotationInterpolator
		final BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0);
		final TransformGroup tg = new TransformGroup();
		final Transform3D yAxis = new Transform3D();
		final Alpha rotationAlpha = new Alpha(-1, 4000);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		final RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, tg, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);

		shape.setGeometry(tri);
		tg.addChild(rotator);
		tg.addChild(shape);
		objRoot.addChild(tg);
		objRoot.compile();
		return objRoot;
	}

	public static void main(String[] args)
	{

		//load up the native dlls!		 
		new Java3dLinker2();

		new Fullscreen();

	}
}
