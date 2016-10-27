package org.jogamp.java3d;

import javaawt.image.RenderedImage;
import tools.compressedtexture.astc.ASTCBufferedImage;


public class ASTCImageComponent2D extends CompressedImageComponent2D
{
	/**
	 * See DDSTextureLoader for an example of how to use this class
	 * 
	 * Note ByRef and YUp are forced to true to ensure no image copies happen inside TextureRetained
	 * 
	 * @param format Only ImageComponent.FORMAT_RGBA supported
	 * @param image Only a DDSBufferedImage can be handed to DDSImageComponent2D
	 */
	public ASTCImageComponent2D(int format, RenderedImage image)
	{
		super(format, image);
		if (!(image instanceof ASTCBufferedImage))
		{
			throw new IllegalArgumentException("Only a DDSBufferedImage can be handed to DDSImageComponent2D");
		}
	}

	/**
	 * Use a special Retained
	 */
	@Override
	void createRetained()
	{
		this.retained = new ASTCImageComponent2DRetained();
		this.retained.setSource(this);
	}

}
