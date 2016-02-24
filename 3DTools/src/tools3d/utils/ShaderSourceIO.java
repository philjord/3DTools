package tools3d.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.j3d.utils.shader.StringIO;

public class ShaderSourceIO
{
	public static boolean SWAP_VER120_TO_VER100 = false;

	public static String getTextFileAsString(String fileName)
	{
		InputStream is = null;
		try
		{
			String sourceCode;
			is = ShaderSourceIO.class.getResourceAsStream("/" + fileName);
			if (is != null)
			{
				sourceCode = StringIO.readFully(new BufferedReader(new InputStreamReader(is)));
			}
			else
			{
				sourceCode = StringIO.readFully(new File(fileName));
			}

			if (SWAP_VER120_TO_VER100)
			{
				sourceCode = sourceCode.replace("#version 120", "#version 100");
			}

			return sourceCode;
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
