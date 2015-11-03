package old.geography;

import javax.vecmath.Vector3f;

public interface HeightMap
{
	public float getGroundY(double x, double z);

	public void getGroundNormal(Vector3f normal, double x, double z);
}
