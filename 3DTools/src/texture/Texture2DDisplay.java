package texture;

import java.io.InputStream;

import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.IndexedTriangleArray;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.compressedtexture.CompressedTextureLoader;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
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
 * display a quad with compressed DDS or ETC2 texture, accepts only a ByteBuffer with the image in it
 * @author philip
 *
 */
public class Texture2DDisplay {

	// only one showing at a time , cause removeNotify breaks things when 2 glwindows are showing, but it shouldn't and I should fix it!
	public static Canvas3D canvas3D;
	public static void showImageInShape(String filename, InputStream inputStream) {
		if(canvas3D != null) {
			canvas3D.removeNotify();
			canvas3D.getGLWindow().destroy();
		}
				
		canvas3D = new Canvas3D();
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
		
		canvas3D.getGLWindow().setSize(800,800);

		SimpleUniverse su = new SimpleUniverse(canvas3D);
		su.addBranchGraph(createSceneGraph(filename, inputStream));
	}
	
	public static void showImageInShape(String filename, Texture tex) {
		if(canvas3D != null) {
			canvas3D.removeNotify();
			canvas3D.getGLWindow().destroy();
		}
				
		canvas3D = new Canvas3D();
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
		
		canvas3D.getGLWindow().setSize(800,800);

		SimpleUniverse su = new SimpleUniverse(canvas3D);
		su.addBranchGraph(createSceneGraph(filename, tex));
	}
	
	

	/**
	 * Builds a scenegraph for the application to render.
	 * @return the root level of the scenegraph
	 */
	private static BranchGroup createSceneGraph(String filename, InputStream inputStream) {
		final BranchGroup objRoot = new BranchGroup();
		
		Texture tex = CompressedTextureLoader.UNKNOWN.getTexture(filename, inputStream);
		
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
		ColoringAttributes ca = new ColoringAttributes(new Color3f(0.5f,0.5f,0.5f), ColoringAttributes.NICEST);
		ap.setColoringAttributes(ca);
		
		ap.setTexture(tex);
		
		shape.setAppearance(ap);
		
		TransformGroup tg1 = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.set(new Vector3f(0,0,-2));
		tg1.setTransform(t3d);
				
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0);
		TransformGroup tg2 = new TransformGroup();
		MouseRotate behavior = new MouseRotate(canvas3D);
		behavior.setTransformGroup(tg2);
		tg2.addChild(behavior);
		behavior.setSchedulingBounds(bounds);
		tg2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);		

		tg2.addChild(shape);
		tg1.addChild(tg2);
		objRoot.addChild(tg1);
		
		//tg2.addChild(new ColorCube());
	 
		return objRoot;
	}
	
	private static BranchGroup createSceneGraph(String filename, Texture tex) {
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
		ColoringAttributes ca = new ColoringAttributes(new Color3f(0.5f,0.5f,0.5f), ColoringAttributes.NICEST);
		ap.setColoringAttributes(ca);
		
		ap.setTexture(tex);
		
		shape.setAppearance(ap);
		
		TransformGroup tg1 = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.set(new Vector3f(0,0,-2));
		tg1.setTransform(t3d);
				
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0);
		TransformGroup tg2 = new TransformGroup();
		MouseRotate behavior = new MouseRotate(canvas3D);
		behavior.setTransformGroup(tg2);
		tg2.addChild(behavior);
		behavior.setSchedulingBounds(bounds);
		tg2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);		

		tg2.addChild(shape);
		tg1.addChild(tg2);
		objRoot.addChild(tg1);
		
		//tg2.addChild(new ColorCube());
	 
		return objRoot;
	}

}
