package tools.compressedtexture.ktx;

import tools.compressedtexture.CompressedBufferedImage;

/**
 * This is NOT A bufferedImage! you can't use it as one. If you get UnsupportedOperationException then you 
 * are trying to use it as a BufferedImage, do not. You might need {@code DDSDecompressor}.
 * This class hides as a BufferedImage inside the Texture and ImageComponent worlds to be manipulated 
 * and finally arrive at the gl.glCompressedTexImage2D call in JoglPipeline.updateTexture2DImage.
 * It is what the DDSImageComponent2D constructor requires.
 */
public class KTXBufferedImage extends CompressedBufferedImage
{
	public KTXImage ktxImage;

	/**
	 * see {@code DDSTextureLoader} for example usage
	 * @param ddsImage
	 * @param mipNumber
	 * @param imageName
	 */
	public KTXBufferedImage(KTXImage ktxImage, int mipmapLevel, String imageName)
	{
		// minimal impact BufferedImage constructor
		super();

		this.ktxImage = ktxImage;
		this.buffer = ktxImage.textureData.get(mipmapLevel);
		this.imageName = imageName;
		this.width = ktxImage.textureData.getWidth(mipmapLevel);
		this.height = ktxImage.textureData.getHeight(mipmapLevel);

		if (width < 1 || height < 1)
		{
			throw new IllegalArgumentException("Height or Width == 0");
		}

		// assume all values that get here are gonna be fine

	}

}
