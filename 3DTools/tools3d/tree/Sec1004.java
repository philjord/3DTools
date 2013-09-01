package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Sec1004
{
	public int int3000;

	public int int3001;

	public int int3002;

	public byte byte3003;

	public int int3004;

	public int int3005;

	public byte byte3006;

	public int int3007;

	public int int3008;

	public byte byte3009;

	public int int3010;

	public Sec1004(InputStream in) throws IOException
	{
		int id = ByteConvert2.readInt(in);
		while (id != 1009)
		{

			if (id == 3000)
				int3000 = ByteConvert2.readInt(in);
			else if (id == 3001)
				int3001 = ByteConvert2.readInt(in);
			else if (id == 3002)
				int3002 = ByteConvert2.readInt(in);
			else if (id == 3003)
				byte3003 = ByteConvert2.readByte(in);
			else if (id == 3004)
				int3004 = ByteConvert2.readInt(in);
			else if (id == 3005)
				int3005 = ByteConvert2.readInt(in);
			else if (id == 3006)
				byte3006 = ByteConvert2.readByte(in);
			else if (id == 3007)
				int3007 = ByteConvert2.readInt(in);
			else if (id == 3008)
				int3008 = ByteConvert2.readInt(in);
			else if (id == 3009)
				byte3009 = ByteConvert2.readByte(in);
			else if (id == 3010)
				int3010 = ByteConvert2.readInt(in);

			id = ByteConvert2.readInt(in);
		}
	}
}
