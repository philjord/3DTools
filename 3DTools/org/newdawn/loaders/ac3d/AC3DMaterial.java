package org.newdawn.loaders.ac3d;

import java.awt.Color;

/**
 * Contains all the information for an ac3d material tag
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class AC3DMaterial
{

	/** The index of this material */
	private int index;

	/** The name of the material */
	//private String name;

	/** The colour of the material */
	private Color colour;

	/** The colour of ambient light reflected */
	private Color amb;

	/** The emited colour of this material */
	private Color emis;

	/** The speculative colour of the material */
	private Color spec;

	/** The shinyness */
	private float shininess;

	/** The translucancy of the material */
	private float translucency;

	/**
	 * Creates new Material from the ac3d data lump
	 *
	 * @param index The material index
	 * @param name The name of this material
	 * @param colour The colour
	 * @param ambient The ambient colour
	 * @param emissive The emissive colour
	 * @param specular The specular colour
	 * @param shininess The shininess
	 * @param translucency The transluncancy
	 */
	public AC3DMaterial(int index, String name, Color colour, Color ambient, Color emissive, Color specular, float shininess,
			float translucency)
	{
		this.index = index;
		//this.name = new String(name);
		//take copies of the colour objects
		this.colour = new Color(colour.getRGB());
		this.amb = new Color(ambient.getRGB());
		this.emis = new Color(emissive.getRGB());
		this.spec = new Color(specular.getRGB());
		this.shininess = shininess;
		this.translucency = translucency;
		//System.out.println("Created an AC3DMaterial\n" + 
		//                   "Index: " + index + " name: " + name +
		//                   "\nColour: R: " + colour.getRed() + " G: " + colour.getGreen() + " B: " + colour.getBlue() +
		//                   "\nAmbient: R: " + amb.getRed() + " G: " + amb.getGreen() + " B: " + amb.getBlue() +
		//                   "\nEmissive: R: " + emis.getRed() + " G: " + emis.getGreen() + " B: " + emis.getBlue() +
		//                   "\nSpecular: R: " + spec.getRed() + " G: " + spec.getGreen() + " B: " + spec.getBlue() +
		//                   "\nShininess: " + shininess + " translucency: " + translucency);
	}

	/**
	 * Gets the index
	 *
	 * @return The index
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Gets the colour
	 *
	 * @return The colour
	 */
	public Color getColour()
	{
		return new Color(colour.getRGB());
	}

	/**
	 * Gets the ambient reflectiveness colour
	 *
	 * @return The ambient reflectiveness colour
	 */
	public Color getAmbience()
	{
		return new Color(amb.getRGB());
	}

	/**
	 * Gets the emissive colour
	 *
	 * @return The emissive colour
	 */
	public Color getEmissive()
	{
		return new Color(emis.getRGB());
	}

	/**
	 * Gets the specular colour
	 *
	 * @return The specular colour
	 */
	public Color getSpecular()
	{
		return new Color(spec.getRGB());
	}

	/**
	 * Gets shine
	 *
	 * @return The shine
	 */
	public float getShininess()
	{
		return shininess;
	}

	/**
	 * Gets the translucancy
	 *
	 * @return The translucency
	 */
	public float getTranslucency()
	{
		return translucency;
	}

}
