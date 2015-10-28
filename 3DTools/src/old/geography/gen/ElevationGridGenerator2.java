package old.geography.gen;

import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import org.j3d.geom.GeometryData;
import org.j3d.geom.InvalidArraySizeException;
import org.j3d.geom.UnsupportedTypeException;

/**
 * A generator that takes a set of height values as a grid and turns it into geometry.
 * <p>
 * 
 * The grid can be created either as absolute or relative height values. This setting is controlled as one of the
 * auxillary flags in the {@link org.j3d.geom.GeometryData} class at construction time. In order for this to work, you
 * will also need to provide a base height when setting the terrain.
 * <p>
 * 
 * Points are defined in the height arrays in width first order. Normals, are always smooth blended.
 * 
 * Alan: There are some cases where texture generation is not complete. Especially in regards to 3D textures.
 * 
 * @author Justin Couch
 * @version $Revision: 1.5 $
 */
@SuppressWarnings("unused")
public class ElevationGridGenerator2
{

	/** Auxillary flag to say to generate points as relative values */
	public static final int RELATIVE_HEIGHTS = 0x01;

	/** Auxillary flag to say to generate points as absolute values */
	public static final int ABSOLUTE_HEIGHTS = 0x02;

	/** Current width of the terrain */
	private float terrainWidth;

	/** Depth of the terrain to generate */
	private float terrainDepth;

	/** Number of points in the width direction */
	private int widthPoints;

	/** Number of points in the depth direction */
	private int depthPoints;

	/** The points to use as a 2D array. */
	private float[][] arrayHeights;

	/** The points to use as a 2D array. */
	private Vector3f[][] arrayNormals;

	/** The points to use as a 2D array. */
	private TexCoord2f[][][] arrayTexCoords;

	/** The number of terrain coordinates in use */
	private int numTerrainValues;

	/** The number of texture coordinates in use */
	private int numTexcoordValues;

	/** The array holding all of the vertices after use */
	private float[] terrainCoordinates;

	/** The array holding all of the normals after use */
	private float[] terrainNormals;

	/** The array holding all of the texture coordinates after use */
	private float[] terrainTexcoords;

	/** The number of quads in the terrain */
	private int facetCount;

	/** Working values for the normal generation */
	private Vector3f normal;

	private Vector3f v0;

	private Vector3f v1;

	/**
	 * Construct a default cylinder with the option of having end caps and selectable number of faces around the radius.
	 * The default height is 2 and radius 1.The minimum number of facets is 3.
	 * 
	 * @param w
	 *            The width of the terrain
	 * @param d
	 *            The depth of the terrain
	 * @param wPnts
	 *            The number of heights in the width
	 * @param dPnts
	 *            The number of heights in the depth
	 * @param heights
	 *            The array of height values to use
	 * @param baseHeight
	 *            The base height for relative calcs. May be zero
	 * @throws IllegalArgumentException
	 *             One of the points were <= 1 or the dimensions are non-positive
	 */
	public ElevationGridGenerator2(float w, float d, int wPnts, int dPnts, float[][] heights, Vector3f[][] normals, TexCoord2f[][][] texCoords)
	{
		if ((wPnts < 2) || (dPnts < 2))
			throw new IllegalArgumentException("Point count <= 1");

		if ((w <= 0) || (d <= 0))
			throw new IllegalArgumentException("Dimension <= 0");

		terrainWidth = w;
		terrainDepth = d;
		widthPoints = wPnts;
		depthPoints = dPnts;

		facetCount = (depthPoints - 1) * (widthPoints - 1);

		arrayHeights = heights;
		arrayNormals = normals;
		arrayTexCoords = texCoords;
	}

	/**
	 * Get the dimensions of the terrain. These are returned as 2 values of width and depth respectively for the array.
	 * A new array is created each time so you can do what you like with it.
	 * 
	 * @return The current size of the terrain
	 */
	public float[] getDimensions()
	{
		return new float[]
		{ terrainWidth, terrainDepth };
	}

	/**
	 * Get the number of vertices that this generator will create for the shape given in the definition based on the
	 * current width and height information.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @return The vertex count for the object
	 * @throws UnsupportedTypeException
	 *             The generator cannot handle the type of geometry you have requested.
	 */
	public int getVertexCount(GeometryData data) throws UnsupportedTypeException
	{
		int ret_val = 0;

		switch (data.geometryType)
		{
			case GeometryData.TRIANGLES:
				ret_val = facetCount * 6;
				break;

			case GeometryData.QUADS:
				ret_val = facetCount * 4;
				break;

			// These all have the same vertex count
			case GeometryData.TRIANGLE_STRIPS:
				ret_val = widthPoints * 2 * (depthPoints - 1);
				break;

			case GeometryData.TRIANGLE_FANS:
				ret_val = facetCount * 4;
				break;
			case GeometryData.INDEXED_TRIANGLES:
				ret_val = facetCount * 2;
				break;
			case GeometryData.INDEXED_QUADS:
				ret_val = facetCount * 2;
				break;
			case GeometryData.INDEXED_TRIANGLE_STRIPS:
				ret_val = facetCount * 2;
				break;
			case GeometryData.INDEXED_TRIANGLE_FANS:
				ret_val = facetCount * 2;
				break;

			default:
				throw new UnsupportedTypeException("Unknown geometry type: " + data.geometryType);
		}

		return ret_val;
	}

