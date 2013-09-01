package org.newdawn.j3d.loaders.ac3d;

import javax.vecmath.Color3f;

/**
 * Contains all the information for an ac3d material tag
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.2 $
 */
public class AC3DMaterial
{

	/** The colour of the material */
	private Color3f colour;

	/** The colour of ambient light reflected */
	private Color3f amb;

	/** The emited colour of this material */
	private Color3f emis;

	/** The speculative colour of the material */
	private Color3f spec;

	/** The real non j3d material */
	private org.newdawn.loaders.ac3d.AC3DMaterial material;

	/**
	 * Creates new Material from the ac3d data
	 *
	 * @param material The real ac3d material
	 */
	public AC3DMaterial(org.newdawn.loaders.ac3d.AC3DMaterial material)
	{
		this.material = material;

		colour = new Color3f(material.getColour());
		amb = new Color3f(material.getAmbience());
		emis = new Color3f(material.getEmissive());
		spec = new Color3f(material.getSpecular());
	}

	/**
	 * Gets the colour
	 *
	 * @return The colour
	 */
	public Color3f getColour()
	{
		return colour;
	}

	/**
	 * Gets the ambient reflectiveness colour
	 *
	 * @return The ambient reflectiveness colour
	 */
	public Color3f getAmbience()
	{
		return amb;
	}

	/**
	 * Gets the emissive colour
	 *
	 * @return The emissive colour
	 */
	public Color3f getEmissive()
	{
		return emis;
	}

	/**
	 * Gets the specular colour
	 *
	 * @return The specular colour
	 */
	public Color3f getSpecular()
	{
		return spec;
	}

	/**
	 * Gets shine
	 *
	 * @return The shine
	 */
	public float getShininess()
	{
		return material.getShininess();
	}

	/**
	 * Gets the translucancy
	 *
	 * @return The translucency
	 */
	public float getTranslucency()
	{
		return material.getTranslucency();
	}

}
