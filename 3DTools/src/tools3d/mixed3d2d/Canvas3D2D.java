package tools3d.mixed3d2d;

import java.awt.GraphicsConfiguration;
import java.io.IOException;
import java.util.Arrays;

import javax.media.j3d.Canvas3D;

import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.geom.SVertex;
import com.jogamp.graph.geom.Vertex;
import com.jogamp.graph.geom.Vertex.Factory;
import com.jogamp.newt.MonitorDevice;
import com.jogamp.newt.Window;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.test.junit.graph.demos.ui.CrossHair;
import com.jogamp.opengl.test.junit.graph.demos.ui.Label;
import com.jogamp.opengl.test.junit.graph.demos.ui.SceneUIController;
import com.jogamp.opengl.test.junit.graph.demos.ui.UIShape;

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
	public boolean isLeft = false;

	public Canvas3D2D(GraphicsConfiguration gc)
	{
		super(gc);

		initOverlySystem();
	}

	private RenderState rs;
	private SceneUIController sceneUIController;
	private final float sceneDist = 1000f;
	private final float zNear = 0.1f, zFar = 7000f;

	private int renderModes = 0;
	private RegionRenderer renderer;
	private final int fontSet = FontFactory.UBUNTU;//TODO: add morrowind fonts
	private Font font;
	private final float fontSizeFpsPVP = 0.038f;
	private float dpiH = 96;


	public void initOverlySystem()
	{
		final Object upObj = this.getGLWindow().getUpstreamWidget();

		if (upObj instanceof Window)
		{
			//FIXME:
			// this right here is the screen 3d gear graphics device stuff
			final Window upWin = (Window) upObj;
			final MonitorDevice mm = upWin.getMainMonitor();
			final float[] monitorDPI = mm.getPixelsPerMM(new float[2]);
			monitorDPI[0] *= 25.4f;
			monitorDPI[1] *= 25.4f;
			final float[] sDPI = upWin.getPixelsPerMM(new float[2]);
			sDPI[0] *= 25.4f;
			sDPI[1] *= 25.4f;
			dpiH = sDPI[1];
			System.err.println("Monitor detected: " + mm);
			System.err.println("Monitor dpi: " + monitorDPI[0] + " x " + monitorDPI[1]);
			System.err.println("Surface scale: native " + Arrays.toString(upWin.getMaximumSurfaceScale(new float[2])) + ", current "
					+ Arrays.toString(upWin.getCurrentSurfaceScale(new float[2])));
			System.err.println("Surface dpi " + sDPI[0] + " x " + sDPI[1]);
		}
		else
		{
			System.err.println("Using default DPI of " + dpiH);
		}

		//System.err.println("Chosen: " + drawable.getChosenGLCapabilities());

		try
		{
			font = FontFactory.get(fontSet).getDefault();
		}
		catch (final IOException ioe)
		{
			throw new RuntimeException(ioe);
		}

		sceneUIController = new SceneUIController(sceneDist, zNear, zFar);
		this.rs = RenderState.createRenderState(SVertex.factory());

		renderer = RegionRenderer.create(rs, RegionRenderer.defaultBlendEnable, RegionRenderer.defaultBlendDisable);
		rs.setHintMask(RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED);

		GL2ES2 gl = this.getGLWindow().getGL().getGL2ES2();
		renderer.init(gl, renderModes);

		sceneUIController.setRenderer(renderer);

		sceneUIController.init(this.getGLWindow());

	}

	public void postRender()
	{
		sceneUIController.display(this.getGLWindow());
	}

	public Label createLabel()
	{
		/**
		 * [Label] Display 112.88889 dpi, fontSize 12.0 ppi -> pixelSize 18.814816
		 * [FPS] Display 112.88889 dpi, fontSize 12.0 ppi -> pixelSize 15.679012
		 */
		final float pixelSizeFPS = fontSizeFpsPVP * this.getGLWindow().getSurfaceHeight();
		Label ret = new Label(renderer.getRenderState().getVertexFactory(), renderModes, font, pixelSizeFPS * 0.1f, "Nothing there yet");

		sceneUIController.addShape(ret);

		return ret;
	}

	public void addUIShape(UIShape uiShape)
	{
		sceneUIController.addShape(uiShape);
	}

	public void removeUIShape(UIShape uiShape)
	{
		sceneUIController.removeShape(uiShape);
	}

	public Factory<? extends Vertex> getVertexFactory()
	{
		return renderer.getRenderState().getVertexFactory();
	}

	public void addElement(awt.tools3d.mixed3d2d.hud.HUDElement textElement)
	{
		// TODO Auto-generated method stub

	}

	public void removeElement(awt.tools3d.mixed3d2d.hud.HUDElement textElement)
	{
		// TODO Auto-generated method stub

	}

}