	/**
	 * Generate a new set of geometry items based on the passed data. If the data does not contain the right minimum
	 * array lengths an exception will be generated. If the array reference is null, this will create arrays of the
	 * correct length and assign them to the return value.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 * @throws UnsupportedTypeException
	 *             The generator cannot handle the type of geometry you have requested
	 */
	public void generate(GeometryData data) throws UnsupportedTypeException, InvalidArraySizeException
	{
		switch (data.geometryType)
		{
			case GeometryData.TRIANGLES:
				unindexedTriangles(data);
				break;
			case GeometryData.TRIANGLE_STRIPS:
				triangleStrips(data);
				break;
			case GeometryData.INDEXED_TRIANGLES:
				indexedTriangles(data);
				break;
			case GeometryData.INDEXED_TRIANGLE_STRIPS:
				indexedTriangleStrips(data);
				break;

			default:
				throw new UnsupportedTypeException("Unknown geometry type: " + data.geometryType);
		}
	}

	/**
	 * Generate a new set of points for an unindexed quad array
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void unindexedTriangles(GeometryData data) throws InvalidArraySizeException
	{
		generateUnindexedTriCoordinates(data);

		if ((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
			generateUnindexedTriNormals(data);

		if ((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
			generateTriTexture2D(data);
		else if ((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
			generateTriTexture3D(data);
	}

	/**
	 * Generate a new set of points for an indexed triangle array
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void indexedTriangles(GeometryData data) throws InvalidArraySizeException
	{
		generateIndexedCoordinates(data);

		if ((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
			generateIndexedNormals(data);

		if ((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
			generateTriTexture2D(data);
		else if ((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
			generateTriTexture3D(data);

		// now let's do the index list
		int index_size = data.vertexCount * 6;

		if (data.indexes == null)
			data.indexes = new int[index_size];
		else if (data.indexes.length < index_size)
			throw new InvalidArraySizeException("Coordinates", data.indexes.length, index_size);

		int[] indexes = data.indexes;
		data.indexesCount = index_size;
		int idx = 0;
		int vtx = 0;

		// each face consists of an anti-clockwise
		for (int i = facetCount; --i >= 0;)
		{
			// triangle 1
			indexes[idx++] = vtx;
			indexes[idx++] = vtx + widthPoints + 1;
			indexes[idx++] = vtx + 1;

			// triangle 2
			indexes[idx++] = vtx + widthPoints;
			indexes[idx++] = vtx + widthPoints + 1;
			indexes[idx++] = vtx;

			vtx++;

			if ((i % (widthPoints - 1)) == 0)
				vtx++;
		}
	}

	/**
	 * Generate a new set of points for a triangle strip array. There is one strip for the side and one strip each for
	 * the ends.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void triangleStrips(GeometryData data) throws InvalidArraySizeException
	{
		generateUnindexedTriStripCoordinates(data);

		if ((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
			generateUnindexedTriStripNormals(data);

		if ((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
			generateUnindexedTriStripTexture2D(data);
		else if ((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
			generateTriTexture3D(data);

		int num_strips = depthPoints - 1;

		if (data.stripCounts == null)
			data.stripCounts = new int[num_strips];
		else if (data.stripCounts.length < num_strips)
			throw new InvalidArraySizeException("Strip counts", data.stripCounts.length, num_strips);

		for (int i = num_strips; --i >= 0;)
			data.stripCounts[i] = widthPoints * 2;
	}

	/**
	 * Generate a new set of points for an indexed triangle strip array. We build the strip from the existing points
	 * starting by working around the side and then doing the top and bottom. To create the ends we start at on radius
	 * point and then always refer to the center for each second item. This wastes every second triangle as a degenerate
	 * triangle, but the gain is less strips needing to be transmitted - ie less memory usage.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void indexedTriangleStrips(GeometryData data) throws InvalidArraySizeException
	{
		generateIndexedCoordinates(data);

		if ((data.geometryComponents & GeometryData.NORMAL_DATA) != 0)
			generateIndexedNormals(data);

		if ((data.geometryComponents & GeometryData.TEXTURE_2D_DATA) != 0)
			generateIndexedTriStripTexture2D(data);
		else if ((data.geometryComponents & GeometryData.TEXTURE_3D_DATA) != 0)
			generateTriTexture3D(data);

		// now let's do the index list
		int index_size = widthPoints * (depthPoints - 1) * 2;
		int num_strips = depthPoints - 1;

		if (data.indexes == null)
			data.indexes = new int[index_size];
		else if (data.indexes.length < index_size)
			throw new InvalidArraySizeException("Indexes", data.indexes.length, index_size);

		if (data.stripCounts == null)
			data.stripCounts = new int[num_strips];
		else if (data.stripCounts.length < num_strips)
			throw new InvalidArraySizeException("Strip counts", data.stripCounts.length, num_strips);

		int[] indexes = data.indexes;
		int[] stripCounts = data.stripCounts;
		data.indexesCount = index_size;
		data.numStrips = num_strips;
		int idx = 0;
		int vtx = 0;
		int total_points = widthPoints * (depthPoints - 1);

		// The side is one big strip
		for (int i = total_points; --i >= 0;)
		{
			indexes[idx++] = vtx;
			indexes[idx++] = vtx + widthPoints;

			vtx++;
		}

		for (int i = num_strips; --i >= 0;)
			stripCounts[i] = widthPoints * 2;
	}

	// ------------------------------------------------------------------------
	// Coordinate generation routines
	// ------------------------------------------------------------------------

	/**
	 * Generates new set of unindexed points for triangles. The array consists of the side coordinates, followed by the
	 * top and bottom.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateUnindexedTriCoordinates(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = depthPoints * widthPoints * 6;

		if (data.coordinates == null)
			data.coordinates = new float[vtx_cnt * 3];
		else if (data.coordinates.length < vtx_cnt * 3)
			throw new InvalidArraySizeException("Coordinates", data.coordinates.length, vtx_cnt * 3);

		float[] coords = data.coordinates;
		data.vertexCount = vtx_cnt;

		regenerateBase();

		int count = 0;
		int i = 0;
		int base_count = 0;
		int width_inc = widthPoints * 3;

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = facetCount; --i >= 0;)
		{
			// triangle 1
			coords[count++] = terrainCoordinates[base_count];
			coords[count++] = terrainCoordinates[base_count + 1];
			coords[count++] = terrainCoordinates[base_count + 2];

			coords[count++] = terrainCoordinates[base_count + width_inc];
			coords[count++] = terrainCoordinates[base_count + width_inc + 1];
			coords[count++] = terrainCoordinates[base_count + width_inc + 2];

			coords[count++] = terrainCoordinates[base_count + 3];
			coords[count++] = terrainCoordinates[base_count + 4];
			coords[count++] = terrainCoordinates[base_count + 5];

			// triangle 2
			coords[count++] = terrainCoordinates[base_count + width_inc];
			coords[count++] = terrainCoordinates[base_count + width_inc + 1];
			coords[count++] = terrainCoordinates[base_count + width_inc + 2];

			coords[count++] = terrainCoordinates[base_count + width_inc + 3];
			coords[count++] = terrainCoordinates[base_count + width_inc + 4];
			coords[count++] = terrainCoordinates[base_count + width_inc + 5];

			coords[count++] = terrainCoordinates[base_count + 3];
			coords[count++] = terrainCoordinates[base_count + 4];
			coords[count++] = terrainCoordinates[base_count + 5];

			base_count += 3;

			if ((i % (widthPoints - 1)) == 0)
				base_count += 3;
		}

	}

	/**
	 * Generates new set of unindexed points for triangles strips. The array consists of one strip per width row.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateUnindexedTriStripCoordinates(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = widthPoints * (depthPoints - 1) * 2;

		if (data.coordinates == null)
			data.coordinates = new float[vtx_cnt * 3];
		else if (data.coordinates.length < vtx_cnt * 3)
			throw new InvalidArraySizeException("Coordinates", data.coordinates.length, vtx_cnt * 3);

		float[] coords = data.coordinates;
		data.vertexCount = vtx_cnt;

		regenerateBase();

		int i;
		int count = 0;
		int base_count = 0;
		int width_inc = widthPoints * 3;
		int total_points = widthPoints * (depthPoints - 1);

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = total_points; --i >= 0;)
		{
			coords[count++] = terrainCoordinates[base_count];
			coords[count++] = terrainCoordinates[base_count + 1];
			coords[count++] = terrainCoordinates[base_count + 2];

			coords[count++] = terrainCoordinates[base_count + width_inc];
			coords[count++] = terrainCoordinates[base_count + width_inc + 1];
			coords[count++] = terrainCoordinates[base_count + width_inc + 2];

			base_count += 3;
		}

	}

	/**
	 * Generates new set of indexed points for triangles or quads. The array consists of the side coordinates, followed
	 * by the center for top, then its points then the bottom center and its points. We do this as they use a completely
	 * different set of normals. The side coordinates are interleved as top and then bottom values.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateIndexedCoordinates(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = widthPoints * depthPoints;

		if (data.coordinates == null)
			data.coordinates = new float[vtx_cnt * 3];
		else if (data.coordinates.length < vtx_cnt * 3)
			throw new InvalidArraySizeException("Coordinates", data.coordinates.length, vtx_cnt * 3);

		float[] coords = data.coordinates;
		data.vertexCount = vtx_cnt;

		regenerateBase();

		System.arraycopy(terrainCoordinates, 0, coords, 0, numTerrainValues);

	}

	// ------------------------------------------------------------------------
	// Normal generation routines
	// ------------------------------------------------------------------------

	/**
	 * Generate a new set of normals for a normal set of unindexed points. Smooth normals are used for the sides at the
	 * average between the faces. Bottom normals always point down.
	 * <p>
	 * This must always be called after the coordinate generation.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateUnindexedTriNormals(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = data.vertexCount * 3;

		if (data.normals == null)
			data.normals = new float[vtx_cnt];
		else if (data.normals.length < vtx_cnt)
			throw new InvalidArraySizeException("Normals", data.normals.length, vtx_cnt);

		regenerateNormals();

		int i = 0;
		int count = 0;
		int base_count = 0;
		int width_inc = widthPoints * 3;
		float[] normals = data.normals;

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = facetCount; --i >= 0;)
		{
			// triangle 1
			normals[count++] = terrainNormals[base_count];
			normals[count++] = terrainNormals[base_count + 1];
			normals[count++] = terrainNormals[base_count + 2];

			normals[count++] = terrainNormals[base_count + width_inc];
			normals[count++] = terrainNormals[base_count + width_inc + 1];
			normals[count++] = terrainNormals[base_count + width_inc + 2];

			normals[count++] = terrainNormals[base_count + 3];
			normals[count++] = terrainNormals[base_count + 4];
			normals[count++] = terrainNormals[base_count + 5];

			// triangle 2
			normals[count++] = terrainNormals[base_count + width_inc];
			normals[count++] = terrainNormals[base_count + width_inc + 1];
			normals[count++] = terrainNormals[base_count + width_inc + 2];

			normals[count++] = terrainNormals[base_count + width_inc + 3];
			normals[count++] = terrainNormals[base_count + width_inc + 4];
			normals[count++] = terrainNormals[base_count + width_inc + 5];

			normals[count++] = terrainNormals[base_count + 3];
			normals[count++] = terrainNormals[base_count + 4];
			normals[count++] = terrainNormals[base_count + 5];

			base_count += 3;

			if ((i % (widthPoints - 1)) == 0)
				base_count += 3;
		}
	}

	/**
	 * Generate a new set of normals for a normal set of unindexed points. Smooth normals are used for the sides at the
	 * average between the faces. Bottom normals always point down.
	 * <p>
	 * This must always be called after the coordinate generation.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateUnindexedTriStripNormals(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = data.vertexCount * 3;

		if (data.normals == null)
			data.normals = new float[vtx_cnt];
		else if (data.normals.length < vtx_cnt)
			throw new InvalidArraySizeException("Normals", data.normals.length, vtx_cnt);

		regenerateNormals();

		int i;
		float[] normals = data.normals;
		int count = 0;
		int base_count = 0;
		int width_inc = widthPoints * 3;
		int total_points = widthPoints * (depthPoints - 1);

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = total_points; --i >= 0;)
		{
			normals[count++] = terrainNormals[base_count];
			normals[count++] = terrainNormals[base_count + 1];
			normals[count++] = terrainNormals[base_count + 2];

			normals[count++] = terrainNormals[base_count + width_inc];
			normals[count++] = terrainNormals[base_count + width_inc + 1];
			normals[count++] = terrainNormals[base_count + width_inc + 2];

			base_count += 3;
		}
	}

	/**
	 * Generate a new set of normals for a normal set of indexed points. Smooth normals are used for the sides at the
	 * average between the faces. Bottom normals always point down.
	 * <p>
	 * This must always be called after the coordinate generation.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateIndexedNormals(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = data.vertexCount * 3;

		if (data.normals == null)
			data.normals = new float[vtx_cnt];
		else if (data.normals.length < vtx_cnt)
			throw new InvalidArraySizeException("Normals", data.normals.length, vtx_cnt);

		regenerateNormals();

		System.arraycopy(terrainNormals, 0, data.normals, 0, numTerrainValues);
	}

	// ------------------------------------------------------------------------
	// Texture coordinate generation routines
	// ------------------------------------------------------------------------
	/**
	 * Generates new set of unindexed texture coordinates for triangles strips. The array consists of one strip per
	 * width row.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateUnindexedTriStripTexture2D(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = widthPoints * (depthPoints - 1) * 2;

		if (data.textureCoordinates == null)
			data.textureCoordinates = new float[vtx_cnt * 2];
		else if (data.textureCoordinates.length < vtx_cnt * 2)
			throw new InvalidArraySizeException("Coordinates", data.textureCoordinates.length, vtx_cnt * 2);

		float[] coords = data.textureCoordinates;

		regenerateTexcoords();

		int i;
		int count = 0;
		int base_count = 0;
		int width_inc = widthPoints * 2;
		int total_points = widthPoints * (depthPoints - 1);

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = total_points; --i >= 0;)
		{
			coords[count++] = terrainTexcoords[base_count];
			coords[count++] = terrainTexcoords[base_count + 1];

			coords[count++] = terrainTexcoords[base_count + width_inc];
			coords[count++] = terrainTexcoords[base_count + width_inc + 1];

			base_count += 2;
		}
	}

	/**
	 * Generates new set of indexed texture coordinates for triangles strips. The array consists of one strip per width
	 * row. NOTE you must provide your own index to the this, as the GeometryData object is filled with a 3 pointer
	 * index
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateIndexedTriStripTexture2D(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = widthPoints * (depthPoints - 1) * 2;

		if (data.textureCoordinates == null)
			data.textureCoordinates = new float[vtx_cnt * 2];
		else if (data.textureCoordinates.length < vtx_cnt * 2)
			throw new InvalidArraySizeException("Coordinates", data.textureCoordinates.length, vtx_cnt * 2);

		regenerateTexcoords();

		System.arraycopy(terrainTexcoords, 0, data.textureCoordinates, 0, terrainTexcoords.length);
	}

	/**
	 * Generate a new set of texCoords for a set of unindexed points.
	 * <p>
	 * This must always be called after the coordinate generation.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateTriTexture2D(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = data.vertexCount * 2;

		if (data.textureCoordinates == null)
			data.textureCoordinates = new float[vtx_cnt];
		else if (data.textureCoordinates.length < vtx_cnt)
			throw new InvalidArraySizeException("2D Texture coordinates", data.textureCoordinates.length, vtx_cnt);

		float[] coords = data.textureCoordinates;

		regenerateTexcoords();

		// System.out.println("Unhandled textured generation case in " + "ElevationGridGenerator");
		int i;
		int count = 0;
		int base_count = 0;
		int width_inc = widthPoints * 2;

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = facetCount; --i >= 0;)
		{
			// triangle 1
			coords[count++] = terrainTexcoords[base_count];
			coords[count++] = terrainTexcoords[base_count + 1];

			coords[count++] = terrainTexcoords[base_count + width_inc];
			coords[count++] = terrainTexcoords[base_count + width_inc + 1];

			coords[count++] = terrainTexcoords[base_count + 2];
			coords[count++] = terrainTexcoords[base_count + 3];

			// triangle 2
			coords[count++] = terrainTexcoords[base_count + width_inc];
			coords[count++] = terrainTexcoords[base_count + width_inc + 1];

			coords[count++] = terrainTexcoords[base_count + width_inc + 2];
			coords[count++] = terrainTexcoords[base_count + width_inc + 3];

			coords[count++] = terrainTexcoords[base_count + 2];
			coords[count++] = terrainTexcoords[base_count + 3];

			base_count += 2;

			if ((i % (widthPoints - 1)) == 0)
				base_count += 2;
		}

	}

	/**
	 * Generate a new set of texCoords for a set of unindexed points.
	 * <p>
	 * This must always be called after the coordinate generation.
	 * 
	 * @param data
	 *            The data to base the calculations on
	 * @throws InvalidArraySizeException
	 *             The array is not big enough to contain the requested geometry
	 */
	private void generateTriTexture3D(GeometryData data) throws InvalidArraySizeException
	{
		int vtx_cnt = data.vertexCount * 2;

		if (data.textureCoordinates == null)
			data.textureCoordinates = new float[vtx_cnt];
		else if (data.textureCoordinates.length < vtx_cnt)
			throw new InvalidArraySizeException("3D Texture coordinates", data.textureCoordinates.length, vtx_cnt);

		// float[] texCoords = data.textureCoordinates;

		System.out.println("Unhandled textured generation case in " + "ElevationGridGenerator");
	}

