package texture;

import java.io.InputStream;
import java.util.ArrayDeque;

import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.IndexedTriangleArray;
import org.jogamp.java3d.JoglesPipeline;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.compressedtexture.CompressedTextureLoader;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseWheelZoom;
import org.jogamp.java3d.utils.shader.SimpleShaderAppearance;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.TexCoord2f;
import org.jogamp.vecmath.Vector3f;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;


/**
 * very simple texture visualizer, throws up glwindow in java3d with esc key as the exit and a basic mouse rotate, to
 * display a quad with compressed DDS or ETC2 texture
 * @author philip
 *
 */
public class Texture2DDisplay {

	private static final int			MAX_CANVAS3D_VISIBLE	= 3;
	public static ArrayDeque<Canvas3D>	canvas3Ds				= new ArrayDeque<Canvas3D>();	// fifo list

	public static void showImageInShape(String filename, InputStream inputStream) {
		// FIXME: the holding of context may add speed but it causes the pipeline to not call releaseContext on each update pass
		// on the GLWindow Surface so the GLWindow setVisible(false) won't remove it
		// and it can't be destroyed, to fix this issue at the least stopping renderer should force a releaseCtx on the pipeline		
		JoglesPipeline.LATE_RELEASE_CONTEXT = false;

		Canvas3D canvas3D = sortOutCanvas();
		SimpleUniverse su = new SimpleUniverse(canvas3D);
		su.getViewer().getView().setBackClipDistance(5000);
		su.addBranchGraph(createSceneGraph(filename, inputStream, canvas3D));
	}

	public static void showImageInShape(String filename, Texture tex) {

		// FIXME: the holding of context may add speed but it causes the pipeline to not call releaseContext on each update pass
		// on the GLWindow Surface so the GLWindow setVisible(false) won't remove it
		// and it can't be destroyed, to fix this issue at the least stopping renderer should force a releaseCtx on the pipeline		
		JoglesPipeline.LATE_RELEASE_CONTEXT = false;

		Canvas3D canvas3D = sortOutCanvas();
		SimpleUniverse su = new SimpleUniverse(canvas3D);
		su.getViewer().getView().setBackClipDistance(5000);
		su.addBranchGraph(createSceneGraph(filename, tex, canvas3D));

	}

	private static Canvas3D sortOutCanvas() {

		while (canvas3Ds.size() > MAX_CANVAS3D_VISIBLE - 1) {
			Canvas3D c = canvas3Ds.removeFirst();
			c.removeNotify();
			c.getGLWindow().destroy();
		}

		Canvas3D canvas3D = new Canvas3D();
		canvas3Ds.addLast(canvas3D);

		canvas3D.addNotify();

		canvas3D.getGLWindow().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				final int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) || ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					canvas3D.removeNotify();
				}
			}
		});

		canvas3D.getGLWindow().setSize(800, 800);
		CompressedTextureLoader.setAnisotropicFilterDegree(8);
		return canvas3D;
	}

	/**
	 * Builds a scenegraph for the application to render.
	 * @param canvas3d
	 * @return the root level of the scenegraph
	 */
	private static BranchGroup createSceneGraph(String filename, InputStream inputStream, Canvas3D canvas3d) {
		Texture tex = CompressedTextureLoader.UNKNOWN.getTexture(filename, inputStream);
		return createSceneGraph(filename, tex, canvas3d);
	}

	private static BranchGroup createSceneGraph(String filename, Texture tex, Canvas3D canvas3d) {
		final BranchGroup objRoot = new BranchGroup();

		double w = tex.getWidth();
		double h = tex.getHeight();
		float sw = (float)(h > w ? 0.5f : 0.5f * (w / h));
		float sh = (float)(w > h ? 0.5f : 0.5f * (h / w));

		Shape3D shape = new Shape3D();
		IndexedTriangleArray tri = new IndexedTriangleArray(4,
				GeometryArray.USE_COORD_INDEX_ONLY | GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2, 6);
		tri.setCoordinate(0, new Point3f(-sw, -sh, 0.0f));
		tri.setCoordinate(1, new Point3f(sw, -sh, 0.0f));
		tri.setCoordinate(2, new Point3f(sw, sh, 0.0f));
		tri.setCoordinate(3, new Point3f(-sw, sh, 0.0f));

		tri.setTextureCoordinate(0, 0, new TexCoord2f(0.0f, 1.0f));
		tri.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		tri.setTextureCoordinate(0, 2, new TexCoord2f(1.0f, 0.0f));
		tri.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));

		tri.setCoordinateIndex(0, 0);
		tri.setCoordinateIndex(1, 1);
		tri.setCoordinateIndex(2, 2);
		tri.setCoordinateIndex(3, 0);
		tri.setCoordinateIndex(4, 3);
		tri.setCoordinateIndex(5, 2);

		shape.setGeometry(tri);

		// Because we're about to spin this triangle, be sure to draw
		// backfaces.  If we don't, the back side of the triangle is invisible.
		SimpleShaderAppearance ap = new SimpleShaderAppearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);
		ColoringAttributes ca = new ColoringAttributes(new Color3f(0.5f, 0.5f, 0.5f), ColoringAttributes.NICEST);
		ap.setColoringAttributes(ca);

		ap.setTexture(tex);

		shape.setAppearance(ap);

		TransformGroup tg1 = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.set(new Vector3f(0, 0, -2));
		tg1.setTransform(t3d);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50000.0);

		TransformGroup tg2 = new TransformGroup();
		tg2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		MouseWheelZoom mouseWheelZoom = new MouseWheelZoom(canvas3d);
		mouseWheelZoom.setTransformGroup(tg2);
		tg2.addChild(mouseWheelZoom);
		mouseWheelZoom.setSchedulingBounds(bounds);

		TransformGroup tg3 = new TransformGroup();
		tg3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		MouseRotate mouseRotate = new MouseRotate(canvas3d);
		mouseRotate.setTransformGroup(tg3);
		tg3.addChild(mouseRotate);
		mouseRotate.setSchedulingBounds(bounds);

		tg3.addChild(shape);
		tg2.addChild(tg3);
		tg1.addChild(tg2);
		objRoot.addChild(tg1);

		//tg2.addChild(new ColorCube());

		return objRoot;
	}

}
