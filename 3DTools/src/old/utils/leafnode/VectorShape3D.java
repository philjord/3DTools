package old.utils.leafnode;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import tools3d.utils.PhysAppearance;

public class VectorShape3D extends Shape3D
{

	public VectorShape3D(Point3d start, Vector3d normal)
	{
		Point3d end = new Point3d(start);
		end.add(normal);
		init(start, end);
	}

	public VectorShape3D(Point3d start, Point3d end)
	{
		init(start, end);
	}

	private void init(Point3d start, Point3d end)
	{
		LineArray cube = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

		float[] verts = new float[6];
		verts[0] = (float) start.x;
		verts[1] = (float) start.y;
		verts[2] = (float) start.z;
		verts[3] = (float) end.x;
		verts[4] = (float) end.y;
		verts[5] = (float) end.z;

		float[] colors = new float[6];
		colors[0] = 0.1f;
		colors[1] = 0.2f;
		colors[2] = 0.3f;
		colors[3] = 0.4f;
		colors[4] = 0.5f;
		colors[5] = 0.6f;

		cube.setCoordinates(0, verts);
		cube.setColors(0, colors);

		this.setGeometry(cube);

		this.setAppearance(new PhysAppearance());

	}

}
