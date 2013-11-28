package javax.media.j3d;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import tools.texture.DDSBufferedImage;
import tools.texture.DDSImage;

public class DDSImageComponent2DRetained extends ImageComponent2DRetained
{
	RenderedImage _byRefImage;

	public DDSImageComponent2DRetained()
	{
	}

	@Override
	/**
	 * MASSIVE assumptions that we've been handed a DDSBufferedImage byref yup
	 * for the 1 pixel image we also support bufferedimage
	 * @param byRefImage
	 * @return
	 */
	ImageData createRenderedImageDataObject(RenderedImage byRefImage)
	{
		this._byRefImage = byRefImage;
		if (byRefImage instanceof DDSBufferedImage)
		{
			return new ImageData2(ImageDataType.TYPE_BYTE_BUFFER, -1, width, height, byRefImage);
		}
		else
		{
			int unitsPerPixel = 4;
			return new ImageData2(ImageDataType.TYPE_INT_ARRAY, width * height * depth * unitsPerPixel, width, height, byRefImage);
		}

	}

	int getImageFormatTypeIntValue(boolean powerOfTwoData)
	{
		if (_byRefImage instanceof DDSBufferedImage)
		{
			DDSImage ddsImage = ((DDSBufferedImage) _byRefImage).ddsImage;
			if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1)
			{
				if (!ddsImage.isPixelFormatFlagSet(DDSImage.DDPF_ALPHAPIXELS))
				{

					return GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
				}
				else
				{
					System.out.println("Alpha present in DXT1!;");
					return -1;
				}
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3)
			{
				return GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
			}
			else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5)
			{
				return GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
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
			System.out.println("bad format for now! " + ddsImage.getPixelFormat());
			return -1;
		}
		else
		{
			return super.getImageFormatTypeIntValue(powerOfTwoData);
		}
	}

	class ImageData2 extends ImageData
	{
		private BufferedImage bi;

		private ImageDataType imageDataType = ImageDataType.TYPE_NULL;

		//private int length = 0;

		private boolean dataIsByRef = false;

		private int dataWidth, dataHeight;

		ImageData2(ImageDataType imageDataType, int length, int dataWidth, int dataHeight, RenderedImage byRefImage)
		{
			// no impact super constructor
			super(imageDataType, 0, 0, 0);

			this.imageDataType = imageDataType;
			//this.length = length;
			this.dataWidth = dataWidth;
			this.dataHeight = dataHeight;
			this.dataIsByRef = true;
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
				System.out.println("bad format for now! " + ddsImage.getPixelFormat());
				return null;
			}
			else
			{
				return ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
			}

		}

		/**
		 * Returns is this data is byRef. No internal data is made.
		 */
		boolean isDataByRef()
		{
			return dataIsByRef;
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
