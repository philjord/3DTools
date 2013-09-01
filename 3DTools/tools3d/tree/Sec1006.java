package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Sec1006
{
	public int countOf1007;

	public Sec1007[] sec1007s;

	public Sec1006(InputStream in) throws IOException
	{
		countOf1007 = ByteConvert2.readInt(in);
		sec1007s = new Sec1007[countOf1007];
		int idx = 0;
		int id = ByteConvert2.readInt(in);
		while (id != 1010)
		{

			if (id == 1007)
			{
				sec1007s[idx] = new Sec1007(in);
				idx++;
			}
			id = ByteConvert2.readInt(in);
		}
	}
}
