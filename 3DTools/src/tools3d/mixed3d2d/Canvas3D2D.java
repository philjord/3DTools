package tools3d.mixed3d2d;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
//<AND>import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.Shape3D;

import tools3d.mixed3d2d.hud.HUDElement;
import tools3d.mixed3d2d.overlay.swing.Panel3D;
import tools3d.utils.leafnode.Cube;

/**
 * TODO: a write up, hud and overlay are seperate
 * 
 * if the hud shape goes live draw to that all hud elements
 * if there is any overlay (panel3d) then draw them to J3dgrpahics2s
 * So the system swaps between the 2 as needed, right now it doesn't a live hud always gets draws
 * 
 * the point being overlays are interactive and a loss of frame rate make little odds
 * whilst the keyboard and mouse are busy
 * 
 * to do something like a chat box it would be a hud piece until activated then swapped to a panel3d
 * 
 * @see getHudShapeRoot() for an example of use
 * 
 * also make note of the scaleform issue of getting the hud pixels matched to screen.
 * check out the tex.setMagFilter(Texture.BASE_LEVEL_LINEAR); see if other produce better text
 * 
 * also continue to enhance by detecting screen size changes and re setting the hud texture to match
 * I suspect the dist in front of viewer stuff will be alter by FOV settings, so watch out for those
 * 

 * 
 * For HMD with teh texture id issue either JCanvas3D  
 * @author philip
 *
 */
public class Canvas3D2D extends Canvas3D
{
	private ArrayList<HUDElement> hudElements = new ArrayList<HUDElement>();

	private ArrayList<Panel3D> panel3ds = new ArrayList<Panel3D>();

	// for a last clear call
	private ArrayList<HUDElement> removedHudElements = new ArrayList<HUDElement>();

	private ArrayList<Panel3D> removedPanel3ds = new ArrayList<Panel3D>();

	private HudShape3D hudShapeBG;

	public Canvas3D2D(GraphicsConfiguration gc)
	{
		super(gc);
		hudShapeBG = new HudShape3D(this);

	}

