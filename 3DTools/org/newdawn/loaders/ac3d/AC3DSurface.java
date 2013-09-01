package org.newdawn.loaders.ac3d;

/**
 * Contains all the information for a surface
 * The vertex references and texture coordinates are returned in arrays, these
 * arrays are ordered the same, so index i in the vertex reference array referes
 * to the same vertex as as index i in the texture coordinate array.
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class AC3DSurface
{
	/** polygon type*/
	public static final int POLYGON = 0;

	/** closed line type*/
	public static final int CLOSED_LINE = 1;

	/** open line type*/
	public static final int LINE = 2;

	/** The type of this surface */
	private int type;

	/** is two sided?*/
	private boolean twoSided;

	/** is shaded */
	private boolean shaded;

	/** materials index */
	private int material;

	/** The vertecies on this surface */
	private int[] surfVerts;

	/** The texture coordiantes for each vertex */
	private float[][] textCoords;

	/**
	 * Creates new AC3DSurface 
	 *
	 * @param type The type of this object
	 * @param twoSided Indication of twosided/singlesided
	 * @param shaded Indication of shadinging on/off
	 * @param material The index to the material to use
	 * @param surfVerts The index of the vertex
	 * @param textCoords The texture coordinates (unmodified)
	 */
	public AC3DSurface(int type, boolean twoSided, boolean shaded, int material, int[] surfVerts, float[][] textCoords)
	{

		this.type = type;
		this.twoSided = twoSided;
		this.shaded = shaded;
		this.material = material;
		this.surfVerts = surfVerts;
		this.textCoords = textCoords;

		//System.out.println("Created surface, type: " + type + " twosided: " + twoSided + " shaded: " + shaded + " with " + surfVerts.length + " vertecies");
	}

	/**
	 * Gets the type of this surface
	 *
	 * @return The type
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Is this surface two sided?
	 *
	 * @return True if this surface is a line
	 */
	public boolean isShaded()
	{
		return shaded;
	}

	/**
	 * Gets vertecies as reference to vertecies in the parent AC3DObject
	 *
	 * @return The vertex references
	 */
	public int[] getVertexReferences()
	{
		return surfVerts;
	}

	/**
	 * Gets the texture coordinates
	 *
	 * @return The texture coordinates
	 */
	public float[][] getTextureCoordinates()
	{
		return textCoords;
	}

	/**
	 * Is this surface a line?
	 *
	 * @return True if this surface is a line
	 */
	public boolean isLine()
	{
		if (type != POLYGON)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Is this surface two sided?
	 *
	 * @return True if this surface is two sided
	 */
	public boolean isTwoSided()
	{
		return twoSided;
	}

	/**
	 * Gets the material id of this surface
	 *
	 * @return The material ID
	 */
	public int getMaterial()
	{
		return material;
	}

	/**
	 * Gets the vertex count, used to verify that this surface is valid
	 * e.g. that asa poly it must have more than 3 vertecies
	 *
	 * @return The number of vertecies on this surface
	 */
	public int getVertexReferenceCount()
	{
		return surfVerts.length;
	}

}
