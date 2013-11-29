package javax.media.j3d;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import tools.ddstexture.DDSBufferedImage;
import tools.ddstexture.DDSImage;

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
			return new ImageData2(ImageDataType.TYPE_BYTE_BUFFER, width, height, byRefImage);
		}
		else
		{
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Note this does NOT return a 
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
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8 || //
				ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
		{
			//not yet supported
		}
		System.out.println("bad format for now! " + ddsImage.getPixelFormat() + " in " + _byRefImage.getImageName());
		return -1;
	}

	class ImageData2 extends ImageData
	{
		private BufferedImage bi;

		private ImageDataType imageDataType;

		private int dataWidth, dataHeight;

		ImageData2(ImageDataType imageDataType, int dataWidth, int dataHeight, RenderedImage byRefImage)
		{
			// no impact super constructor
			super(imageDataType, 0, 0, 0);
			this.imageDataType = imageDataType;
			this.dataWidth = dataWidth;
			this.dataHeight = dataHeight;
			bi = (BufferedImage) byRefImage;
		}

		/**
		* Returns the type of this DataBuffer.
		*/
		ImageDataType getType()
		{
			return imageDataType;
		}

		/**
		 * Returns the number of elements in this DataBuffer.
		 */
		int length()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns the width of this DataBuffer.
		 */
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
		 * Returns this DataBuffer as an Object.
		 */
		Object get()
		{

			//This is the critical part, this call is optomised to rebuild less often
			//return ((DDSBufferedImage) bi).getInts();

			if (_byRefImage instanceof DDSBufferedImage)
			{
				DDSImage ddsImage = ((DDSBufferedImage) _byRefImage).ddsImage;
				if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1//
						|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3//
						|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5)
				{
					return ((DDSBufferedImage) bi).getBuffer();
				}
				else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8)
				{
				}
				else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8)
				{
				}
				else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8)
				{
				}
				else if (ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
				{
				}
				System.out.println("Bad format for now! " + ((DDSBufferedImage) _byRefImage).getImageName() + "; "
						+ ddsImage.getPixelFormat());
				return null;
			}
			else
			{
				throw new UnsupportedOperationException();
				//return ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
			}

		}

		/**
		 * Returns is this data is byRef. No internal data is made.
		 */
		boolean isDataByRef()
		{
			return true;
		}

		/**
		 * Returns this DataBuffer as a byte array.
		 */
		byte[] getAsByteArray()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns this DataBuffer as an int array.
		 */
		int[] getAsIntArray()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns this DataBuffer as an nio ByteBuffer.
		 */
		ByteBuffer getAsByteBuffer()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns this DataBuffer as an nio IntBuffer.
		 */
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
