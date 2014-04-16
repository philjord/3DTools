package org.newdawn.loaders.ac3d;

import java.util.ArrayList;

/**
 * Representation of the AC3D model
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class AC3DModel
{

	/** The AC3DHeader of this model */
	public AC3DHeader header;

	/** The AC3DObjects in this model */
	public ArrayList<AC3DObject> objects = new ArrayList<AC3DObject>();

	/** The AC3DMaterials in this model */
	public ArrayList<AC3DMaterial> materials = new ArrayList<AC3DMaterial>();

	/**
	 * Constructs a default AC3DModel 
	 */
	public AC3DModel()
	{
	}

	/**
	 * Adds an object to the model
	 *
	 * @param object The <code>AC3DObject</code> to add to this model
	 */
	public void addObject(AC3DObject object)
	{
		objects.add(object);
	}

	/**
	 * Adds an AC3DMaterial to the model
	 *
	 * @param newMaterial The <code>AC3DMaterial</code> to add to this model
	 */
	public void addMaterial(AC3DMaterial newMaterial)
	{
		materials.add(newMaterial);
	}

	/**
	 * Sets the AC3DHeader of the model
	 *
	 * @param theHeader The <CODE>AC3DHeader</CODE> to set as this models header
	 */
	public void setHeader(AC3DHeader theHeader)
	{
		header = theHeader;
	}

	/**
	 * Gets the materials 
	 *
	 * @return The AC3DMaterials
	 */
	public ArrayList<AC3DMaterial> getMaterials()
	{
		return materials;
	}

	/**
	 * Gets the objects 
	 *
	 * @return The AC3DObjects
	 */
	public ArrayList<AC3DObject> getObjects()
	{
		return objects;
	}

}
