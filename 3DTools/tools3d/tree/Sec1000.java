package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Sec1000
{
	public String headerString;

	public Sec1000(InputStream in) throws IOException
	{
		headerString = LittleEndianPrimitiveBytes.readSizedString(in);
	}
}
