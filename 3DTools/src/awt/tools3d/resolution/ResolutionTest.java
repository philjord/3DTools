package awt.tools3d.resolution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 

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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.sun.j3d.utils.universe.SimpleUniverse;

import tools.ddstexture.DDSTextureLoader;

public final class ResolutionTest
{
	private final JFrame win;

	private Canvas3D canvas3D;

	private ResolutionTest()
	{
		//note win construction MUST occur before asking for graphics environment etc.
		win = new JFrame("Fullscreen Example");
		win.setVisible(true);
		win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JMenuBar menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem setGraphicsMenuItem = new JMenuItem("Set Graphics");
		fileMenu.add(setGraphicsMenuItem);
		menubar.add(fileMenu);
		win.setJMenuBar(menubar);

		canvas3D = new Canvas3D();
		//win.add(canvas3D);
		canvas3D.addNotify();

		GraphicsSettings gs = ScreenResolution.organiseResolution(null, win, false, true, true);

		canvas3D.getGLWindow().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				final int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) || ((keyCode == KeyEvent.VK_C) && e.isControlDown()))
				{
					System.exit(0);
				}
			}
		});

		SimpleUniverse su = new SimpleUniverse(canvas3D);
		su.getViewingPlatform().setNominalViewingTransform(); // back away from object a little
		su.addBranchGraph(createSceneGraph());

		canvas3D.getView().setSceneAntialiasingEnable(gs.isAaRequired());
		DDSTextureLoader.setAnisotropicFilterDegree(gs.getAnisotropicFilterDegree());

		// don't bother super fast for now
		//ConsoleFPSCounter fps = new ConsoleFPSCounter();
		//su.addBranchGraph(fps.getBehaviorBranchGroup());

		setGraphicsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GraphicsSettings gs = ScreenResolution.organiseResolution(null, win, false, false, false);

				canvas3D.getView().setSceneAntialiasingEnable(gs.isAaRequired());
				DDSTextureLoader.setAnisotropicFilterDegree(gs.getAnisotropicFilterDegree());

			}
		});

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
		Shape3D shape = new Shape3D();
		TriangleArray tri = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		tri.setCoordinate(0, new Point3f(0.5f, 0.0f, 0.0f));
		tri.setCoordinate(1, new Point3f(0.0f, 0.5f, 0.0f));
		tri.setCoordinate(2, new Point3f(-0.5f, 0.0f, 0.0f));
		tri.setColor(0, new Color3f(1.0f, 0.0f, 0.0f));
		tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
		tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));

		// Because we're about to spin this triangle, be sure to draw
		// backfaces.  If we don't, the back side of the triangle is invisible.
		Appearance ap = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);
		shape.setAppearance(ap);

		// Set up a simple RotationInterpolator
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0);
		TransformGroup tg = new TransformGroup();
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, 4000);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, tg, yAxis, 0.0f, (float) Math.PI * 2.0f);
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
		new ResolutionTest();
	}
}
