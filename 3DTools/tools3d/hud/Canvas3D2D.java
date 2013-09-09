package tools3d.hud;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;

import tools3d.swingoverlay.Panel3D;
import tools3d.utils.leafnode.Cube;

public class Canvas3D2D extends Canvas3D
{
	public static boolean ENABLE_POSTRENDER_HUD = true;

	public static boolean ENABLE_TEXTURE_HUD = false;

	public static int TEX_WIDTH = 1024;

	public static int TEX_HEIGHT = 1024;

	public static float SHAPE_WIDTH = 0.25f;

	public static float SHAPE_HEIGHT = 0.25f;

	public static float SHAPE_Z = -0.198f;

	private ArrayList<HUDElement> elements = new ArrayList<HUDElement>();

	private ArrayList<Panel3D> panel3ds = new ArrayList<Panel3D>();

	private BranchGroup fixedBG = new BranchGroup();

	private BufferedImage bi = new BufferedImage(TEX_WIDTH, TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

	// double buffer to allow easy flip to keep yUp =true
	private BufferedImage bi2 = new BufferedImage(TEX_WIDTH, TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

	private ImageComponent2D ic2d;

	private AffineTransformOp op;

	public Canvas3D2D(GraphicsConfiguration arg0)
	{
		super(arg0);

		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -TEX_HEIGHT);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		Shape3D hudShape = new Shape3D();

		Appearance app = new Appearance();

		PolygonAttributes polyAttribs = new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0);
		app.setPolygonAttributes(polyAttribs);

		TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
		transparencyAttributes.setTransparencyMode(TransparencyAttributes.NICEST);
		transparencyAttributes.setTransparency(0.0f);
		app.setTransparencyAttributes(transparencyAttributes);

		TextureAttributes textureAttributes = new TextureAttributes();
		textureAttributes.setTextureMode(TextureAttributes.REPLACE);
		app.setTextureAttributes(textureAttributes);

		Material m = new Material();
		m.setLightingEnable(false);
		app.setMaterial(m);

		Texture2D tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, TEX_WIDTH, TEX_HEIGHT);
		tex.setBoundaryModeS(Texture.WRAP);
		tex.setBoundaryModeT(Texture.WRAP);
		tex.setMagFilter(Texture.BASE_LEVEL_LINEAR);

