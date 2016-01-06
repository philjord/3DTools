package tools3d.universe;

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import javax.media.j3d.AutoOffScreenCanvas3D;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class HeadlessUniverse extends SimpleUniverse
{
	public static Viewer createView()
	{
		Canvas3D c3D = new InternalCanvas3D(SimpleUniverse.getPreferredConfiguration(), true);
		BufferedImage bImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		ImageComponent2D imageCom = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
		c3D.setOffScreenBuffer(imageCom);

		// NOTE: the size, physical width, and physical height of the associated Screen3D must be set explicitly prior
		// to rendering.
		// Failure to do so will result in an exception.
		
		//NOTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! this screen is literally the same device as ALL other universes and canvas etc.
		// so dicking with it here changes tehm to, ha, so off screen is not being understood
		c3D.getScreen3D().setSize(16, 16);
		c3D.getScreen3D().setPhysicalScreenWidth(0.1);
		c3D.getScreen3D().setPhysicalScreenHeight(0.1);

		// create a viewer with the given canvas, physical environment and physical body are defaulted
		Viewer viewer = new Viewer(c3D);

		viewer.getView().setBackClipDistance(1);
		// go a bit slow on the rendering to nowhere, buit note all animation are elapsedframes(0) behaviors
		viewer.getView().setMinimumFrameCycleTime(5);
		return viewer;

	}

	public HeadlessUniverse()
	{
		super(new ViewingPlatform(), createView());
		//NOTE!!! some damn bug in canvas3D requires it to be added to a container before
		// thee behavior schduler starts
		// however adding it to a JFrame doesn't seem to work 
		// but this call here along with teh AutoOffScreenCanvas3D below kicks it into life		
		this.getCanvas().addNotify();

	}

	static class InternalCanvas3D extends Canvas3D implements AutoOffScreenCanvas3D
	{
		public InternalCanvas3D(GraphicsConfiguration config, boolean offscreen)
		{
			super(config, offscreen);
		}
	}
}
