package tools3d.treetest;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Sec1006
{
	public int countOf1007;

	public Sec1007[] sec1007s;

	public Sec1006(InputStream in) throws IOException
	{
		countOf1007 = LittleEndianPrimitiveBytes.readInt(in);
		sec1007s = new Sec1007[countOf1007];
		int idx = 0;
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1010)
		{

			if (id == 1007)
			{
				sec1007s[idx] = new Sec1007(in);
				idx++;
			}
			id = LittleEndianPrimitiveBytes.readInt(in);
		}
	}
}
