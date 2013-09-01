package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Sec1007
{
	public String leafTexture;

	public Sec1007(InputStream in) throws IOException
	{
		int id = ByteConvert2.readInt(in);
		while (id != 1008)
		{

			if (id == 4000)
			{
				ByteConvert2.readByte(in);
				// generally appears to be 0?
			}
			else if (id == 4001)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				// three identical 1065353216?
			}
			else if (id == 4002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 4003)
			{
				leafTexture = ByteConvert2.readString(in);
			}
			else if (id == 4004)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 4005)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 4006)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 4007)
			{
				ByteConvert2.readInt(in);
			}

			id = ByteConvert2.readInt(in);
		}

		//System.out.println("\t\t\t\t" + leafTexture);
	}
}
