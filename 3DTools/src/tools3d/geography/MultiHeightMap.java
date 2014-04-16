package tools3d.geography;

import java.util.Vector;

import javax.vecmath.Vector3f;

public class MultiHeightMap implements HeightMap
{
	private Vector<HeightMap> heightMaps = new Vector<HeightMap>();

	public void addHeightMap(HeightMap heightMap)
	{
		heightMaps.add(heightMap);
	}

	public void removeHeightMap(HeightMap heightMap)
	{
		heightMaps.remove(heightMap);
	}

	/**
	 * @see tools3d.geography.HeightMap#getGroundY(double, double)
	 */
	public float getGroundY(double x, double z)
	{
		// return first non bad value
		for (int i = 0; i < heightMaps.size(); i++)
		{
			HeightMap heightMap = heightMaps.elementAt(i);
			float h = heightMap.getGroundY(x, z);
			if (h != Float.MIN_VALUE)
				return h;
		}
		return Float.MIN_VALUE;
	}

	/**
	 * @see tools3d.geography.HeightMap#getGroundNormal(javax.vecmath.Vector3d, double, double)
	 */
	public void getGroundNormal(Vector3f normal, double x, double z)
	{
		for (int i = 0; i < heightMaps.size(); i++)
		{
			HeightMap heightMap = heightMaps.elementAt(i);
			heightMap.getGroundNormal(normal, x, z);
			if (normal.x != 0 || normal.y != 0 || normal.z != 0)
				return;
		}
	}

}
