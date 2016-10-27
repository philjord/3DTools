package tools3d.utils.loader;

import org.jogamp.vecmath.Vector3f;

public class PropertyCodec
{
	public static Vector3f vector3fOut(String in)
	{
		try
		{
			String x = in.substring(in.indexOf("(") + 1, in.indexOf(","));
			String y = in.substring(in.indexOf(",") + 1, in.indexOf(",", in.indexOf(",") + 1));
			String z = in.substring(in.indexOf(",", in.indexOf(",") + 1) + 1, in.indexOf(")"));

			return new Vector3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
		}
		catch (StringIndexOutOfBoundsException e1)
		{
			new Vector3f();
		}
		catch (NumberFormatException e)
		{
			new Exception("Bad float value").printStackTrace();
		}
		return null;

	}

	public static String vector3fIn(Vector3f in)
	{
		return "" + in;
	}
	//TODO:yawpitch quat transform etc
}