		ic2d = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bi, true, true);
		ic2d.setCapability(ImageComponent.ALLOW_IMAGE_READ);
		ic2d.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);

		tex.setImage(0, ic2d);
		app.setTexture(tex);

		app.setCapability(Appearance.ALLOW_TEXTURE_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

		tex.setCapability(Texture.ALLOW_IMAGE_WRITE);
		tex.setCapability(Texture.ALLOW_IMAGE_READ);

		hudShape.setGeometry(createGeometry(0.25f, 0.25f, SHAPE_Z));
		hudShape.setAppearance(app);

		fixedBG.addChild(hudShape);

		UpdateHudTextureBehavior hudTextureBehave = new UpdateHudTextureBehavior();
		fixedBG.addChild(hudTextureBehave);
		hudTextureBehave.setEnable(true);

		this.addComponentListener(new ComponentListener()
		{

			@Override
			public void componentResized(ComponentEvent e)
			{
				screenResized();
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				screenResized();
			}

			@Override
			public void componentHidden(ComponentEvent e)
			{
			}
		});
	}

	private void screenResized()
	{
		//this.setSize(1024, 1024);
		//System.out.println("screenResized and forced to 1024 by 1024 in Canvas2D3D, until I get the ratio perfect");
		System.out.println("width " + this.getWidth());
		System.out.println("height " + this.getHeight());
	}

	/**
	 * Calling this mehtod will turn on the texture Hud and turn OFF the overlay hud, be careful
	 * @return
	 */
	public BranchGroup getHudShapeRoot()
	{
		ENABLE_POSTRENDER_HUD = false;
		ENABLE_TEXTURE_HUD = true;
		return fixedBG;
	}

	public Canvas3D2D(GraphicsConfiguration arg0, boolean arg1)
	{
		super(arg0, arg1);
	}

	public void addElement(HUDElement element)
	{
		if (element != null && !elements.contains(element))
		{
			synchronized (elements)
			{
				elements.add(element);
			}
		}
	}

	public void removeElement(HUDElement element)
	{
		synchronized (elements)
		{
			elements.remove(element);
		}
	}

	public void addPanel3D(Panel3D panel3D)
	{
		if (panel3D != null && !panel3ds.contains(panel3D))
		{
			synchronized (panel3ds)
			{
				panel3ds.add(panel3D);
			}
		}
	}

	public void removePanel3D(Panel3D panel3D)
	{
		synchronized (panel3ds)
		{
			panel3ds.remove(panel3D);
		}
	}

	// For reseting teh texture binding in the pipelline (trust me)
	private static Shape3D trivialShape = new Cube(0.01f);

	public void postRender()
	{
		if (ENABLE_POSTRENDER_HUD)
		{
			// Oh my god. Long story short, don't touch this if doing overlays.
			// Longer version, if the last rendered texture on a canvas3d has a transformation
			// then calls to the J3DGraphics2D will inherit it. Easy way to ensure last texture is plain, render trival cube.
			getGraphicsContext3D().draw(trivialShape);

			J3DGraphics2D g = getGraphics2D();

			synchronized (elements)
			{
				for (HUDElement e : elements)
				{

					if (e != null && e.isEnabled())
					{
						g.drawImage(e.getBufferedImage(), e.getAbsoluteX(), e.getAbsoluteY(), null);
					}
				}
			}

			synchronized (panel3ds)
			{
				for (Panel3D p : panel3ds)
				{
					if (p != null && p.isEnabled())
					{
						g.drawImage(p.getBufferedImage(), p.getX(), p.getY(), null);
					}
				}
			}

			g.flush(false);
		}

	}

	public void updateHudShapeTexture()
	{
		if (ENABLE_TEXTURE_HUD)
		{
			Graphics2D g2 = bi2.createGraphics();
			g2.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
			g2.clearRect(0, 0, 1024, 1024);

			g2.drawRect(0, 0, 1023, 1023);// to allow sexy placement

			synchronized (elements)
			{
				for (HUDElement e : elements)
				{
					if (e != null && e.isEnabled())
					{
						g2.drawImage(e.getBufferedImage(), e.getAbsoluteX(), e.getAbsoluteY(), null);
					}
				}
			}

			synchronized (panel3ds)
			{
				for (Panel3D p : panel3ds)
				{
					if (p != null && p.isEnabled())
					{
						g2.drawImage(p.getBufferedImage(), p.getX(), p.getY(), null);
					}
				}
			}

			//flip it now to allow the yup flag			
			bi = op.filter(bi2, bi);
			ic2d.set(bi);
		}
	}

	private static QuadArray createGeometry(float rectWidth, float rectHeight, float z)
	{
		float hW = rectWidth / 2f;
		float hH = rectHeight / 2f;

		float[] verts1 =
		{ hW, -hH, z, hW, hH, z, -hW, hH, z, -hW, -hH, z };
		float[] texCoords =
		{ 0f, -1f, //
				0f, 0f,//
				-1f, 0f,//
				-1f, -1f };

		QuadArray rect = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		rect.setCoordinates(0, verts1);
		rect.setTextureCoordinates(0, 0, texCoords);

		return rect;
	}

	class UpdateHudTextureBehavior extends Behavior
	{
		private WakeupOnElapsedFrames wakeupCriterion = new WakeupOnElapsedFrames(2);

		//private WakeupOnElapsedTime wakeupCriterion = new WakeupOnElapsedTime(100);

		public void initialize()
		{
			// see also UpdateLastPerFrameBehavior
			setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
			wakeupOn(wakeupCriterion);
		}

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		public void processStimulus(Enumeration critiria)
		{
			updateHudShapeTexture();
			wakeupOn(wakeupCriterion);
		}

	}
}
