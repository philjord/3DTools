package org.jogamp.java3d;

import tools.compressedtexture.ktx.KTXBufferedImage;

/**
 * Stealth class to get DXT ByteBuffers handed to the pipeline along with a type that
 * gets them loaded compressed
 * @author philip
 *
 */
public class KTXImageComponent2DRetained extends CompressedImageComponent2DRetained
{

	public KTXImageComponent2DRetained()
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
		return ((KTXBufferedImage) _byRefImage).ktxImage.headers.getGLInternalFormat();
	}

}
