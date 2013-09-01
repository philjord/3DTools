package tools3d.tree;

import javax.vecmath.Point3f;

public class BezierSpline
{
	public String data;

	public float n1;

	public float n2;

	public float n3;

	public int pointCount;

	public float[] pointsT;

	// list of "t's" always starts with 0 and ends with 1 if 2 point 0,1 is the contents if 3 might be 0,0.25,1 etc
	public float[] pointsF1;

	// some values 0.5,1  1,0.000782381  0.5,0.5  0.00138887,0.998903,-6.24607e-008  0.5,0.577452,1.3037

	public Point3f[] points;

	public BezierSpline(String data) throws NumberFormatException
	{
		this.data = data;
		if (!data.startsWith("BezierSpline"))
			throw new NumberFormatException("Bad bezier spline string");

		int start = data.indexOf(" ") + 1;
		int end = data.indexOf("\t");
		n1 = Float.parseFloat(data.substring(start, end));
		start = end + 1;
		end = data.indexOf("\t", start);
		n2 = Float.parseFloat(data.substring(start, end));
		start = end + 1;
		end = data.indexOf("\n", start);
		n3 = Float.parseFloat(data.substring(start, end));
		start = data.indexOf("\t", start) + 1;
		end = data.indexOf("\n", start);
		pointCount = Integer.parseInt(data.substring(start, end));

		pointsT = new float[pointCount];
		pointsF1 = new float[pointCount];
		points = new Point3f[pointCount];

		for (int i = 0; i < pointCount; i++)
		{
			start = data.indexOf("\t", start) + 1;
			end = data.indexOf(" ", start);
			pointsT[i] = Float.parseFloat(data.substring(start, end));
			start = end + 1;
			end = data.indexOf(" ", start);
			pointsF1[i] = Float.parseFloat(data.substring(start, end));
			start = end + 1;
			end = data.indexOf(" ", start);
			float x = Float.parseFloat(data.substring(start, end));
			start = end + 1;
			end = data.indexOf(" ", start);
			float y = Float.parseFloat(data.substring(start, end));
			start = end + 1;
			end = data.indexOf("\n", start);
			float z = Float.parseFloat(data.substring(start, end));
			points[i] = new Point3f(x, y, z);
		}

	}

	public String toString()
	{
		return data;
	}

}
