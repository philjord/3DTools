/*
 * Created on 6/06/2005
 */
package tools3d.hud.old;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.ModelClip;
import javax.media.j3d.Raster;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import tools3d.hud.HUD;
import tools3d.utils.Utils3D;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * @author pj
 *
 */
public class NewHUD extends HUD
{
	// A model cliop for NOT shjowing the HUD on viewPlatform
	private ModelClip modelClip = new ModelClip();

	private FPSCounter fpsCounter;

	// front Clip is the closest something can be to the camera without
	// being clipped for being to close
	private float frontClipAndABit = 1f;

	public NewHUD(float frontClip)
	{
		this.frontClipAndABit = frontClip * 1.1f;
		addFPS();
		addCrossHair();

		modelClip.addScope(this);
		modelClip.setInfluencingBounds(Utils3D.defaultBounds);

		//lets experiment with rasters
		TextureLoader tex = new TextureLoader("media/images/commsFrameIcon.gif", null);
		ImageComponent2D ic = tex.getImage();

		Raster drawRaster = new Raster(new Point3f(0, 0, -2f), Raster.RASTER_COLOR_DEPTH, 0, 0, ic.getWidth(), ic.getHeight(), ic, null);

		Shape3D shape = new Shape3D(drawRaster);
		drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);

		Appearance app = new Appearance();
		RenderingAttributes ra = new RenderingAttributes(false, false, 0.0f, RenderingAttributes.GREATER);

		app.setRenderingAttributes(ra);
		shape.setAppearance(app);
		BranchGroup objRoot = new BranchGroup();
		objRoot.addChild(shape);
		//	addChild(objRoot);

		//TODO: adding this node causes corruption of the text2d's, why?
	}

	/**
	 * @return a clip that will NOT show the HUD elements (e.g. for the ohv)
	 */
	public ModelClip getClipForHUD()
	{
		return modelClip;
	}

	private void addFPS()
	{
		TransformGroup vtg = Utils3D.createTranslatedTG(new Vector3f(-frontClipAndABit / 2.5f, frontClipAndABit / 3, -frontClipAndABit));
		fpsCounter = new FPSCounter();
		vtg.addChild(fpsCounter);
		addChild(vtg);
	}

	private void addCrossHair()
	{
		CrossHair crossHair = new CrossHair(0.002f);
		TransformGroup vtg = Utils3D.createTranslatedTG(new Vector3f(0f, 0f, -frontClipAndABit));
		vtg.addChild(crossHair);
		addChild(vtg);
	}

}
