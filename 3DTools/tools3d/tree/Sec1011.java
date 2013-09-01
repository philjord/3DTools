package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Sec1011
{

	public int int3000;

	public int int3001;

	public int int3002;

	public int int3003;

	public Sec1011(InputStream in) throws IOException
	{
		int id = ByteConvert2.readInt(in);
		while (id != 1012)
		{

			if (id == 5000)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 5001)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 5002)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 5003)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 5004)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 5005)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 5006)
			{
				ByteConvert2.readByte(in);
			}

			id = ByteConvert2.readInt(in);
		}
	}
}
