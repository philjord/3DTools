package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Trunk1002
{
	public String barkTexture2000;

	public int int2001;

	public byte byte2002;

	public int int2003;

	public int int2005;

	public int int2006;

	public int int2007;

	public Branches1014 branches1014;

	public Trunk1002(InputStream in) throws IOException
	{

		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1003)
		{
			if (id == 2000)
				barkTexture2000 = LittleEndianPrimitiveBytes.readSizedString(in);
			else if (id == 2001)
				int2001 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 2002)
				byte2002 = LittleEndianPrimitiveBytes.readByte(in);
			else if (id == 2003)
				int2003 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 2005)
				int2005 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 2006)
				int2006 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 2007)
				int2007 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 1014)
				branches1014 = new Branches1014(in);

			id = LittleEndianPrimitiveBytes.readInt(in);
		}

		//System.out.println("\t\t " + barkTexture2000);
	}
}
