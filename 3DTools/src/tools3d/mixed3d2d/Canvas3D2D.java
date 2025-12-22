package tools3d.mixed3d2d;

import java.io.IOException;

import org.jogamp.java3d.Canvas3D;

import com.jogamp.graph.font.Font;
import com.jogamp.graph.ui.Scene;
import com.jogamp.graph.ui.Shape;
import com.jogamp.graph.ui.shapes.Label;
import com.jogamp.newt.MonitorDevice;
import com.jogamp.newt.Window;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
 

import tools3d.mixed3d2d.curvehud.elements.UbuntuFontLoader;

public class Canvas3D2D extends Canvas3D
{
	public boolean isLeft = false;

	public Canvas3D2D(GLWindow glwin)
	{
		super(glwin);	 
		initRenderer();
	}	
	
	public Canvas3D2D(boolean offScreen)
	{
		super(offScreen);
	}

	public Canvas3D2D()
	{
		super();
		initRenderer();
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		outputOverlayDetails();
	}

	private Scene sceneUIController = new Scene();

	private int renderModes = 0;
	private Font font;
	private final float fontSizeFpsPVP = 0.038f;
	
	
	private void initRenderer()
	{ 
		sceneUIController.init(this.getGLWindow());
		
		try
		{
			font = new UbuntuFontLoader().getDefault();
			//URLConnection u = IOUtil.getResource("fonts/freefont/Pelagiad.ttf", FontSet01.class.getClassLoader());
			//font = FontFactory.get(u.getInputStream(), true);
		}
		catch (final IOException ioe)
		{
			throw new RuntimeException(ioe);
		}

	}
	


	private float dpiH = 96;

	private void outputOverlayDetails()
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
			//System.err.println("Monitor detected: " + mm);
			//System.err.println("Monitor dpi: " + monitorDPI[0] + " x " + monitorDPI[1]);
			//System.err.println("Surface scale: native " + Arrays.toString(upWin.getMaximumSurfaceScale(new float[2])) + ", current "
			//		+ Arrays.toString(upWin.getCurrentSurfaceScale(new float[2])));
			//System.err.println("Surface dpi " + sDPI[0] + " x " + sDPI[1]);

			//PC
			//Monitor detected: Monitor[Id 0x0 [primary], 520 x 290 mm, pixelScale [1.0, 1.0], viewport [ 0 / 0  1920 x 1080 ] [pixels], [ 0 / 0  1920 x 1080 ] [window], orig [Id 0x0, [ 1920 x 1080 pixels x 32 bpp ] @ 60.0 Hz, flags [], 0 degr], curr [Id 0x0, [ 1920 x 1080 pixels x 32 bpp ] @ 60.0 Hz, flags [], 0 degr], modeChanged false, modeCount 148]
			//Monitor dpi: 93.784615 x 94.5931
			//Surface scale: native [1.0, 1.0], current [1.0, 1.0]
			//Surface dpi 93.784615 x 94.5931

			//Android
			//Monitor detected: Monitor[Id 0x0 [primary], 61 x 101 mm, pixelScale [1.0, 1.0], viewport [ 0 / 0  1080 x 1776 ] [pixels], [ 0 / 0  1080 x 1776 ] [window], orig [Id 0x0, [ 1080 x 1776 pixels x 32 bpp ] @ 60.0 Hz, flags [], 0 degr], curr [Id 0x0, [ 1080 x 1776 pixels x 32 bpp ] @ 60.0 Hz, flags [], 0 degr], modeChanged false, modeCount 4]
			//Monitor dpi: 449.7049 x 446.6376
			//Surface scale: native [1.0, 1.0], current [1.0, 1.0]
			//Surface dpi 449.7049 x 446.6376
		}
		else
		{
			System.err.println("Using default DPI of " + dpiH);
		}

		//System.err.println("Chosen: " + drawable.getChosenGLCapabilities());

		

	}

	@Override
	public void postRender()
	{
		//if(false)
		if (this.getGLWindow().getGL() != null)
		{
			//Stenciled things might be the last displayed in the renderer, so turn off the stencil in case it's on
			GL2ES2 gl = this.getGLWindow().getGL().getGL2ES2();
			gl.glDisable(GL2.GL_STENCIL_TEST);
			sceneUIController.display(this.getGLWindow());
		}
	}

	public Label createLabel()
	{
		/**
		 * [Label] Display 112.88889 dpi, fontSize 12.0 ppi -> pixelSize 18.814816
		 * [FPS] Display 112.88889 dpi, fontSize 12.0 ppi -> pixelSize 15.679012
		 */
		final float pixelSizeFPS = fontSizeFpsPVP * this.getGLWindow().getSurfaceHeight();
		Label ret = new Label(renderModes, font, pixelSizeFPS * 0.1f, "");
		sceneUIController.addShape(ret);

		return ret;
	}

	public void addUIShape(Shape uiShape)
	{
		sceneUIController.addShape(uiShape);
	}

	public void removeUIShape(Shape uiShape)
	{
		sceneUIController.removeShape(uiShape);
	}

	public int getRenderMode()
	{
		return renderModes;
	}

	public float getPixelSize()
	{
		return fontSizeFpsPVP * this.getGLWindow().getSurfaceHeight();
	}

	public Font getFont()
	{
		return font;
	}

}
