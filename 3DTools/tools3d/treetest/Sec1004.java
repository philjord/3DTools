package tools3d.treetest;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

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
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1009)
		{

			if (id == 3000)
				int3000 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3001)
				int3001 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3002)
				int3002 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3003)
				byte3003 = LittleEndianPrimitiveBytes.readByte(in);
			else if (id == 3004)
				int3004 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3005)
				int3005 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3006)
				byte3006 = LittleEndianPrimitiveBytes.readByte(in);
			else if (id == 3007)
				int3007 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3008)
				int3008 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 3009)
				byte3009 = LittleEndianPrimitiveBytes.readByte(in);
			else if (id == 3010)
				int3010 = LittleEndianPrimitiveBytes.readInt(in);

			id = LittleEndianPrimitiveBytes.readInt(in);
		}
	}
}
