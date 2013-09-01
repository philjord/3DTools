package org.newdawn.loaders.ac3d;

import java.util.ArrayList;

/**
 * Representation of the AC3D object tag
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.2 $
 */
public class AC3DObject
{

	/** The 'world' tpye */
	public static final int TYPE_WORLD = 0;

	/** The 'poly' tpye */
	public static final int TYPE_POLY = 1;

	/** The 'group' type */
	public static final int TYPE_GROUP = 2;

	/** The type of the object */
	private int type;

	/** The object name */
	private String name;

	/** The texture URL */
	private String textureName;

	/** The rotatino matrix of this object*/
	private float[] rotation;

	/** The location vector of this object*/
	private float[] location;

	/** The objects verticies */
	private float[][] verts;

	/** Texture repeat values*/
	private float textureRepeatX = 1, textureRepeatY = 1;

	/** Texture offset values*/
	private float textureOffsetx = 0f, textureOffsety = 0f;

	/** The surfaces of this object*/
	private ArrayList<AC3DSurface> surfaces = new ArrayList<AC3DSurface>();

	/** This objects sub objects*/
	private ArrayList<AC3DObject> kids = new ArrayList<AC3DObject>();

	/**
	 * Creates new AC3DObject
	 * @param type The type of object
	 * @param name The name of the object
	 * @param textureName The texture name of the object
	 * @param rotation The objects rotation
	 * @param location The objects location
	 * @param verts The objects verticies
	 * @param textureRepeatX The texture repeat value in the x axis
	 * @param textureRepeatY The texture repeat value in the y axis
	 * @param textureOffsetx The texture offset in the X axis
	 * @param textureOffsety The texture off set in the Y axis
	 */
	public AC3DObject(int type, String name, String textureName, float[] rotation, float[] location, float[][] verts, float textureRepeatX,
			float textureRepeatY, float textureOffsetx, float textureOffsety)
	{
		this.type = type;
		this.name = name;
		this.textureName = textureName;
		this.rotation = rotation;
		this.location = location;
		this.verts = verts;
		this.textureRepeatX = textureRepeatX;
		this.textureRepeatY = textureRepeatY;
		this.textureOffsetx = textureOffsetx;
		this.textureOffsety = textureOffsety;
		//System.out.println("Created Object, type: " + type + " name: " + name +
		//                   " textureName: " + textureName + " with " + verts.length + " verticies");
	}

	/**
	 * Add a surface
	 *
	 * @param surface The surface to add
	 */
	public void addSurface(AC3DSurface surface)
	{
		surfaces.add(surface);
	}

	/**
	 * Add a kidd
	 *
	 * @param object The object to add
	 */
	public void addObject(AC3DObject object)
	{
		kids.add(object);
	}

	/**
	 * Gets all the surfaces from this object
	 *
	 * @return The surfaces
	 */
	public ArrayList<AC3DSurface> getSurfaces()
	{
		return surfaces;
	}

	/**
	 * Gets the url of the texture of this object
	 *
	 * @return The name of the texture
	 */
	public String getTextureName()
	{
		if (textureName != null)
		{
			return new String(textureName);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets this objects child objects
	 *
	 * @return The child objects
	 */
	public ArrayList<AC3DObject> getKids()
	{
		return kids;
	}

	/**
	 * Gets the type of this object
	 *
	 * @return The type
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Gets the name of this object
	 *
	 * @return The name
	 */
	public String getName()
	{
		if (name == null)
		{
			return null;
		}
		else
		{
			return new String(name);
		}
	}

	/**
	 * Gets the rotation of this object
	 *
	 * @return The type
	 */
	public float[] getRotation()
	{
		return rotation;
	}

	/**
	 * Gets the location of this object
	 *
	 * @return The locaiton
	 */
	public float[] getLocation()
	{
		return location;
	}

	/**
	 * Gets the vertecies of this object
	 *
	 * @return The verticies
	 */
	public float[][] getVerticies()
	{
		return verts;
	}

	/**
	 * Gets the texture repeat in the x axis of this object
	 *
	 * @return The texture repeat in the x axis
	 */
	public float getTextureRepeatX()
	{
		return textureRepeatX;
	}

	/**
	 * Gets the texture repeat in the y axis of this object
	 *
	 * @return The texture repeat in the y axis
	 */
	public float getTextureRepeatY()
	{
		return textureRepeatY;
	}

	/**
	 * Gets the texture offset in the x axis of this object
	 *
	 * @return The texture offset in the x axis
	 */
	public float getTextureOffsetX()
	{
		return textureOffsetx;
	}

	/**
	 * Gets the texture offset in the y axis of this object
	 *
	 * @return The texture offset in the y axis
	 */
	public float getTextureOffsetY()
	{
		return textureOffsety;
	}

}
