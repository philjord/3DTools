package tools.compressedtexture.dds;

import tools.compressedtexture.CompressedBufferedImage;

/**
 * This is NOT A bufferedImage! you can't use it as one. If you get UnsupportedOperationException then you 
 * are trying to use it as a BufferedImage, do not. You might need {@code DDSDecompressor}.
 * This class hides as a BufferedImage inside the Texture and ImageComponent worlds to be manipulated 
 * and finally arrive at the gl.glCompressedTexImage2D call in JoglPipeline.updateTexture2DImage.
 * It is what the DDSImageComponent2D constructor requires.
 */
public class DDSBufferedImage extends CompressedBufferedImage
{
	public DDSImage ddsImage;

	private DDSImage.ImageInfo imageInfo;

	/**
	 * see {@code DDSTextureLoader} for example usage
	 * @param ddsImage
	 * @param mipNumber
	 * @param imageName
	 */
	public DDSBufferedImage(DDSImage ddsImage, int mipNumber, String imageName)
	{
		// minimal impact BufferedImage constructor
		super();

		this.ddsImage = ddsImage;
		this.imageInfo = ddsImage.getAllMipMaps()[mipNumber];
		this.imageName = imageName;
		this.width = imageInfo.getWidth();
		this.height = imageInfo.getHeight();

		if (width < 1 || height < 1)
		{
			throw new IllegalArgumentException("Height or Width == 0");
		}
		else
		{

			if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT2)
			{
				System.out.println("DXT2 not supported; " + imageName + "; mip num = " + mipNumber);
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT4)
			{
				System.out.println("DXT4 not supported; " + imageName + "; mip num = " + mipNumber);
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_UNKNOWN)
			{
				System.out.println("D3DFMT_UNKNOWN not supported; " + imageName + "; mip num = " + mipNumber);
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1 || //
					ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3 || //
					ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5 || //
					ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8 || //
					ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8 || //
					ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8 || //
					ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F || //
					ddsImage.getPixelFormat() == DDSImage.D3DFMT_ATI2)
			{
				//good
			}
			else
			{
				System.out.println("not DDS format; " + ddsImage.getPixelFormat() + "; " + imageName + "; mip num = " + mipNumber);
			}
		}
		this.buffer = imageInfo.getData();
	}

}
