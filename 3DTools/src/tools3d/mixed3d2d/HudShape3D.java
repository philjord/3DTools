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
import javax.media.j3d.ImageComponent2D.Updater;
import javax.media.j3d.J3DBuffer;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;

import tools3d.mixed3d2d.hud.HUDElement;
import tools3d.mixed3d2d.overlay.swing.Panel3D;
import tools3d.utils.SimpleShaderAppearance;
import tools3d.utils.Utils3D;

/**
 * @author philip
 *
 */

public class HudShape3D extends BranchGroup implements Updater, ComponentListener
{
	public static int SHAPE_TEX_WIDTH = 1024;

	public static int SHAPE_TEX_HEIGHT = 1024;

	public static float SHAPE_WIDTH = 0.25f;

	public static float SHAPE_HEIGHT = 0.25f;

	public static float SHAPE_Z = -0.198f;

	private Shape3D hudShape = new Shape3D();

	private Appearance app;

	private Texture2D tex;

	private ImageComponent2D hudShapeIc2d;

	private Canvas3D2D canvas;

	private boolean finalClearRequired = false;

 

	public HudShape3D(Canvas3D2D canvas)
	{
		this.canvas = canvas;

		this.setCapability(BranchGroup.ALLOW_DETACH);

		app = new SimpleShaderAppearance(true);

		app.setCapability(Appearance.ALLOW_TEXTURE_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

		TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
		transparencyAttributes.setTransparencyMode(TransparencyAttributes.NICEST);
		transparencyAttributes.setTransparency(0.0f);
		app.setTransparencyAttributes(transparencyAttributes);

		//keep stencil gear in check
		RenderingAttributes ra = new RenderingAttributes();
		ra.setDepthBufferEnable(false);
		app.setRenderingAttributes(ra);

		hudShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		hudShape.setAppearance(app);
		addChild(hudShape);

		UpdateHudTextureBehavior hudTextureBehave = new UpdateHudTextureBehavior();
		addChild(hudTextureBehave);
		hudTextureBehave.setEnable(true);

		canvas.addComponentListener(this);
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
			//System.out.println("shapeWidth " + SHAPE_WIDTH);
			SHAPE_HEIGHT = (float) (SHAPE_WIDTH / aspectRatio);
			//System.out.println("shapeHeight " + SHAPE_HEIGHT);

			hudShape.setGeometry(createGeometry(SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_Z));

			SHAPE_TEX_WIDTH = 1024;
			SHAPE_TEX_HEIGHT = (int) (SHAPE_TEX_WIDTH / aspectRatio);
			//System.out.println("SHAPE_TEX_WIDTH " + SHAPE_TEX_WIDTH);
			//System.out.println("SHAPE_TEX_HEIGHT " + SHAPE_TEX_HEIGHT);

			BufferedImage hudShapeBufferedImage = new BufferedImage(SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

			tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT);
			tex.setBoundaryModeS(Texture.WRAP);
			tex.setBoundaryModeT(Texture.WRAP);
			tex.setMagFilter(Texture.LINEAR_SHARPEN_RGB);//Texture.BASE_LEVEL_LINEAR);
			tex.setMinFilter(Texture.BASE_LEVEL_LINEAR);

			hudShapeIc2d = new ImageComponent2D(ImageComponent.FORMAT_RGBA, hudShapeBufferedImage, true, true);
			hudShapeIc2d.setCapability(ImageComponent.ALLOW_IMAGE_READ);
			hudShapeIc2d.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);

			tex.setImage(0, hudShapeIc2d);
			app.setTexture(tex);

		}
	}

	public void updateHudShapeTexture()
	{
		if (hudShapeIc2d != null)
			hudShapeIc2d.updateData(this, 0, 0, hudShapeIc2d.getWidth(), hudShapeIc2d.getHeight());
	}

	@Override
	public void updateData(ImageComponent2D imageComponent, int x, int y, int width, int height)
	{
		if (!canvas.hasEnabledPanel3D())
		{
			//This method will only be called when we are attached to a scene graph, i.e. this.isLive()==true
			// so these hudelements won't be drawn as overlays
			Graphics2D g = hudShapeIc2d.getImage().createGraphics();
			g.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));// for clear Rect to work

			//I'm  way better off clearing the individual hud elements little squares worth
			//g.clearRect(0, 0, SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT); //NOT fillRect doesn't work

			//ok I've got it, the hud sizes are for screen coords, but for hud shape texture
			// I've got a fixed width of 1024, so the draws need to account for that properly

			float hW = (float) SHAPE_TEX_WIDTH / (float) canvas.getWidth();
			float hH = (float) SHAPE_TEX_HEIGHT / (float) canvas.getHeight();
			//System.out.println("hW " + hW + " = " + SHAPE_TEX_WIDTH + "/" + canvas.getWidth());
			//	System.out.println("hH " + hH + " = " + SHAPE_TEX_HEIGHT + "/" + canvas.getHeight());

			//final clear for all previously removed hud elements
			synchronized (canvas.getRemovedHudElements())
			{
				for (HUDElement e : canvas.getRemovedHudElements())
				{
					if (e != null)
					{
						g.clearRect((int) (e.getAbsoluteX() * hW), (int) (e.getAbsoluteY() * hH), e.getWidth(), e.getHeight()); //NOT fillRect doesn't work
					}
				}
			}
			//final clear for all previously removed panel3ds
			synchronized (canvas.getRemovedPanel3ds())
			{
				for (Panel3D p : canvas.getRemovedPanel3ds())
				{
					if (p != null)
					{
						g.clearRect(p.getX(), p.getY(), p.getWidth(), p.getHeight()); //NOTE fillRect doesn't work
					}
				}
			}

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

			//see below never actually called
			synchronized (canvas.getPanel3ds())
			{
				for (Panel3D p : canvas.getPanel3ds())
				{
					if (p != null && p.isEnabled() && p.isUpdated())
					{
						g.clearRect(p.getX(), p.getY(), p.getWidth(), p.getHeight()); //NOTE fillRect doesn't work
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

			//TODO: Note this is never in fact called, because of teh check above
			// I can't put nice Panel3D in the hud shape because teh scaling (hW and hH) make it
			// look too terrible
			synchronized (canvas.getPanel3ds())
			{
				for (Panel3D p : canvas.getPanel3ds())
				{
					if (p != null && p.isEnabled())
					{
						g.drawImage(p.getBufferedImage(), (int) (p.getX() * hW), (int) (p.getY() * hH), //
								(int) (p.getWidth() * hW), (int) (p.getHeight() * hH), null);
					}
				}
			}

			// in case we flip to post render system due to panel3Ds arriving
			finalClearRequired = true;

			// Enable to help place hud elements
			//g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
			//g.drawRect(3, 3, SHAPE_TEX_WIDTH - 6, SHAPE_TEX_HEIGHT - 6);
			//g.drawRect(4, 4, SHAPE_TEX_WIDTH - 8, SHAPE_TEX_HEIGHT - 8);

			// must reset so the image displays, before TextureRetained mip level fix this wasn't needed
			app.setTexture(tex);
		}
		else if (finalClearRequired)
		{
			Graphics2D g = hudShapeIc2d.getImage().createGraphics();
			g.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));// for clear Rect to work
			g.clearRect(0, 0, SHAPE_TEX_WIDTH, SHAPE_TEX_HEIGHT); //NOTE fillRect doesn't work
			finalClearRequired = false;
			app.setTexture(tex);
		}

	}

	private static GeometryArray createGeometry(float rectWidth, float rectHeight, float z)
	{
		float hW = rectWidth / 2f;
		float hH = rectHeight / 2f;

		float[] verts1 = { hW, -hH, z, //1
				hW, hH, z, //2
				-hW, hH, z, //3
				hW, -hH, z, //1
				-hW, hH, z, //3
				-hW, -hH, z };//4

		//-1 flip the y axis so yUp
		float[] texCoords = { 0f, 1f, //
				0f, 0f, //
				-1f, 0f, //
				 0f, 1f, //
				 -1f, 0f, //
				-1f, 1f };

		TriangleArray rect = new TriangleArray(6,
				GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);
		rect.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(verts1)));
		rect.setTexCoordRefBuffer(0, new J3DBuffer(Utils3D.makeFloatBuffer(texCoords)));

		return rect;
	}

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

		@SuppressWarnings({ "rawtypes" })
		public void processStimulus(Enumeration critiria)
		{
			updateHudShapeTexture();
			wakeupOn(wakeupCriterion);
		}

	}

}
