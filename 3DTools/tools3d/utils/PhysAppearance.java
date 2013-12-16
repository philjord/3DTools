package tools3d.utils;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;

public class PhysAppearance extends Appearance
{
	public PhysAppearance()
	{
		PolygonAttributes polyAtt = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.0f);
		polyAtt.setPolygonOffset(0.1f);
		setPolygonAttributes(polyAtt);
		LineAttributes lineAtt = new LineAttributes(1, LineAttributes.PATTERN_SOLID, false);
		setLineAttributes(lineAtt);
		setTexture(null);
		setMaterial(null);
		// TODO: see bhkEntity for list of colors
		ColoringAttributes colorAtt = new ColoringAttributes(1.0f, 0.0f, 0.0f, ColoringAttributes.FASTEST);
		setColoringAttributes(colorAtt);
	}
}
