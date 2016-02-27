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
		KTXReader reader = KTXReader.getNew(buf);
		headers = reader.getHeaders();
		textureData = reader.getTextureData();
		reader.recycle();
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
