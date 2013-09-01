package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Sec1000
{
	public String headerString;

	public Sec1000(InputStream in) throws IOException
	{
		headerString = ByteConvert2.readString(in);
	}
}
