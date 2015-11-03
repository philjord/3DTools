package old.geography;

import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.PickRay;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;

public class LargeGeometry implements HeightMap
{
	private IndexedGeometryArray[][][] subSpaces;

	private Appearance app;

	private int numSpaceX = 0;

	private float minX = Float.MAX_VALUE;

	private float maxX = Float.MIN_VALUE;

	private float spaceSizeX = 0;

	private int numSpaceY = 0;

	private float minY = Float.MAX_VALUE;

	private float maxY = Float.MIN_VALUE;

	private float spaceSizeY = 0;

	private int numSpaceZ = 0;

	private float minZ = Float.MAX_VALUE;

	private float maxZ = Float.MIN_VALUE;

	private float spaceSizeZ = 0;

	public LargeGeometry(int numSpaceX, int numSpaceY, int numSpaceZ, Shape3D sourceShape)
	{
		long start = System.currentTimeMillis();
		this.numSpaceX = numSpaceX;
		this.numSpaceY = numSpaceY;
		this.numSpaceZ = numSpaceZ;

		app = sourceShape.getAppearance();

		ArrayList<Triangle>[][][] subSpacesTriangles = new ArrayList[numSpaceX][numSpaceY][numSpaceZ];

		if (sourceShape.numGeometries() != 1)
		{
			System.out.println("More than one geometry, just using the 0th");
		}
		// Let's just assume the first geometry is the one we want, fuck it.
		Geometry geometry = sourceShape.getGeometry(0);
		if (geometry != null)
		{
			if (geometry instanceof TriangleStripArray)
			{
				TriangleStripArray tsa = (TriangleStripArray) geometry;
				float[] coords = null;
				float[] normals = null;

				boolean hasNormals = (tsa.getVertexFormat() & GeometryArray.NORMALS) != 0;
				boolean hasCoords = (tsa.getVertexFormat() & GeometryArray.COORDINATES) != 0;
				// boolean hasColors3 = (tsa.getVertexFormat() & GeometryArray.COLOR_3) != 0;
				// boolean hasColors4 = (tsa.getVertexFormat() & GeometryArray.COLOR_4) != 0;

				// boolean hasTextureCoord2 = (tsa.getVertexFormat() & GeometryArray.TEXTURE_COORDINATE_2) != 0;
				// boolean hasTextureCoord3 = (tsa.getVertexFormat() & GeometryArray.TEXTURE_COORDINATE_3) != 0;
				// boolean hasTextureCoord4 = (tsa.getVertexFormat() & GeometryArray.TEXTURE_COORDINATE_4) != 0;

				// TODO: there are many possible formats when interleaved, I am assuming normals and coords only
				// if interleaved is true we need to do some hanky panky
				if ((tsa.getVertexFormat() & GeometryArray.INTERLEAVED) != 0)
				{
					/*
					 * If interleaved is true but NIO is false, then you get the interleaved float array by calling
					 * getInterleavedVertices(). You have to examine the vertex format to determine what's included
					 * in each vertex -- the texture data comes first, followed by the color, then the normal, then
					 * the vertex coordinates.
					 */

					/*
					 * System.out.println( "hasNormals " + hasNormals + " hasCoords " + hasCoords + " hasColors3 " +
					 * hasColors3 + " hasColors4 " + hasColors4);
					 * 
					 * System.out.println( "hasTextureCoord2 " + hasTextureCoord2 + " hasTextureCoord3 " +
					 * hasTextureCoord3 + " hasTextureCoord4 " + hasTextureCoord4);
					 */
					if (hasCoords)
					{
						coords = new float[tsa.getVertexCount() * 3];
						float[] interleavedSource = tsa.getInterleavedVertices();
						int idx = 0;
						if (hasNormals)
						{

							normals = new float[tsa.getVertexCount() * 3];
							// note assume not texcoordinate 2
							for (int i = 0; i < interleavedSource.length; i += 6)
							{
								normals[idx] = interleavedSource[i];
								coords[idx] = interleavedSource[i + 3];
								idx++;
								normals[idx] = interleavedSource[i + 1];
								coords[idx] = interleavedSource[i + 4];
								idx++;
								normals[idx] = interleavedSource[i + 2];
								coords[idx] = interleavedSource[i + 5];
								idx++;
							}

						}
						else
						{
							normals = new float[tsa.getVertexCount() * 3];
							// note assume texcoordinate 2
							for (int i = 0; i < interleavedSource.length; i += 5)
							{
								coords[idx] = interleavedSource[i + 2];
								normals[idx] = 0;
								idx++;
								coords[idx] = interleavedSource[i + 3];
								normals[idx] = 1;
								idx++;
								coords[idx] = interleavedSource[i + 4];
								normals[idx] = 0;
								idx++;
							}
						}
					}

					System.out.println("uninterleaved " + (System.currentTimeMillis() - start));
				}
				else
				{
					coords = tsa.getCoordRefFloat();
					normals = tsa.getNormalRefFloat();
				}

				// lets find out min and maxes
				for (int i = 0; i < coords.length; i += 3)
				{
					minX = coords[i] < minX ? coords[i] : minX;
					maxX = coords[i] > maxX ? coords[i] : maxX;
					minY = coords[i + 1] < minY ? coords[i + 1] : minY;
					maxY = coords[i + 1] > maxY ? coords[i + 1] : maxY;
					minZ = coords[i + 2] < minZ ? coords[i + 2] : minZ;
					maxZ = coords[i + 2] > maxZ ? coords[i + 2] : maxZ;
				}
				// now to avoid troublesome calcs later
				minX -= 1;
				maxX += 1;
				minY -= 1;
				maxY += 1;
				minZ -= 1;
				maxZ += 1;

				spaceSizeX = (maxX - minX) / numSpaceX;
				spaceSizeY = (maxY - minY) / numSpaceY;
				spaceSizeZ = (maxZ - minZ) / numSpaceZ;

				int[] stripVCount = new int[tsa.getNumStrips()];
				tsa.getStripVertexCounts(stripVCount);

				// right now let's go through each vertex and which every subspace it falls in add
				// the all three triangles it represents are added to the subspace.

				int stripStartCoordIdx = 0;
				Point3f p = new Point3f();

				for (int stripIdx = 0; stripIdx < stripVCount.length; stripIdx++)
				{
					for (int i = 0; i < stripVCount[stripIdx] * 3; i += 3)
					{
						int currentCoord = stripStartCoordIdx + i;
						p.set(coords[currentCoord], coords[currentCoord + 1], coords[currentCoord + 2]);
						// now work out which subspace p is in
						// get dist from min then div that by space size
						int xSpaceIdx = (int) Math.floor((p.x - minX) / spaceSizeX);
						int ySpaceIdx = (int) Math.floor((p.y - minY) / spaceSizeY);
						int zSpaceIdx = (int) Math.floor((p.z - minZ) / spaceSizeZ);

						if (subSpacesTriangles[xSpaceIdx][ySpaceIdx][zSpaceIdx] == null)
						{
							subSpacesTriangles[xSpaceIdx][ySpaceIdx][zSpaceIdx] = new ArrayList<Triangle>();
						}

						ArrayList<Triangle> spaceVector = subSpacesTriangles[xSpaceIdx][ySpaceIdx][zSpaceIdx];

						// now attach the 3 (or 2 or 1) tris to that geometryarray
						// which are v, v+1, v+2
						// v-1, v, v+1
						// v-2, v-1, v
						// note vertexes not coords

						if (i < (stripVCount[stripIdx] * 3) - 8)
						{
							Triangle t1 = new Triangle();
							t1.v1.set(coords[currentCoord], coords[currentCoord + 1], coords[currentCoord + 2]);
							t1.v2.set(coords[currentCoord + 3], coords[currentCoord + 4], coords[currentCoord + 5]);
							t1.v3.set(coords[currentCoord + 6], coords[currentCoord + 7], coords[currentCoord + 8]);

							t1.n1.set(normals[currentCoord], normals[currentCoord + 1], normals[currentCoord + 2]);
							t1.n2.set(normals[currentCoord + 3], normals[currentCoord + 4], normals[currentCoord + 5]);
							t1.n3.set(normals[currentCoord + 6], normals[currentCoord + 7], normals[currentCoord + 8]);

							spaceVector.add(t1);
						}

						if (i > 2 && i < (stripVCount[stripIdx] * 3) - 5)
						{
							Triangle t2 = new Triangle();
							t2.v1.set(coords[currentCoord - 3], coords[currentCoord - 2], coords[currentCoord - 1]);
							t2.v2.set(coords[currentCoord], coords[currentCoord + 1], coords[currentCoord + 2]);
							t2.v3.set(coords[currentCoord + 3], coords[currentCoord + 4], coords[currentCoord + 5]);

							t2.n1.set(normals[currentCoord - 3], normals[currentCoord - 2], normals[currentCoord - 1]);
							t2.n2.set(normals[currentCoord], normals[currentCoord + 1], normals[currentCoord + 2]);
							t2.n3.set(normals[currentCoord + 3], normals[currentCoord + 4], normals[currentCoord + 5]);

							spaceVector.add(t2);
						}

						if (i > 5)
						{
							Triangle t3 = new Triangle();
							t3.v1.set(coords[currentCoord - 6], coords[currentCoord - 5], coords[currentCoord - 4]);
							t3.v2.set(coords[currentCoord - 3], coords[currentCoord - 2], coords[currentCoord - 1]);
							t3.v3.set(coords[currentCoord], coords[currentCoord + 1], coords[currentCoord + 2]);

							t3.n1.set(normals[currentCoord - 6], normals[currentCoord - 5], normals[currentCoord - 4]);
							t3.n2.set(normals[currentCoord - 3], normals[currentCoord - 2], normals[currentCoord - 1]);
							t3.n3.set(normals[currentCoord], normals[currentCoord + 1], normals[currentCoord + 2]);

							spaceVector.add(t3);
						}

					}
					stripStartCoordIdx += stripVCount[stripIdx] * 3;
				}
			}
			System.out.println("starting conversion " + (System.currentTimeMillis() - start));
			// now covert from the rough vector of triangles to a real geomertry in the subspaces
			subSpaces = convert(subSpacesTriangles);

		}
		else
		{
			System.out.println("geometry 0 is null or not trianglestriparray " + geometry);
		}

		System.out.println("Geomtery indexing took " + (System.currentTimeMillis() - start));

	}

