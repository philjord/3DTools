package tools3d.mixed3d2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
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

public class HudShape3D extends BranchGroup
{
	public static int SHAPE_TEX_WIDTH = 1024;

	public static int SHAPE_TEX_HEIGHT = 1024;

	public static float SHAPE_WIDTH = 0.25f;

	public static float SHAPE_HEIGHT = 0.25f;

	public static float SHAPE_Z = -0.198f;

	private Shape3D hudShape = new Shape3D();

	private Appearance hudShapeApp = new Appearance();

	private BufferedImage hudShapeBufferedImage = new BufferedImage(SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

	private ImageComponent2D hudShapeIc2d;

	private Canvas3D2D canvas;

	public HudShape3D(Canvas3D2D canvas)
	{
		this.canvas = canvas;
		TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
		transparencyAttributes.setTransparencyMode(TransparencyAttributes.NICEST);
		transparencyAttributes.setTransparency(0.0f);
		hudShapeApp.setTransparencyAttributes(transparencyAttributes);

		TextureAttributes textureAttributes = new TextureAttributes();
		textureAttributes.setTextureMode(TextureAttributes.REPLACE);
		hudShapeApp.setTextureAttributes(textureAttributes);

		Material m = new Material();
		m.setLightingEnable(false);

		hudShapeApp.setMaterial(m);

		hudShapeApp.setCapability(Appearance.ALLOW_TEXTURE_READ);
		hudShapeApp.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		hudShape.setAppearance(hudShapeApp);

		hudShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

		addChild(hudShape);

		UpdateHudTextureBehavior hudTextureBehave = new UpdateHudTextureBehavior();
		addChild(hudTextureBehave);
		hudTextureBehave.setEnable(true);

		canvas.addComponentListener(new ComponentListener()
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

	public void screenResized()
	{
		//System.out.println("screenResized");
		if (canvas.getView() != null)
		{
			//System.out.println("screenResized width " + this.getWidth() + " height " + this.getHeight());

			double aspectRatio = (double) canvas.getWidth() / (double) canvas.getHeight();
			double fov = canvas.getView().getFieldOfView();
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

			SHAPE_TEX_WIDTH = 1024;
			SHAPE_TEX_HEIGHT = (int) (SHAPE_TEX_WIDTH / aspectRatio);
			//System.out.println("TEX_WIDTH " + TEX_WIDTH);
			//System.out.println("TEX_HEIGHT " + TEX_HEIGHT);

			hudShapeBufferedImage = new BufferedImage(SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

			Texture2D tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT);
			tex.setBoundaryModeS(Texture.WRAP);
			tex.setBoundaryModeT(Texture.WRAP);
			tex.setMagFilter(Texture.FASTEST);//Texture.BASE_LEVEL_LINEAR);//
			tex.setMinFilter(Texture.FASTEST);//Texture.BASE_LEVEL_LINEAR);//

			hudShapeIc2d = new ImageComponent2D(ImageComponent.FORMAT_RGBA, hudShapeBufferedImage, true, true);
			hudShapeIc2d.setCapability(ImageComponent.ALLOW_IMAGE_READ);
			hudShapeIc2d.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);

			tex.setImage(0, hudShapeIc2d);
			hudShapeApp.setTexture(tex);

		}
	}

	public void updateHudShapeTexture()
	{
		//This method will only be called when we are attached to a scene graph, i.e. fixedBG.isLive()==true
		// so these hudelements won't be drawn as overlays
		Graphics2D g = hudShapeBufferedImage.createGraphics();
		g.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));// for clear REct to work

		//TODO: I'd be way better off clearing the individual hud elements littel squares worth
		//g.clearRect(0, 0, SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT); //NOT fillRect doesn't work

		// Enable to help place hud elements
		//g.drawRect(2, 2, TEX_WIDTH - 4, TEX_HEIGHT - 4);

		//ok I've got it, the hud sizes are for screen coords, but for hud shape
		// I've got a fixed width of 1024, so the draws need to account for that properly

		float hW = (float) SHAPE_TEX_WIDTH / (float) canvas.getWidth();
		float hH = (float) SHAPE_TEX_HEIGHT / (float) canvas.getHeight();
		//System.out.println("hW " + hW + " " + TEX_WIDTH + "/" + this.getWidth());
		//	System.out.println("hH " + hH + " " + TEX_HEIGHT + "/" + this.getHeight());

		//do all clears first in case of overlapping elements
		synchronized (canvas.getHudElements())
		{
			for (HUDElement e : canvas.getHudElements())
			{
				if (e != null && e.isEnabled())
				{
					g.clearRect((int) (e.getAbsoluteX() * hW), (int) (e.getAbsoluteY() * hH), e.getWidth(), e.getHeight()); //NOT fillRect doesn't work
				}
			}
		}

		//TODO: I should NEVER draw this on a hud shape I should flip to post render
		synchronized (canvas.getPanel3ds())
		{
			for (Panel3D p : canvas.getPanel3ds())
			{
				if (p != null && p.isEnabled())
				{
					g.clearRect(p.getX(), p.getY(), p.getWidth(), p.getHeight()); //NOT fillRect doesn't work
				}
			}
		}

		// now draw
		synchronized (canvas.getHudElements())
		{
			for (HUDElement e : canvas.getHudElements())
			{
				if (e != null && e.isEnabled())
				{
					//TODO: draw at scaled width and height?
					g.drawImage(e.getBufferedImage(), (int) (e.getAbsoluteX() * hW), (int) (e.getAbsoluteY() * hH), null);
				}
			}
		}

		//TODO: I should NEVER draw this on a hud shape I should flip to post render
		synchronized (canvas.getPanel3ds())
		{
			for (Panel3D p : canvas.getPanel3ds())
			{
				if (p != null && p.isEnabled())
				{
					//TODO: draw at scaled width and height?
					g.drawImage(p.getBufferedImage(), (int) (p.getX() * hW), (int) (p.getY() * hH), null);
				}
			}
		}
		hudShapeIc2d.set(hudShapeBufferedImage);

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