	/**
	 * Regenerate the base coordinate points. These are the flat circle that makes up the base of the code. The
	 * coordinates are generated based on the 2 PI divided by the number of facets to generate.
	 */
	private final void regenerateBase()
	{
		numTerrainValues = widthPoints * depthPoints * 3;

		if ((terrainCoordinates == null) || (numTerrainValues > terrainCoordinates.length))
		{
			terrainCoordinates = new float[numTerrainValues];
		}

		float d = -terrainDepth / 2;
		float w = -terrainWidth / 2;
		float width_inc = terrainWidth / (widthPoints - 1);
		float depth_inc = terrainDepth / (depthPoints - 1);

		int count = 0;

		for (int i = 0; i < depthPoints; i++)
		{
			for (int j = 0; j < widthPoints; j++)
			{
				terrainCoordinates[count++] = w;
				terrainCoordinates[count++] = arrayHeights[i][j];
				terrainCoordinates[count++] = d;

				w += width_inc;
			}

			d += depth_inc;
			w = -terrainWidth / 2;
		}

	}

	/**
	 * Regenerate the base normals points. These are the flat circle that makes up the base of the code. The normals are
	 * generated based the smoothing of normal averages for interior points. Around the edges, we use the average of the
	 * edge value polygons.
	 */
	private final void regenerateNormals()
	{
		if ((terrainNormals == null) || (numTerrainValues > terrainNormals.length))
		{
			terrainNormals = new float[numTerrainValues];
		}

		if (arrayNormals != null)
		{

			int count = 0;

			for (int i = 0; i < depthPoints; i++)
			{
				for (int j = 0; j < widthPoints; j++)
				{
					Vector3f n = arrayNormals[i][j];
					terrainNormals[count++] = n.x;
					terrainNormals[count++] = n.y;
					terrainNormals[count++] = n.z;
				}
			}
		}
		else
		{

			Vector3f norm;
			int count = 0;
			int base_count = 0;
			int i, j;
			int width_inc = widthPoints * 3;

			// The first edge
			// corner point - normal based on only that face
			norm = createFaceNormal(terrainCoordinates, width_inc, 0, 3);

			terrainNormals[count++] = norm.x;
			terrainNormals[count++] = norm.y;
			terrainNormals[count++] = norm.z;

			base_count = 3;

			for (i = 1; i < (widthPoints - 1); i++)
			{
				norm = calcSideAverageNormal(terrainCoordinates, base_count, base_count + 3, base_count + width_inc, base_count - 3);

				terrainNormals[count++] = norm.x;
				terrainNormals[count++] = norm.y;
				terrainNormals[count++] = norm.z;

				base_count += 3;
			}

			// Last corner point of the first row
			norm = createFaceNormal(terrainCoordinates, base_count, base_count + width_inc, base_count - 3);

			terrainNormals[count++] = norm.x;
			terrainNormals[count++] = norm.y;
			terrainNormals[count++] = norm.z;

			base_count += 3;

			// Now, process all of the internal points
			for (i = 1; i < (depthPoints - 1); i++)
			{

				norm = calcSideAverageNormal(terrainCoordinates, base_count, base_count - width_inc, base_count + 3, base_count + width_inc);

				terrainNormals[count++] = norm.x;
				terrainNormals[count++] = norm.y;
				terrainNormals[count++] = norm.z;

				base_count += 3;

				for (j = 1; j < (widthPoints - 1); j++)
				{

					norm = calcQuadAverageNormal(terrainCoordinates, base_count, base_count + 3, base_count + width_inc, base_count - 3, base_count
							- width_inc);

					terrainNormals[count++] = norm.x;
					terrainNormals[count++] = norm.y;
					terrainNormals[count++] = norm.z;

					base_count += 3;
				}

				// Last point of the row
				norm = calcSideAverageNormal(terrainCoordinates, base_count, base_count + width_inc, base_count - 3, base_count - width_inc);

				terrainNormals[count++] = norm.x;
				terrainNormals[count++] = norm.y;
				terrainNormals[count++] = norm.z;

				base_count += 3;
			}

			// The last edge
			// corner point - normal based on only that face
			norm = createFaceNormal(terrainCoordinates, base_count, base_count - width_inc, base_count + 3);

			terrainNormals[count++] = norm.x;
			terrainNormals[count++] = norm.y;
			terrainNormals[count++] = norm.z;

			base_count += 3;

			for (i = 1; i < (widthPoints - 1); i++)
			{
				norm = calcSideAverageNormal(terrainCoordinates, base_count, base_count - 3, base_count - width_inc, base_count + 3);

				terrainNormals[count++] = norm.x;
				terrainNormals[count++] = norm.y;
				terrainNormals[count++] = norm.z;

				base_count += 3;
			}

			// Last corner point of the first row
			norm = createFaceNormal(terrainCoordinates, base_count, base_count - 3, base_count - width_inc);

			terrainNormals[count++] = norm.x;
			terrainNormals[count++] = norm.y;
			terrainNormals[count++] = norm.z;
		}
	}

