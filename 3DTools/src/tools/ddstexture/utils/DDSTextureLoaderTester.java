package tools.ddstexture.utils;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

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
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.sun.j3d.utils.universe.SimpleUniverse;

import awt.tools3d.resolution.GraphicsSettings;
import awt.tools3d.resolution.ScreenResolution;
import tools.ddstexture.DDSImage;
import tools.ddstexture.DDSTextureLoader;
import tools.swing.DetailsFileChooser;

/**
 * dds image loading tester, note this use teh decompress to buffered image util system
 * not the jogl compressed call
 * @author philip
 *
 */
public class DDSTextureLoaderTester
{
	private static Preferences prefs;

	public static void main(String[] args)
	{
		prefs = Preferences.userNodeForPackage(DDSTextureLoaderTester.class);

		DetailsFileChooser dfc = new DetailsFileChooser(prefs.get("DDSToTexture", ""), new DetailsFileChooser.Listener() {
			@Override
			public void directorySelected(File dir)
			{
				prefs.put("DDSToTexture", dir.getAbsolutePath());
				System.out.println("Selected dir: " + dir);
				processDir(dir);
			}

			@Override
			public void fileSelected(File file)
			{
				prefs.put("DDSToTexture", file.getAbsolutePath());
				System.out.println("Selected file: " + file);
				showImage(file, 15000);
			}
		});

		dfc.setFileFilter(new FileNameExtensionFilter("dds", "dds"));
	}

	private static void processDir(File dir)
	{
		System.out.println("Processing directory " + dir);
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++)
		{
			try
			{
				if (fs[i].isFile() && fs[i].getName().endsWith(".dds"))
				{
					System.out.println("\tFile: " + fs[i]);
					showImage(fs[i], 5000);

					//pause between each show to gve it a chance to show
					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException e)
					{
					}
				}
				else if (fs[i].isDirectory())
				{
					processDir(fs[i]);
				}

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static void showImage(File file, long stayTime)
	{
		String filename = file.getAbsolutePath();
		try
		{
			showImage(filename, new FileInputStream(file), stayTime);
			//showImageInShape(filename, new FileInputStream(file));
		}
		catch (IOException e)
		{
			System.out.println("" + DDSTextureLoaderTester.class + " had a  IO problem with " + filename + " : " + e.getMessage());
		}

	}

	public static void showImage(String filename, InputStream inputStream, final long stayTime)
	{
		final JFrame f = new JFrame();
		f.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		f.getContentPane().setBackground(new Color(255, 0, 255));

		DDSImage ddsImage;
		try
		{
			ddsImage = DDSImage.read(DDSTextureLoader.toByteBuffer(inputStream));
			ddsImage.debugPrint();
		}
		catch (IOException e)
		{
			System.out.println("" + DDSTextureLoaderTester.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return;
		}

		DDSImage.ImageInfo[] infos = ddsImage.getAllMipMaps();

		int height = -1;
		int width = 0;
		for (int i = 0; i < infos.length; i++)
		{
			BufferedImage image = new DDSDecompressor(ddsImage, i, filename).convertImage();
			if (image != null)
			{
				if (height == -1)// height of first big one only
					height = image.getHeight();
				width += image.getWidth();

				//Flip because of the desire to have yUp on
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -image.getHeight());
				BufferedImage mine = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
				((Graphics2D) mine.getGraphics()).drawImage(image, tx, null);

				ImageIcon icon = new ImageIcon(mine);
				f.getContentPane().add(new JLabel(icon));
			}
		}

		ddsImage.close();
		f.setTitle(filename);
		f.setVisible(true);
		f.setSize(width + f.getInsets().left + f.getInsets().right, height + f.getInsets().top + f.getInsets().bottom);

		Thread t = new Thread() {
			public void run()
			{
				try
				{
					Thread.sleep(stayTime);
				}
				catch (InterruptedException e)
				{
				}
				f.dispose();
			}
		};
		t.start();
	}

	//Method to show teh DXT texture method in a 3d scene
	public static void showImageInShape(String filename, InputStream inputStream)
	{
		//note win construction MUST occur beofre asking for graphics environment etc.
		JFrame win = new JFrame("Fullscreen Example");
		win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Canvas3D canvas3D = new Canvas3D();
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
		su.addBranchGraph(createSceneGraph(filename, inputStream));

		canvas3D.getView().setSceneAntialiasingEnable(gs.isAaRequired());
		DDSTextureLoader.setAnisotropicFilterDegree(gs.getAnisotropicFilterDegree());

		// don't bother super fast for now
		//ConsoleFPSCounter fps = new ConsoleFPSCounter();
		//su.addBranchGraph(fps.getBehaviorBranchGroup());

	}

	/**
	 * Builds a scenegraph for the application to render.
	 * @return the root level of the scenegraph
	 */
	private static BranchGroup createSceneGraph(String filename, InputStream inputStream)
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
		TriangleArray tri = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.TEXTURE_COORDINATE_2);
		tri.setCoordinate(0, new Point3f(0.5f, 0.0f, 0.0f));
		tri.setCoordinate(1, new Point3f(0.0f, 0.5f, 0.0f));
		tri.setCoordinate(2, new Point3f(-0.5f, 0.0f, 0.0f));
		tri.setColor(0, new Color3f(1.0f, 0.0f, 0.0f));
		tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
		tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));

		tri.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.5f));
		tri.setTextureCoordinate(0, 1, new TexCoord2f(0.0f, 0.0f));
		tri.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));

		// Because we're about to spin this triangle, be sure to draw
		// backfaces.  If we don't, the back side of the triangle is invisible.
		Appearance ap = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);
		shape.setAppearance(ap);

		ap.setTexture(DDSTextureLoader.getTexture(filename, inputStream));

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

}