	/**
	 * When the returned tree is live in the scene graph all hud lement output will go to it's 
	 * texture, and the behaviour will keep it updated. If this is not attached to the view platform then all
	 * hud element output will go to the (slower) overlay system.
	 * 
	 * This should be attached to the viewing platform like so:
	 * viewingPlatform.getPlatformGeometry().addChild(canvas3D2D.getHudShapeRoot());
	 * 
	 * @return a tree wiht a hud shape and a behavior for updating the texture
	 */
	public BranchGroup getHudShapeRoot()
	{
		return hudShapeBG;
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
			removedHudElements.add(element);
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
			removedPanel3ds.add(panel3D);
		}
	}

	// For reseting the texture binding in the pipeline (trust me)
	private static Shape3D trivialShape = new Cube(0.01f);

	@Override
	public void postRender()
	{
		//applyPostEffect();

		//	J3dUtil.postProcessFrameBuffer(distortionOffset, this);

		// we only draw if the hud is not in the scene live or any panel3d are enabled
		if (!hudShapeBG.isLive() || hasEnabledPanel3D())
		{
			// Oh my god. Long story short, don't touch this if doing overlays.
			// Longer version, if the last rendered texture on a canvas3d has a transformation
			// then calls to the J3DGraphics2D will inherit it. Easy way to ensure last texture is plain, render trival cube.
			//RAISE_BUG:
//			getGraphicsContext3D().draw(trivialShape);

			//<AND>	J3DGraphics2D g = getGraphics2D();

//			synchronized (hudElements)
//			{
//				for (HUDElement e : hudElements)
//				{
//					if (e != null && e.isEnabled())
//					{
//						g.drawImage(e.getBufferedImage(), e.getAbsoluteX(), e.getAbsoluteY(), null);
//					}
//				}
//			}
//
//			synchronized (panel3ds)
//			{
//				for (Panel3D p : panel3ds)
//				{
//					if (p != null && p.isEnabled())
//					{
//						g.drawImage(p.getBufferedImage(), p.getX(), p.getY(), null);
//					}
//				}
//			}
//
//			g.flush(false);
		}

	}

	public ArrayList<HUDElement> getHudElements()
	{
		return hudElements;
	}

	public ArrayList<Panel3D> getPanel3ds()
	{
		return panel3ds;
	}

	public ArrayList<HUDElement> getRemovedHudElements()
	{
		return removedHudElements;
	}

	public ArrayList<Panel3D> getRemovedPanel3ds()
	{
		return removedPanel3ds;
	}

	public boolean hasEnabledPanel3D()
	{
		boolean enabledPanel3D = false;
		for (Panel3D p : panel3ds)
		{
			if (p != null && p.isEnabled())
			{
				enabledPanel3D = true;
				break;
			}
		}
		return enabledPanel3D;
	}
	
	
	
	
	public boolean isLeft = true;
	/*

	public static boolean applyPostEffect = false;

	public float distortionOffset = 0.25f;

	private BufferedImage img = null;

	private BufferedImage img2 = null;

	private BufferedImage img3 = null;

	private Raster ras = null;

	private AffineTransform tx = null;

	public void applyPostEffect()
	{
		if (applyPostEffect)
		{
			if (img == null)
			{

				Rectangle rect = this.getBounds();
				img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
				img2 = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
				img3 = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);

				tx = new AffineTransform();
				tx.setToScale(1, -1);
				tx.translate(0, -img.getHeight(null));

				//((Graphics2D) img2.getGraphics()).setTransform(af);

				ImageComponent2D comp = new ImageComponent2D(ImageComponent.FORMAT_RGB, img, true, true);

				// The raster components need all be set!
				ras = new Raster(new Point3f(-1.0f, -1.0f, -1.0f), Raster.RASTER_COLOR, 0, 0, rect.width, rect.height, comp, null);
			}

			getGraphicsContext3D().readRaster(ras);
			((Graphics2D) img2.getGraphics()).drawImage(img, tx, null);

			float LensOffset = 0.1453f;//see docs based on physicals sizes
			//TODO: chekc if left/right are swapped somehow?
			if (isLeft)
			{
				LensCenterLocation.set(-LensOffset, 0.0f);
			}
			else
			{
				LensCenterLocation.set(+LensOffset, 0.0f);
			}

			Vector2f texIn = new Vector2f();
			for (int x = 0; x < img2.getWidth(); x++)
			{
				for (int y = 0; y < img2.getHeight(); y++)
				{
					texIn.set((float) x / (float) img2.getWidth(), (float) y / (float) img2.getHeight());
					Tuple2f tc = HmdWarp(texIn);
					if (x == 0 && y == 0 && false)
					{
						System.out.println("x " + ((float) x / (float) img2.getWidth()) + " y " + ((float) y / (float) img2.getHeight()));
						System.out.println(" tc.x " + tc.x + " tc.y " + tc.y);
					}
					tc.x *= img2.getWidth();
					tc.y *= img2.getHeight();
					if ((int) tc.x >= 0 && (int) tc.x < img3.getWidth() && (int) tc.y >= 0 && (int) tc.y < img3.getHeight())
					{
						img3.setRGB(x, y, img2.getRGB((int) tc.x, (int) tc.y));
					}

				}
			}

			getGraphics2D().drawAndFlushImage(img3, 0, 0, null);
		}
	}



	private Vector2f theta = new Vector2f();

	private Tuple2f HmdWarp(Tuple2f texIn)
	{
		theta.set(texIn);// range 0 to 1

		theta.x -= 0.5;
		theta.y -= 0.5;//range now -0.5 to 0.5

		theta.scale(2);//range now -1 to 1

		theta.y /= 1.25; // y over size remove

		theta.add(LensCenterLocation); // add lens offset

		float rSq = (theta.x * theta.x) + (theta.y * theta.y);
		float distort = K0 + //
				(K1 * rSq) + //
				(K2 * rSq * rSq);//
		//+	 (K3 * rSq * rSq * rSq);		
		theta.scale(distort);
		theta.sub(LensCenterLocation);//remove lens offset

		theta.y *= 1.25; // y over size remove

		theta.scale(0.5f);//range now -0.5 to 0.5

		theta.x += 0.5;
		theta.y += 0.5;//range 0 to 1

		//now scale back up to full screen???

		return theta;
	}

	public static float K0 = 1.0f;

	public static float K1 = 0.22f;

	public static float K2 = 0.24f;

	public static float K3 = 0.0f;

	private Tuple2f LensCenterLocation = new Vector2f(0f, 0f);
	*/

}