	/**
	 * Convenience method to calculate the average normal value between two quads - ie along the side of an object
	 * 
	 * @param coords
	 *            The coordinates to generate from
	 * @param p
	 *            The centre point
	 * @param p1
	 *            The first point of the first side
	 * @param p2
	 *            The middle, shared side point
	 * @param p3
	 *            The last point of the second side
	 * @return The averaged vector
	 */
	private Vector3f calcSideAverageNormal(float[] coords, int p, int p1, int p2, int p3)
	{
		Vector3f norm;
		float x, y, z;

		// Normal first for the previous quad
		norm = createFaceNormal(coords, p, p1, p2);
		x = norm.x;
		y = norm.y;
		z = norm.z;

		// Normal for the next quad
		norm = createFaceNormal(coords, p, p2, p3);

		// create the average of each compoenent for the final normal
		norm.x = (norm.x + x) / 2;
		norm.y = (norm.y + y) / 2;
		norm.z = (norm.z + z) / 2;

		norm.normalize();

		return norm;
	}

	/**
	 * Convenience method to create quad average normal amongst four quads based around a common centre point (the one
	 * having the normal calculated).
	 * 
	 * @param coords
	 *            The coordinates to generate from
	 * @param p
	 *            The centre point
	 * @param p1
	 *            shared point between first and last quad
	 * @param p2
	 *            shared point between first and second quad
	 * @param p3
	 *            shared point between second and third quad
	 * @param p4
	 *            shared point between third and fourth quad
	 * @return The averaged vector
	 */
	private Vector3f calcQuadAverageNormal(float[] coords, int p, int p1, int p2, int p3, int p4)
	{
		Vector3f norm;
		float x, y, z;

		// Normal first for quads 1 & 2
		norm = createFaceNormal(coords, p, p2, p1);
		x = norm.x;
		y = norm.y;
		z = norm.z;

		// Normal for the quads 2 & 3
		norm = createFaceNormal(coords, p, p2, p3);

		x += norm.x;
		y += norm.y;
		z += norm.z;

		// Normal for quads 3 & 4
		norm = createFaceNormal(coords, p, p3, p4);

		x += norm.x;
		y += norm.y;
		z += norm.z;

		// Normal for quads 1 & 4
		norm = createFaceNormal(coords, p, p4, p1);

		// create the average of each compoenent for the final normal
		norm.x = (norm.x + x) / 4;
		norm.y = (norm.y + y) / 4;
		norm.z = (norm.z + z) / 4;

		norm.normalize();

		return norm;
	}

