package tools3d.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.j3d.utils.shader.StringIO;

public class TextIO
{
	public static String getTextFileAsString(String fileName)
	{
		InputStream is = null;
		try
		{
			is = TextIO.class.getResourceAsStream("/" + fileName);
			if (is != null)
			{
				return StringIO.readFully(new BufferedReader(new InputStreamReader(is)));
			}
			else
			{
				return StringIO.readFully(new File(fileName));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (is != null)
					is.close();
			}
			catch (IOException e)
			{
			}
		}
		return null;
	}

}