	private static IndexedGeometryArray[][][] convert(ArrayList<Triangle>[][][] subSpacesTriangles)
	{
		int numSpaceX = subSpacesTriangles.length;
		int numSpaceY = subSpacesTriangles[0].length;
		int numSpaceZ = subSpacesTriangles[0][0].length;

		IndexedGeometryArray[][][] subSpaces = new IndexedGeometryArray[numSpaceX][numSpaceY][numSpaceZ];
		Vector3f e1 = new Vector3f();
		Vector3f e2 = new Vector3f();
		Vector3f normal = new Vector3f();

		// now to convert my triangles into a triangle array
		for (int x = 0; x < numSpaceX; x++)
		{
			for (int y = 0; y < numSpaceY; y++)
			{
				for (int z = 0; z < numSpaceZ; z++)
				{
					ArrayList<Triangle> spaceVector = subSpacesTriangles[x][y][z];
					if (spaceVector != null)
					{
						GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
						// now load the triangles in form the vector
						float[] giCoords = new float[spaceVector.size() * 3 * 3];
						float[] giNormals = new float[spaceVector.size() * 3 * 3];
						for (int i = 0; i < spaceVector.size(); i++)
						{
							Triangle t = spaceVector.get(i);

							e1.sub(t.v2, t.v1);
							e2.sub(t.v3, t.v1);

							normal.cross(e1, e2);

							// flip it to point "up" because the windings might be not CCW
							if (normal.y < 0)
							{
								giCoords[(i * 3 * 3)] = t.v1.x;
								giCoords[(i * 3 * 3) + 1] = t.v1.y;
								giCoords[(i * 3 * 3) + 2] = t.v1.z;
								giCoords[(i * 3 * 3) + 3] = t.v3.x;
								giCoords[(i * 3 * 3) + 4] = t.v3.y;
								giCoords[(i * 3 * 3) + 5] = t.v3.z;
								giCoords[(i * 3 * 3) + 6] = t.v2.x;
								giCoords[(i * 3 * 3) + 7] = t.v2.y;
								giCoords[(i * 3 * 3) + 8] = t.v2.z;

								giNormals[(i * 3 * 3)] = t.n1.x;
								giNormals[(i * 3 * 3) + 1] = t.n1.y;
								giNormals[(i * 3 * 3) + 2] = t.n1.z;
								giNormals[(i * 3 * 3) + 3] = t.n3.x;
								giNormals[(i * 3 * 3) + 4] = t.n3.y;
								giNormals[(i * 3 * 3) + 5] = t.n3.z;
								giNormals[(i * 3 * 3) + 6] = t.n2.x;
								giNormals[(i * 3 * 3) + 7] = t.n2.y;
								giNormals[(i * 3 * 3) + 8] = t.n2.z;
							}
							else
							{
								giCoords[(i * 3 * 3)] = t.v1.x;
								giCoords[(i * 3 * 3) + 1] = t.v1.y;
								giCoords[(i * 3 * 3) + 2] = t.v1.z;
								giCoords[(i * 3 * 3) + 3] = t.v2.x;
								giCoords[(i * 3 * 3) + 4] = t.v2.y;
								giCoords[(i * 3 * 3) + 5] = t.v2.z;
								giCoords[(i * 3 * 3) + 6] = t.v3.x;
								giCoords[(i * 3 * 3) + 7] = t.v3.y;
								giCoords[(i * 3 * 3) + 8] = t.v3.z;

								giNormals[(i * 3 * 3)] = t.n1.x;
								giNormals[(i * 3 * 3) + 1] = t.n1.y;
								giNormals[(i * 3 * 3) + 2] = t.n1.z;
								giNormals[(i * 3 * 3) + 3] = t.n2.x;
								giNormals[(i * 3 * 3) + 4] = t.n2.y;
								giNormals[(i * 3 * 3) + 5] = t.n2.z;
								giNormals[(i * 3 * 3) + 6] = t.n3.x;
								giNormals[(i * 3 * 3) + 7] = t.n3.y;
								giNormals[(i * 3 * 3) + 8] = t.n3.z;
							}

						}

						if (giCoords.length > 0)
						{
							// System.out.println("x " + x + " y " + y + " z " + z + " l = " + giCoords.length);
							gi.setCoordinates(giCoords);
							gi.setNormals(giNormals);
							Stripifier strip = new Stripifier();
							strip.stripify(gi);
							IndexedGeometryArray tsa = gi.getIndexedGeometryArray(true, true, true, true, false);
							subSpaces[x][y][z] = tsa;

						}
						else
						{
							// subspace is null
						}
					}
				}
			}
		}

		return subSpaces;
	}