	/**
	 * Regenerate the texture coordinate points. Assumes regenerateBase has been called before this
	 */
	private final void regenerateTexcoords()
	{
		numTexcoordValues = widthPoints * depthPoints * 2;

		if ((terrainTexcoords == null) || (numTexcoordValues > terrainTexcoords.length))
		{
			terrainTexcoords = new float[numTexcoordValues];
		}

		float d = 0;
		float w = 0;
		float width_inc = 1.0f / (widthPoints - 1);
		float depth_inc = 1.0f / (depthPoints - 1);

		int count = 0;

		for (int i = 0; i < depthPoints; i++)
		{
			for (int j = 0; j < widthPoints; j++)
			{
				terrainTexcoords[count++] = w;
				terrainTexcoords[count++] = d;

				w += width_inc;
			}

			d += depth_inc;
			w = 0;
		}

	}

	/**
	 * Convenience method to create a normal for the given vertex coordinates and normal array. This performs a cross
	 * product of the two vectors described by the middle and two end points.
	 * 
	 * @param coords
	 *            The coordinate array to read values from
	 * @param p
	 *            The index of the middle point
	 * @param p1
	 *            The index of the first point
	 * @param p2
	 *            The index of the second point
	 * @return A temporary value containing the normal value
	 */
	private Vector3f createFaceNormal(float[] coords, int p, int p1, int p2)
	{
		v0.x = coords[p1] - coords[p];
		v0.y = coords[p1 + 1] - coords[p + 1];
		v0.z = coords[p1 + 2] - coords[p + 2];

		v1.x = coords[p] - coords[p2];
		v1.y = coords[p + 1] - coords[p2 + 1];
		v1.z = coords[p + 2] - coords[p2 + 2];

		normal.cross(v0, v1);
		normal.normalize();

		return normal;
	}

