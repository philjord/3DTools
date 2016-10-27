package org.jogamp.java3d;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jogamp.java3d.ImageComponent2DRetained;
import org.jogamp.java3d.NioImageBuffer;

import javaawt.image.RenderedImage;
import tools.compressedtexture.CompressedBufferedImage;

/**
 * Stealth class to get DXT ByteBuffers handed to the pipeline along with a type that
 * gets them loaded compressed
 * @author philip
 *
 */
public abstract class CompressedImageComponent2DRetained extends ImageComponent2DRetained
{
	protected CompressedBufferedImage _byRefImage;

	public CompressedImageComponent2DRetained()
	{
	}

	@Override
	ImageData createRenderedImageDataObject(RenderedImage byRefImage)
	{
		if (byRefImage instanceof CompressedBufferedImage)
		{
			this._byRefImage = (CompressedBufferedImage) byRefImage;
			return new CompressedImageData(ImageDataType.TYPE_BYTE_BUFFER, width, height, _byRefImage);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

 

	class CompressedImageData extends ImageData
	{
		private CompressedBufferedImage bi;

		private ImageDataType imageDataType;

		private int dataWidth, dataHeight;

		CompressedImageData(ImageDataType imageDataType, int dataWidth, int dataHeight, CompressedBufferedImage byRefImage)
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
			return bi.getBuffer();
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