	public synchronized float getGroundY(double x, double z)
	{
		PickResult result = getGroundPickResult(x, z);
		if (result != null && result.numIntersections() > 0)
		{
			PickIntersection intersection = result.getIntersection(0);
			return OVER_HEIGHT_Y - (float) intersection.getDistance();
		}
		else
		{
			return Float.MIN_VALUE;
		}
	}

	// deburners
	private static Vector3f edge1 = new Vector3f();

	private static Vector3f edge2 = new Vector3f();

	/**
	 * store resuiltant normal in normal vector
	 * 
	 * @param normal
	 * @param x
	 * @param z
	 * @param ground
	 * @return
	 */
	public synchronized void getGroundNormal(Vector3f normal, double x, double z)
	{
		PickResult result = getGroundPickResult(x, z);

		if (result != null && result.numIntersections() > 0)
		{
			PickIntersection intersection = result.getIntersection(0);

			Point3d[] cos = intersection.getPrimitiveCoordinates();
			edge1.sub(new Point3f(cos[1]), new Point3f(cos[0]));
			edge2.sub(new Point3f(cos[2]), new Point3f(cos[0]));

			normal.cross(edge1, edge2);

			// flip it to point "up" because the windings might be not CCW
			if (normal.y < 0)
			{
				normal.negate();
			}
			normal.normalize();
		}
		else
		{
			normal.set(0, 0, 0);
		}
	}

