package tools.compressedtexture.ktx;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.numericalactivity.dktxtools.ktx.KTXFormatException;
import com.numericalactivity.dktxtools.ktx.KTXHeader;
import com.numericalactivity.dktxtools.ktx.KTXReader;
import com.numericalactivity.dktxtools.ktx.KTXTextureData;

public class KTXImage
{
	public KTXHeader headers;
	public KTXTextureData textureData;

	public KTXImage(ByteBuffer buf) throws IOException, KTXFormatException
	{
		KTXReader reader = new KTXReader(buf, false);
		headers = reader.getHeaders();
		textureData = reader.getTextureData();
		 
	}

	public int getNumMipMaps()
	{
		return headers.getNumberOfMipmapLevels();
	}

	public int getWidth()
	{
		return headers.getPixelWidth();
	}

	public int getHeight()
	{
		return headers.getPixelHeight();
	}

}
