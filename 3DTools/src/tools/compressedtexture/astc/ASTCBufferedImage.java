package tools.compressedtexture.astc;

import tools.compressedtexture.CompressedBufferedImage;

/**
 * This is NOT A bufferedImage! you can't use it as one. If you get UnsupportedOperationException then you 
 * are trying to use it as a BufferedImage, do not. You might need {@code DDSDecompressor}.
 * This class hides as a BufferedImage inside the Texture and ImageComponent worlds to be manipulated 
 * and finally arrive at the gl.glCompressedTexImage2D call in JoglPipeline.updateTexture2DImage.
 * It is what the DDSImageComponent2D constructor requires.
 */
public class ASTCBufferedImage extends CompressedBufferedImage
{

	public ASTCImage astcImage;

	/**
	 * see {@code DDSTextureLoader} for example usage
	 * @param ddsImage
	 * @param mipNumber likely to always be 0
	 * @param imageName
	 */
	public ASTCBufferedImage(ASTCImage astcImage, int mipNumber, String imageName)
	{

		super();

		this.astcImage = astcImage;

		this.imageName = imageName;
		this.width = astcImage.getWidth();
		this.height = astcImage.getHeight();

		if (width < 1 || height < 1)
		{
			throw new IllegalArgumentException("Height or Width == 0");
		}
		else
		{
			// we assume all is well in all cases, how could it hurt?
		}
		this.buffer = astcImage.byteBuffer;
	}

}
