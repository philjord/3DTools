package tools3d.mixed3d2d;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.J3dUtil;
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
 * @author philip
 *
 */
public class Canvas3D2D extends Canvas3D
{
	private ArrayList<HUDElement> hudElements = new ArrayList<HUDElement>();

	private ArrayList<Panel3D> panel3ds = new ArrayList<Panel3D>();

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
	
	@Override
	public void preRender()
	{
		 
	}

	// For reseting the texture binding in the pipeline (trust me)
	private static Shape3D trivialShape = new Cube(0.01f);

	public float distortionOffset = 0.25f;

	public void postRender()
	{
		J3dUtil.postProcessFrameBuffer(distortionOffset);

		// we only draw if the hud is not in the scene live or any panel3d exists
		if (!hudShapeBG.isLive())
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

	public ArrayList<HUDElement> getHudElements()
	{
		return hudElements;
	}

	public ArrayList<Panel3D> getPanel3ds()
	{
		return panel3ds;
	}

	
	@Override
	public void renderField(int fieldDesc)
	{
		//TODO: right and left barrel
		//HOWEVER! poosibly now is too late to set left eye up?
		// but the transparent pass is after so the whole thing may be pointless
		//http://www.conitec.net/shaders/shader_work5.htm
		//http://forum.jogamp.org/Java3D-stereo-td4029914.html 
		//also GraphicsContext3D does cool stuff

		//Pipeline.getPipeline().readRaster
		//https://github.com/ixd-hof/Processing/tree/master/Examples/Oculus%20Rift/OculusRift_Basic/data
	}

}
