package tools3d.mixed3d2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;

import tools3d.mixed3d2d.hud.HUDElement;
import tools3d.mixed3d2d.overlay.swing.Panel3D;
import tools3d.utils.leafnode.Cube;

/**
 * TODO: a write up, hud and overlay are seperate
 * 
 * if the hud shape goes live draw to that all hud elements
 * if there is any overlay (panel3d) then draw them to J3dgrpahics2s
 * So the system swaps between the 2 as needed
 * 
 * the point being overlays are interactive and a loss of frame rate make little odds
 * whilst the keyboard and mouse are busy
 * 
 * to do something like a chat box it would be a hud piece until activated then swapped to a panel3d
 * 
 * @see getHudShapeRoot() for an example of use
 * 
 * also make note of the scaleform issue of getting th hud pixel mathced to screen.
 * check out the tex.setMagFilter(Texture.BASE_LEVEL_LINEAR); see if other produce better text
 * 
 * also continue to enhance by detecting screen size changes and re setting the hud texture to match
 * I suspect the dist in front of viewer stuff will be alter by FOV settings, so watch out for those
 * 
 * 
 * @author philip
 *
 */
public class Canvas3D2D extends Canvas3D
{

	public static int TEX_WIDTH = 1024;

	public static int TEX_HEIGHT = 1024;

	public static float SHAPE_WIDTH = 0.25f;

	public static float SHAPE_HEIGHT = 0.25f;

	public static float SHAPE_Z = -0.198f;

	private ArrayList<HUDElement> hudElements = new ArrayList<HUDElement>();

	private ArrayList<Panel3D> panel3ds = new ArrayList<Panel3D>();

	private BranchGroup fixedBG = new BranchGroup();

	private Shape3D hudShape = new Shape3D();

	private Appearance app = new Appearance();

	private BufferedImage bi = new BufferedImage(TEX_WIDTH, TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

	private ImageComponent2D ic2d;

	public Canvas3D2D(GraphicsConfiguration gc)
	{
		super(gc);

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

		app.setCapability(Appearance.ALLOW_TEXTURE_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		hudShape.setAppearance(app);

		hudShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

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
		//System.out.println("screenResized");
		if (getView() != null)
		{
			//System.out.println("screenResized width " + this.getWidth() + " height " + this.getHeight());

			double aspectRatio = (double) this.getWidth() / (double) this.getHeight();
			double fov = getView().getFieldOfView();
			double halfFov = fov / 2d;

			//System.out.println("aspectRatio " + aspectRatio);
			//System.out.println("fov radians " + getView().getFieldOfView());
			//System.out.println("fov deg " + ((getView().getFieldOfView() / Math.PI) * 180));

			// now fov is the center of a non right angle(Isosceles) with point touching user
			// use sohcahtoa on the 2 right tri making up isoscele
			double distFromEye = -SHAPE_Z; //adj
			//System.out.println("distFromEye " + distFromEye);
			double opp = distFromEye * Math.tan(halfFov);
			//System.out.println("opp " + opp);
			SHAPE_WIDTH = (float) (opp * 2f);
			//System.out.println("shapeWidth " + shapeWidth);
			SHAPE_HEIGHT = (float) (SHAPE_WIDTH / aspectRatio);
			//System.out.println("shapeHeight " + shapeHeight);

			hudShape.setGeometry(createGeometry(SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_Z));

			TEX_WIDTH = 1024;
			TEX_HEIGHT = (int) (TEX_WIDTH / aspectRatio);
			//System.out.println("TEX_WIDTH " + TEX_WIDTH);
			//System.out.println("TEX_HEIGHT " + TEX_HEIGHT);

			bi = new BufferedImage(TEX_WIDTH, TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

			Texture2D tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, TEX_WIDTH, TEX_HEIGHT);
			tex.setBoundaryModeS(Texture.WRAP);
			tex.setBoundaryModeT(Texture.WRAP);
			tex.setMagFilter(Texture.FASTEST);//Texture.BASE_LEVEL_LINEAR);//
			tex.setMinFilter(Texture.FASTEST);//Texture.BASE_LEVEL_LINEAR);//

			ic2d = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bi, true, true);
			ic2d.setCapability(ImageComponent.ALLOW_IMAGE_READ);
			ic2d.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);

			tex.setImage(0, ic2d);
			app.setTexture(tex);

		}
	}

	/**
	 * When the returned tree is live in the scene graph all hud lement output will go to it's 
	 * texture, and the behaviour will keep it updated. If this is not attached to the view platform then all
	 * hud element output will go to the (slower) overlay system.
	 * 
	 * This should be attached to teh viewing platform like so:
	 * viewingPlatform.getPlatformGeometry().addChild(canvas3D2D.getHudShapeRoot());
	 * 
	 * @return a tree wiht a hud shape and a behavior for updating the texture
	 */
	public BranchGroup getHudShapeRoot()
	{
		return fixedBG;
	}

	public void addElement(HUDElement element)
	{
		if (element != null && !hudElements.contains(element))
		{
			synchronized (hudElements)
			{
				hudElements.add(element);
			}
		}
	}

	public void removeElement(HUDElement element)
	{
		synchronized (hudElements)
		{
			hudElements.remove(element);
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

	// For reseting the texture binding in the pipelline (trust me)
	private static Shape3D trivialShape = new Cube(0.01f);

	public void postRender()
	{
		// we only draw if the hud is now in the scene live and hudelements exist or any panel3d exists
		if (!fixedBG.isLive())
		{
			// Oh my god. Long story short, don't touch this if doing overlays.
			// Longer version, if the last rendered texture on a canvas3d has a transformation
			// then calls to the J3DGraphics2D will inherit it. Easy way to ensure last texture is plain, render trival cube.
			getGraphicsContext3D().draw(trivialShape);

			J3DGraphics2D g = getGraphics2D();

			synchronized (hudElements)
			{
				for (HUDElement e : hudElements)
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

		//This method will only be called when we are attached to a scene graph, i.e. fixedBG.isLive()==true
		// so these hudelements won't be drawn as overlays
		Graphics2D g = bi.createGraphics();
		g.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		g.clearRect(0, 0, TEX_WIDTH, TEX_HEIGHT); //NOT fillRect doesn't work
		//g.drawRect(2, 2, TEX_WIDTH - 4, TEX_HEIGHT - 4);// to help place hud elements

		//ok I've got it, the hud sizes are for screen coords, but for hud shape
		// I've got a fixed width of 1024, so the draws need to account for that properly

		float hW = (float) TEX_WIDTH / (float) this.getWidth();
		float hH = (float) TEX_HEIGHT / (float) this.getHeight();
		//System.out.println("hW " + hW + " " + TEX_WIDTH + "/" + this.getWidth());
		//	System.out.println("hH " + hH + " " + TEX_HEIGHT + "/" + this.getHeight());

		synchronized (hudElements)
		{
			for (HUDElement e : hudElements)
			{
				if (e != null && e.isEnabled())
				{
					g.drawImage(e.getBufferedImage(), (int) (e.getAbsoluteX() * hW), (int) (e.getAbsoluteY() * hH), null);
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
		ic2d.set(bi);

	}

	private static QuadArray createGeometry(float rectWidth, float rectHeight, float z)
	{

		float hW = rectWidth / 2f;
		float hH = rectHeight / 2f;

		float[] verts1 =
		{ hW, -hH, z, hW, hH, z, -hW, hH, z, -hW, -hH, z };

		//-1 flip the y axis so yUp
		float[] texCoords =
		{ 0f, 1f, //
				0f, 0f,//
				-1f, 0f,//
				-1f, 1f };

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
