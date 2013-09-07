package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Sec1007
{
	public String leafTexture;

	public Sec1007(InputStream in) throws IOException
	{
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1008)
		{

			if (id == 4000)
			{
				LittleEndianPrimitiveBytes.readByte(in);
				// generally appears to be 0?
			}
			else if (id == 4001)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				// three identical 1065353216?
			}
			else if (id == 4002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 4003)
			{
				leafTexture = LittleEndianPrimitiveBytes.readSizedString(in);
			}
			else if (id == 4004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 4005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 4006)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 4007)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}

			id = LittleEndianPrimitiveBytes.readInt(in);
		}

		//System.out.println("\t\t\t\t" + leafTexture);
	}
}