	private static Point3d groundPoint = new Point3d();

	private static Shape3D intersectShape = new Shape3D();

	private static PickRay pickRay = new PickRay();

	private static final Vector3d Y_DOWN = new Vector3d(0, -1, 0);

	private static final float OVER_HEIGHT_Y = 1000;

	// no virtualworld transform
	private static Transform3D VWTransform3D = new Transform3D();

	private synchronized PickResult getGroundPickResult(double x, double z)
	{
		groundPoint.set(x, OVER_HEIGHT_Y, z);

		// right now I need to discover what subspaces the ray intersects

		// for now I'll do some crappy crap xz straight down stuff
		int xSpaceIdx = (int) Math.floor((x - minX) / spaceSizeX);
		int zSpaceIdx = (int) Math.floor((z - minZ) / spaceSizeZ);

		if (xSpaceIdx >= 0 && zSpaceIdx >= 0 && xSpaceIdx < numSpaceX && zSpaceIdx < numSpaceZ)
		{
			// now do each y space
			for (int ySpaceIdx = 0; ySpaceIdx < numSpaceY; ySpaceIdx++)
			{
				IndexedGeometryArray tsa = subSpaces[xSpaceIdx][ySpaceIdx][zSpaceIdx];
				if (tsa != null)
				{
					intersectShape.setGeometry(tsa);
					pickRay.set(groundPoint, Y_DOWN);
					PickResult pr = new PickResult(intersectShape, VWTransform3D, pickRay);
					if (pr.numIntersections() > 0)
					{
						return pr;
					}
				}

			}
		}
		return null;

	}

