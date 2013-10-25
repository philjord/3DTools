package javax.media.j3d;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DDSImageComponent2DRetained extends ImageComponent2DRetained
{
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
		int unitsPerPixel = 4;
		return new ImageData2(ImageDataType.TYPE_INT_ARRAY, width * height * depth * unitsPerPixel, width, height, byRefImage);
	}

	class ImageData2 extends ImageData
	{
		private BufferedImage bi;

		private ImageDataType imageDataType = ImageDataType.TYPE_NULL;

		private int length = 0;

		private boolean dataIsByRef = false;

		private int dataWidth, dataHeight;

		ImageData2(ImageDataType imageDataType, int length, int dataWidth, int dataHeight, Object byRefImage)
		{
			// no impact super constructor
			super(ImageDataType.TYPE_INT_ARRAY, 0, 0, 0);

			this.imageDataType = imageDataType;
			this.length = length;
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
			return length;
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
			//This is the critical part, it calls a rebuild of uncompressed data
			// therefore only the compress DDSbi is held
			return ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
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
