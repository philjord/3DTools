package javax.media.j3d;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import tools.ddstexture.DDSBufferedImage;
import tools.ddstexture.DDSImage;

/**
 * Stealth class to get DXT ByteBuffers handed to the pipeline along with a type that
 * gets them loaded compressed
 * @author philip
 *
 */
public class DDSImageComponent2DRetained extends ImageComponent2DRetained
{
	DDSBufferedImage _byRefImage;

	public DDSImageComponent2DRetained()
	{
	}

	@Override
	ImageData createRenderedImageDataObject(RenderedImage byRefImage)
	{
		if (byRefImage instanceof DDSBufferedImage)
		{
			this._byRefImage = (DDSBufferedImage) byRefImage;
			return new DDSImageData(ImageDataType.TYPE_BYTE_BUFFER, width, height, _byRefImage);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Note this does NOT return a ImageComponentRetained enum value
	 * But teh value returned does find its way into the pipeline and cause the compressed image load call.
	 * @param powerOfTwoData
	 * @return
	 */
	@Override
	int getImageFormatTypeIntValue(boolean powerOfTwoData)
	{
		DDSImage ddsImage = _byRefImage.ddsImage;
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
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
		{
			//not yet supported
		}
		System.out.println("Bad DXT format (for now) " + ddsImage.getPixelFormat() + " in " + _byRefImage.getImageName());
		return -1;
	}

	class DDSImageData extends ImageData
	{
		private BufferedImage bi;

		private ImageDataType imageDataType;

		private int dataWidth, dataHeight;

		DDSImageData(ImageDataType imageDataType, int dataWidth, int dataHeight, DDSBufferedImage byRefImage)
		{
			// no impact super constructor
			super(imageDataType, 0, 0, 0);
			this.imageDataType = imageDataType;
			this.dataWidth = dataWidth;
			this.dataHeight = dataHeight;
			bi = byRefImage;
		}

		/**
		* Returns the type of this DataBuffer.
		*/
		@Override
		ImageDataType getType()
		{
			return imageDataType;
		}

		/**
		 * Returns the width of this DataBuffer.
		 */
		@Override
		int getWidth()
		{
			return dataWidth;
		}

		/**
		 * Returns the height of this DataBuffer.
		 */
		int getHeight()
		{
			return dataHeight;
		}

		/**
		* Returns is this data is byRef. No internal data is made.
		*/
		boolean isDataByRef()
		{
			return true;
		}

		/**
		 * Returns this DataBuffer as an Object.
		 */
		@Override
		Object get()
		{

			DDSImage ddsImage = _byRefImage.ddsImage;
			if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1//
					|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3//
					|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5)
			{
				return ((DDSBufferedImage) bi).getBuffer();
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8)
			{
				return ((DDSBufferedImage) bi).getBuffer();// byte buffer madness
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8)
			{
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8)
			{
			}
			else if (ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
			{
			}
			System.out.println("Bad format for now! " + _byRefImage.getImageName() + "; " + ddsImage.getPixelFormat());
			return null;

		}

		@Override
		int length()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		byte[] getAsByteArray()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		int[] getAsIntArray()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		ByteBuffer getAsByteBuffer()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		IntBuffer getAsIntBuffer()
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	ImageData createRenderedImageDataObject(RenderedImage byRefImage, int dataWidth, int dataHeight)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	ImageData createNioImageBufferDataObject(NioImageBuffer nioImageBuffer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	boolean isImageTypeSupported(NioImageBuffer nioImgBuf)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	void createBlankImageData()
	{
		throw new UnsupportedOperationException();
	}
}
