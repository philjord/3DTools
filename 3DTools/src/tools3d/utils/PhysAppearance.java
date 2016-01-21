package tools3d.utils;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;

public class PhysAppearance  
{
	private static final boolean USE_SHADERS = true;

	// not to be used anymore
	private PhysAppearance()
	{
	}

	public static Appearance makeAppearance()
	{
		return makeAppearance(new Color3f(1, 0, 0));
	}

	public static Appearance makeAppearance(Color3f color)
	{
		if (USE_SHADERS)
			return new SimpleShaderAppearance(color);
		else
			return makeFFPAppearance(color);
	}

	private static Appearance makeFFPAppearance(Color3f color)
	{
		Appearance app = new Appearance();
		PolygonAttributes polyAtt = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.0f);
		polyAtt.setPolygonOffset(0.1f);
		app.setPolygonAttributes(polyAtt);
		LineAttributes lineAtt = new LineAttributes(1, LineAttributes.PATTERN_SOLID, false);
		app.setLineAttributes(lineAtt);
		app.setTexture(null);
		app.setMaterial(null);
		// TODO: see bhkEntity for list of colors
		ColoringAttributes colorAtt = new ColoringAttributes(color, ColoringAttributes.FASTEST);
		app.setColoringAttributes(colorAtt);

		return app;
	}

}
