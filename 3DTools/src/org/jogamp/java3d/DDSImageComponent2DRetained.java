package org.jogamp.java3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import tools.compressedtexture.dds.DDSBufferedImage;
import tools.compressedtexture.dds.DDSImage;

/**
 * Stealth class to get DXT ByteBuffers handed to the pipeline along with a type that
 * gets them loaded compressed
 * @author philip
 *
 */
public class DDSImageComponent2DRetained extends CompressedImageComponent2DRetained
{

	public DDSImageComponent2DRetained()
	{
	}

	/**
	 * Note this does NOT return a ImageComponentRetained enum value
	 * But the value returned does find its way into the pipeline and cause the compressed image load call.
	 * @param powerOfTwoData
	 * @return
	 */
	@Override
	int getImageFormatTypeIntValue(boolean powerOfTwoData)
	{
		DDSImage ddsImage = ((DDSBufferedImage) _byRefImage).ddsImage;
		if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1)
		{
			return GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3)
		{
			return GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5)
		{
			return GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8)
		{
			return GL2.GL_RGBA_S3TC;
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_ATI2)
		{
			//System.out.println("GL_COMPRESSED_LUMINANCE_ALPHA_LATC2_EXT image type, is this fallout4?");
			return GL2.GL_COMPRESSED_LUMINANCE_ALPHA_LATC2_EXT;
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
		{
			//not yet supported
		}
		System.out.println("Bad DXT format (for now) " + ddsImage.getPixelFormat() + " in " + _byRefImage.getImageName());
		return -1;
	}

}
