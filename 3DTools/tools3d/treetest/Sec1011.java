package tools3d.treetest;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Sec1011
{

	public int int3000;

	public int int3001;

	public int int3002;

	public int int3003;

	public Sec1011(InputStream in) throws IOException
	{
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1012)
		{

			if (id == 5000)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 5001)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 5002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 5003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 5004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 5005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 5006)
			{
				LittleEndianPrimitiveBytes.readByte(in);
			}

			id = LittleEndianPrimitiveBytes.readInt(in);
		}
	}
}
