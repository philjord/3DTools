package javax.media.j3d;

import java.awt.image.RenderedImage;

import tools.ddstexture.DDSBufferedImage;

/**
 * This class extends a core class, the jars for java3d must not be sealed
 * This means java3d (e.g. 1.5.2) installed in lib\ext will cause a crash
 * 
 * @author philip
 *
 */
public class DDSImageComponent2D extends ImageComponent2D
{
	private static boolean byRef = true;

	private static boolean yUp = true;

	/**
	 * See DDSTextureLoader for an example of how to use this class
	 * 
	 * Note ByRef and YUp are forced to true to ensure no image copies happen inside TextureRetained
	 * 
	 * @param format Only ImageComponent.FORMAT_RGBA supported
	 * @param image Only a DDSBufferedImage can be handed to DDSImageComponent2D
	 */
	public DDSImageComponent2D(int format, RenderedImage image)
	{
		super(format, image, byRef, yUp);
		if (!(image instanceof DDSBufferedImage))
		{
			throw new IllegalArgumentException("Only a DDSBufferedImage can be handed to DDSImageComponent2D");
		}
	}

	/**
	 * Use a special Retained handler
	 */
	@Override
	void createRetained()
	{
		this.retained = new DDSImageComponent2DRetained();
		this.retained.setSource(this);
	}

}
