package org.jogamp.java3d;

import com.jogamp.opengl.GL3;

import tools.compressedtexture.astc.ASTCBufferedImage;
import tools.compressedtexture.astc.ASTCImage;

/**
 * Stealth class to get DXT ByteBuffers handed to the pipeline along with a type that
 * gets them loaded compressed
 * @author philip
 *
 */
public class ASTCImageComponent2DRetained extends CompressedImageComponent2DRetained
{

	public ASTCImageComponent2DRetained()
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
		ASTCImage astcImage = ((ASTCBufferedImage) _byRefImage).astcImage;
		if (astcImage.hdr.blockdim_z == 1)
		{
			if (astcImage.hdr.blockdim_x == 4)
			{
				if (astcImage.hdr.blockdim_y == 4)
					return GL3.GL_COMPRESSED_RGBA_ASTC_4x4_KHR;
			}
			else if (astcImage.hdr.blockdim_x == 5)
			{
				if (astcImage.hdr.blockdim_y == 4)
					return GL3.GL_COMPRESSED_RGBA_ASTC_5x4_KHR;
				else if (astcImage.hdr.blockdim_y == 5)
					return GL3.GL_COMPRESSED_RGBA_ASTC_5x5_KHR;
			}
			else if (astcImage.hdr.blockdim_x == 6)
			{
				if (astcImage.hdr.blockdim_y == 5)
					return GL3.GL_COMPRESSED_RGBA_ASTC_6x5_KHR;
				else if (astcImage.hdr.blockdim_y == 6)
					return GL3.GL_COMPRESSED_RGBA_ASTC_6x6_KHR;
			}
			else if (astcImage.hdr.blockdim_x == 8)
			{
				if (astcImage.hdr.blockdim_y == 5)
					return GL3.GL_COMPRESSED_RGBA_ASTC_8x5_KHR;
				else if (astcImage.hdr.blockdim_y == 6)
					return GL3.GL_COMPRESSED_RGBA_ASTC_8x6_KHR;
				else if (astcImage.hdr.blockdim_y == 8)
					return GL3.GL_COMPRESSED_RGBA_ASTC_8x8_KHR;
			}
			else if (astcImage.hdr.blockdim_x == 10)
			{
				if (astcImage.hdr.blockdim_y == 5)
					return GL3.GL_COMPRESSED_RGBA_ASTC_10x5_KHR;
				else if (astcImage.hdr.blockdim_y == 6)
					return GL3.GL_COMPRESSED_RGBA_ASTC_10x6_KHR;
				else if (astcImage.hdr.blockdim_y == 8)
					return GL3.GL_COMPRESSED_RGBA_ASTC_10x8_KHR;
				else if (astcImage.hdr.blockdim_y == 10)
					return GL3.GL_COMPRESSED_RGBA_ASTC_10x10_KHR;
			}
			else if (astcImage.hdr.blockdim_x == 12)
			{
				if (astcImage.hdr.blockdim_y == 10)
					return GL3.GL_COMPRESSED_RGBA_ASTC_12x10_KHR;
				else if (astcImage.hdr.blockdim_y == 12)
					return GL3.GL_COMPRESSED_RGBA_ASTC_12x12_KHR;
			}
		}

		System.out.println("Bad ASTC format (for now) " + astcImage.hdr + " in " + _byRefImage.getImageName());
		return -1;
	}

}
