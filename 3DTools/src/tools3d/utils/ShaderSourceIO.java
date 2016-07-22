package tools3d.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.j3d.utils.shader.StringIO;

public class ShaderSourceIO
{
	public static boolean ES_SHADERS = false;

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

			if (ES_SHADERS)
			{
				sourceCode = sourceCode.replace("#version 120", "#version 100");
				sourceCode = sourceCode.replace("#version 140", "#version 300 es");
				sourceCode = sourceCode.replace("#version 150", "#version 300 es");

				//also swap the normal swizzle!
				sourceCode = sourceCode.replace("vec4 normalMap = vec4( texture2D( NormalMap, offset ).ag * 2.0 - 1.0, 0.0, 0.0 );",
						"vec4 normalMap = texture2D( NormalMap, offset );");
				sourceCode = sourceCode.replace("normalMap.z = sqrt( 1.0 - dot( normalMap.xy,normalMap.xy ) );",
						"normalMap.rgb = normalMap.rgb * 2.0 - 1.0;");
				
				//fo4_env.frag use a function not in frag shader for ES
				//FIXME: the supposedly correct second version makes the shader break on android
			//	sourceCode = sourceCode.replace("vec4 cube = textureCubeLod( CubeMap, reflectedWS, 8.0 - g * 8.0 );"
			//			,"vec4 cube = textureCube( CubeMap, reflectedWS );");
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