	private class Triangle
	{
		public Point3f v1 = new Point3f();

		public Point3f v2 = new Point3f();

		public Point3f v3 = new Point3f();

		public Point3f n1 = new Point3f();

		public Point3f n2 = new Point3f();

		public Point3f n3 = new Point3f();
	}

	public static void printVertexFormat(GeometryArray ga)
	{
		int format = ga.getVertexFormat();

		boolean COORDINATES = (format & GeometryArray.COORDINATES) != 0;
		boolean NORMALS = (format & GeometryArray.NORMALS) != 0;
		boolean COLOR_3 = (format & GeometryArray.COLOR_3) != 0;
		boolean COLOR_4 = (format & GeometryArray.COLOR_4) != 0;
		boolean TEXTURE_COORDINATE_2 = (format & GeometryArray.TEXTURE_COORDINATE_2) != 0;
		boolean TEXTURE_COORDINATE_3 = (format & GeometryArray.TEXTURE_COORDINATE_3) != 0;
		boolean TEXTURE_COORDINATE_4 = (format & GeometryArray.TEXTURE_COORDINATE_4) != 0;
		boolean BY_REFERENCE = (format & GeometryArray.BY_REFERENCE) != 0;
		boolean INTERLEAVED = (format & GeometryArray.INTERLEAVED) != 0;
		boolean USE_NIO_BUFFER = (format & GeometryArray.USE_NIO_BUFFER) != 0;
		boolean USE_COORD_INDEX_ONLY = (format & GeometryArray.USE_COORD_INDEX_ONLY) != 0;

		System.out.println("COORDINATES " + COORDINATES);
		System.out.println("NORMALS " + NORMALS);
		System.out.println("COLOR_3 " + COLOR_3);
		System.out.println("COLOR_4 " + COLOR_4);
		System.out.println("TEXTURE_COORDINATE_2 " + TEXTURE_COORDINATE_2);
		System.out.println("TEXTURE_COORDINATE_3 " + TEXTURE_COORDINATE_3);
		System.out.println("TEXTURE_COORDINATE_4 " + TEXTURE_COORDINATE_4);
		System.out.println("BY_REFERENCE " + BY_REFERENCE);
		System.out.println("INTERLEAVED " + INTERLEAVED);
		System.out.println("USE_NIO_BUFFER " + USE_NIO_BUFFER);
		System.out.println("USE_COORD_INDEX_ONLY " + USE_COORD_INDEX_ONLY);
	}

	public float getMinX()
	{
		return minX;
	}

	public float getMaxX()
	{
		return maxX;
	}

	public float getMinY()
	{
		return minY;
	}

	public float getMinZ()
	{
		return minZ;
	}

	public float getMaxZ()
	{
		return maxZ;
	}

	/**
	 * @return
	 */
	public Appearance getApp()
	{
		return app;
	}

	/**
	 * @return
	 */
	public int getNumSpaceX()
	{
		return numSpaceX;
	}

	/**
	 * @return
	 */
	public int getNumSpaceY()
	{
		return numSpaceY;
	}

	/**
	 * @return
	 */
	public int getNumSpaceZ()
	{
		return numSpaceZ;
	}

	/**
	 * @return
	 */
	public float getSpaceSizeX()
	{
		return spaceSizeX;
	}

	/**
	 * @return
	 */
	public float getSpaceSizeY()
	{
		return spaceSizeY;
	}

	/**
	 * @return
	 */
	public float getSpaceSizeZ()
	{
		return spaceSizeZ;
	}

	/**
	 * @return
	 */
	public IndexedGeometryArray[][][] getSubSpaces()
	{
		return subSpaces;
	}
}