	/**
	 * 
	 * @param x
	 *            by y
	 */
	public static float[] trianglizeTexture2D(int widthPoints, int depthPoints, TexCoord2f[][] texCoords)
	{
		int facetCount = (depthPoints - 1) * (widthPoints - 1);

		float[] coords = new float[depthPoints * widthPoints * 6 * 2];

		int count = 0;

		float[] terrainTexcoords = new float[depthPoints * widthPoints * 2];
		for (int i = 0; i < depthPoints; i++)
		{
			for (int j = 0; j < widthPoints; j++)
			{
				TexCoord2f t = texCoords[i][j];
				terrainTexcoords[count++] = t.x;
				terrainTexcoords[count++] = t.y;
			}
		}

		count = 0;
		int base_count = 0;
		int width_inc = widthPoints * 2;

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (int i = facetCount; --i >= 0;)
		{
			// triangle 1
			coords[count++] = terrainTexcoords[base_count];
			coords[count++] = terrainTexcoords[base_count + 1];

			coords[count++] = terrainTexcoords[base_count + width_inc];
			coords[count++] = terrainTexcoords[base_count + width_inc + 1];

			coords[count++] = terrainTexcoords[base_count + 2];
			coords[count++] = terrainTexcoords[base_count + 3];

			// triangle 2
			coords[count++] = terrainTexcoords[base_count + width_inc];
			coords[count++] = terrainTexcoords[base_count + width_inc + 1];

			coords[count++] = terrainTexcoords[base_count + width_inc + 2];
			coords[count++] = terrainTexcoords[base_count + width_inc + 3];

			coords[count++] = terrainTexcoords[base_count + 2];
			coords[count++] = terrainTexcoords[base_count + 3];

			base_count += 2;

			if ((i % (widthPoints - 1)) == 0)
				base_count += 2;
		}

		return coords;
	}

