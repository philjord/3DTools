package org.jogamp.java3d;

import org.jogamp.java3d.ImageComponent2D;

import javaawt.image.RenderedImage;

/**
 * Stealth class to get DXT ByteBuffers handed to the pipeline along with a type that
 * gets them loaded compressed
 * This class extends a core class, the jars for java3d must not be sealed
 * This means java3d (e.g. 1.5.2) installed in lib\ext will cause a crash
 * You must try to detect this and alter install to avoid.
 * 
 * @author philip
 *
 */
public abstract class CompressedImageComponent2D extends ImageComponent2D
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
	public CompressedImageComponent2D(int format, RenderedImage image)
	{
		super(format, image, byRef, yUp);

	}
}
