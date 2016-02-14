package tools3d.mixed3d2d;

import java.io.IOException;

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
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.hudbasics.graph.demos.ui.Label;
import com.jogamp.opengl.hudbasics.graph.demos.ui.SceneUIController;
import com.jogamp.opengl.hudbasics.graph.demos.ui.UIShape;

public class Canvas3D2D extends Canvas3D
{
	public boolean isLeft = false;

	public Canvas3D2D()
	{
		super();

		initOverlySystem();
	}

	private RenderState rs;
	private SceneUIController sceneUIController;
	private final float sceneDist = 10f;
	private final float zNear = 0.1f, zFar = 100f;

	private int renderModes = 0;
	private RegionRenderer renderer;

	private Font font;
	private final float fontSizeFpsPVP = 0.038f;
	private float dpiH = 96;

	public void initOverlySystem()
	{
		final Object upObj = this.getGLWindow().getUpstreamWidget();

		if (upObj instanceof Window)
		{
			final Window upWin = (Window) upObj;
			final MonitorDevice mm = upWin.getMainMonitor();
			final float[] monitorDPI = mm.getPixelsPerMM(new float[2]);
			monitorDPI[0] *= 25.4f;
			monitorDPI[1] *= 25.4f;
			final float[] sDPI = upWin.getPixelsPerMM(new float[2]);
			sDPI[0] *= 25.4f;
			sDPI[1] *= 25.4f;
			dpiH = sDPI[1];
			/*	System.err.println("Monitor detected: " + mm);
				System.err.println("Monitor dpi: " + monitorDPI[0] + " x " + monitorDPI[1]);
				System.err.println("Surface scale: native " + Arrays.toString(upWin.getMaximumSurfaceScale(new float[2])) + ", current "
						+ Arrays.toString(upWin.getCurrentSurfaceScale(new float[2])));
				System.err.println("Surface dpi " + sDPI[0] + " x " + sDPI[1]);*/
		}
		else
		{
			System.err.println("Using default DPI of " + dpiH);
		}

		//System.err.println("Chosen: " + drawable.getChosenGLCapabilities());

		try
		{
			font = FontFactory.get(FontFactory.UBUNTU).getDefault();
			//URLConnection u = IOUtil.getResource("fonts/freefont/Pelagiad.ttf", FontSet01.class.getClassLoader());
			//font = FontFactory.get(u.getInputStream(), true);
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
		//Stenciled things might be teh last displayed in the renderer, so turn off the stencil in case it's on
		GL2ES2 gl = this.getGLWindow().getGL().getGL2ES2();
		gl.glDisable(GL2.GL_STENCIL_TEST);
		sceneUIController.display(this.getGLWindow());
	}

	public Label createLabel()
	{
		/**
		 * [Label] Display 112.88889 dpi, fontSize 12.0 ppi -> pixelSize 18.814816
		 * [FPS] Display 112.88889 dpi, fontSize 12.0 ppi -> pixelSize 15.679012
		 */
		final float pixelSizeFPS = fontSizeFpsPVP * this.getGLWindow().getSurfaceHeight();
		Label ret = new Label(renderer.getRenderState().getVertexFactory(), renderModes, font, pixelSizeFPS * 0.1f, "");

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

}