	// note include MUST be the size of the base heeghts
	public void generatePartialUnindexedTriCoordinates(GeometryData data, boolean[][] includes)
	{
		int vtx_cnt = 0;

		for (int i = 0; i < includes.length; i++)
		{
			for (int j = 0; j < includes[0].length; j++)
			{
				if (includes[i][j])
				{
					vtx_cnt += 6;
				}
			}
		}

		data.coordinates = new float[vtx_cnt * 3];

		float[] coords = data.coordinates;
		data.vertexCount = vtx_cnt;

		regenerateBase();

		int count = 0;
		int i = 0;
		int base_count = 0;
		int width_inc = widthPoints * 3;

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = facetCount; --i >= 0;)
		{
			if (includes[i % (widthPoints - 1)][i / (widthPoints - 1)])
			{

				// triangle 1
				coords[count++] = terrainCoordinates[base_count];
				coords[count++] = terrainCoordinates[base_count + 1];
				coords[count++] = terrainCoordinates[base_count + 2];

				coords[count++] = terrainCoordinates[base_count + width_inc];
				coords[count++] = terrainCoordinates[base_count + width_inc + 1];
				coords[count++] = terrainCoordinates[base_count + width_inc + 2];

				coords[count++] = terrainCoordinates[base_count + 3];
				coords[count++] = terrainCoordinates[base_count + 4];
				coords[count++] = terrainCoordinates[base_count + 5];

				// triangle 2
				coords[count++] = terrainCoordinates[base_count + width_inc];
				coords[count++] = terrainCoordinates[base_count + width_inc + 1];
				coords[count++] = terrainCoordinates[base_count + width_inc + 2];

				coords[count++] = terrainCoordinates[base_count + width_inc + 3];
				coords[count++] = terrainCoordinates[base_count + width_inc + 4];
				coords[count++] = terrainCoordinates[base_count + width_inc + 5];

				coords[count++] = terrainCoordinates[base_count + 3];
				coords[count++] = terrainCoordinates[base_count + 4];
				coords[count++] = terrainCoordinates[base_count + 5];
			}

			base_count += 3;

			if ((i % (widthPoints - 1)) == 0)
				base_count += 3;
		}

	}

	public void generatePartialUnindexedNormals(GeometryData data, boolean[][] includes)
	{
		int vtx_cnt = data.vertexCount * 3;

		data.normals = new float[vtx_cnt];

		float[] normals = data.normals;

		regenerateNormals();

		int count = 0;
		int i = 0;
		int base_count = 0;
		int width_inc = widthPoints * 3;

		// Start of with one less row (width) here because we don't have two
		// sets of coordinates for those.
		for (i = facetCount; --i >= 0;)
		{

			if (includes[i % (widthPoints - 1)][i / (widthPoints - 1)])
			{
				// triangle 1
				normals[count++] = terrainNormals[base_count];
				normals[count++] = terrainNormals[base_count + 1];
				normals[count++] = terrainNormals[base_count + 2];

				normals[count++] = terrainNormals[base_count + width_inc];
				normals[count++] = terrainNormals[base_count + width_inc + 1];
				normals[count++] = terrainNormals[base_count + width_inc + 2];

				normals[count++] = terrainNormals[base_count + 3];
				normals[count++] = terrainNormals[base_count + 4];
				normals[count++] = terrainNormals[base_count + 5];

				// triangle 2
				normals[count++] = terrainNormals[base_count + width_inc];
				normals[count++] = terrainNormals[base_count + width_inc + 1];
				normals[count++] = terrainNormals[base_count + width_inc + 2];

				normals[count++] = terrainNormals[base_count + width_inc + 3];
				normals[count++] = terrainNormals[base_count + width_inc + 4];
				normals[count++] = terrainNormals[base_count + width_inc + 5];

				normals[count++] = terrainNormals[base_count + 3];
				normals[count++] = terrainNormals[base_count + 4];
				normals[count++] = terrainNormals[base_count + 5];
			}

			base_count += 3;

			if ((i % (widthPoints - 1)) == 0)
				base_count += 3;
		}
	}
}
