package tools3d.geography;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * @author pj
 *
  */
public class GroundHeightChanger implements GeometryUpdater
{
	private Point3f center;

	private float radius;

	private Color3f newColor;

	private boolean adjustNormals;

	public GroundHeightChanger(Point3f center, float radius, Color3f newColor, boolean adjustNormals)
	{
		this.center = center;
		this.radius = radius;
		this.newColor = newColor;
		this.adjustNormals = adjustNormals;

	}

	public void updateData(Geometry geometry)
	{
		// get the 
		Point3f xzCenter = new Point3f(center);
		xzCenter.y = 0;
		float newY = center.y;
		center.y = 0;

		TriangleStripArray geom = (TriangleStripArray) geometry;
		float[] coords = geom.getCoordRefFloat();
		float[] colors = geom.getColorRefFloat();
		float[] normals = geom.getNormalRefFloat();

		// flatten them where nessesary
		for (int i = 0; i < coords.length; i += 3)
		{
			float x = coords[i];
			//float y = coords[i + 1];
			float z = coords[i + 2];
			xzCenter.x = x;
			xzCenter.z = z;

			// if we are within the space then set the y down
			// and make it a gray color
			if (center.distance(xzCenter) < radius)
			{
				coords[i + 1] = newY;

				if (newColor != null)
				{
					colors[i] = newColor.x;
					colors[i + 1] = newColor.y;
					colors[i + 2] = newColor.z;
				}

				if (adjustNormals)
				{
					normals[i] = 0;
					normals[i + 1] = 1;
					normals[i + 2] = 0;
				}
			}
		}

		//as all are refs no need to "set" them again.
	}
}
