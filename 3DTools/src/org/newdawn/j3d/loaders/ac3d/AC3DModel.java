package org.newdawn.j3d.loaders.ac3d;

import java.util.ArrayList;
import java.util.Iterator;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import org.newdawn.j3d.loaders.ac3d.tree.ShapeTree;
import org.newdawn.loaders.InvalidDataException;
import org.newdawn.loaders.resourcepools.ResourceLoadFailedException;

/**
 * An AC3DModel in a form that can be used to make Java3D shapes
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.3 $
 */
public class AC3DModel
{

	/** The texture path */
	private String texturePath;

	/** The real model */
	private org.newdawn.loaders.ac3d.AC3DModel model;

	/** The j3d AC3DMaterials */
	private AC3DMaterial[] materials;

	/** The j3d AC3DObjects */
	private ArrayList<AC3DObject> objects = new ArrayList<AC3DObject>();

	/**
	 * Creates a j3d AC3DModel based on the provided AC3DModel
	 *
	 * @param theModel The real AC3DModel
	 * @param texturePath The path (relative to the model) to the textures
	 * @throws InvalidDataException Thrown if some data in the model is invalid
	 * @throws ResourceLoadFailedException Thrown if a resouce needed for the model failed to load
	 */
	public AC3DModel(org.newdawn.loaders.ac3d.AC3DModel theModel, String texturePath) throws InvalidDataException,
			ResourceLoadFailedException
	{
		this.texturePath = texturePath;
		model = theModel;
		//long startTime = System.currentTimeMillis();

		org.newdawn.loaders.ac3d.AC3DMaterial[] realMaterials = new org.newdawn.loaders.ac3d.AC3DMaterial[0];
		realMaterials = model.getMaterials().toArray(realMaterials);
		materials = new AC3DMaterial[realMaterials.length];

		for (int i = 0; i < realMaterials.length; i++)
		{
			org.newdawn.loaders.ac3d.AC3DMaterial realMaterial = realMaterials[i];
			materials[realMaterial.getIndex()] = new AC3DMaterial(realMaterial);
		}

		Iterator<?> objIt = (model.getObjects()).iterator();
		//int objCount = 0;
		while (objIt.hasNext())
		{
			objects.add(new AC3DObject((org.newdawn.loaders.ac3d.AC3DObject) objIt.next(), this));
			//objCount++;
		}
		//System.out.println("Added " + objCount + " objects at the top level");
		//System.out.println("model converted in " + (System.currentTimeMillis() - startTime) + " milliseconds");
	}

	/**
	 * Returns the texture path (relative to the model)
	 *
	 * @return The ralativeTexturePath
	 */
	public String getTexturePath()
	{
		return texturePath;
	}

	/**
	 * Creates a <CODE>BranchGroup</CODE> containing an instance of the model
	 *
	 * @return The <CODE>Branchgroup</CODE> containting the model
	 * @throws ResourceLoadFailedException Thrown if a resource needed by the model failed to load
	 */
	public BranchGroup createInstance() throws ResourceLoadFailedException
	{
		return createInstance(null, false);
	}

	/**
	 * Creates a <CODE>BranchGroup</CODE> containing an instance of the model
	 *
	 * @param optimise Indication as to wether optimastion of the <CODE>Shape3D</CODE>s should be
	 * performed
	 * @throws ResourceLoadFailedException Thrown if a resouce needed by the model failed to load
	 * @return The <CODE>BranchGroup</CODE> containtg the model
	 */
	public BranchGroup createInstance(boolean optimise) throws ResourceLoadFailedException
	{
		return createInstance(null, optimise);
	}

	/**
	 * Creates a <CODE>BranchGroup</CODE> containing an instance of the model
	 *
	 * @param factory The <CODE>NodeFactory</CODE> to use to build the <CODE>Shape3D</CODE>s
	 * @throws ResourceLoadFailedException Thrown if a resource needed by the model failed to load
	 * @return The <CODE>BranchGroup</CODE> containtg the model
	 */
	public BranchGroup createInstance(NodeFactory factory) throws ResourceLoadFailedException
	{
		return createInstance(factory, false);
	}

	/**
	 * Creates a <CODE>BranchGroup</CODE> containing an instance of the model
	 *
	 * @param nodeFactory The <CODE>NodeFactory</CODE> to use to build the <CODE>Shape3D</CODE>s
	 * @param optimise Indication as to wether optimastion of the <CODE>Shape3D</CODE>s should be
	 * performed
	 * @throws ResourceLoadFailedException Thrown if a resource needed by the model failed to load
	 * @return The <CODE>BranchGroup</CODE> containtg the model
	 */
	public BranchGroup createInstance(NodeFactory nodeFactory, boolean optimise) throws ResourceLoadFailedException
	{
		BranchGroup modelGroup = new BranchGroup();
		ShapeTree tree = new ShapeTree();
		Iterator<AC3DObject> objIt = objects.iterator();

		//long startTime = System.currentTimeMillis();

		if (nodeFactory == null)
		{
			nodeFactory = new DefaultNodeFactory();
		}

		if (optimise)
		{
			while (objIt.hasNext())
			{
				objIt.next().addSurfacesToTree(tree, true);
			}
		}
		else
		{
			while (objIt.hasNext())
			{
				objIt.next().addShapeTreeToTree(tree);
			}
		}

		//System.out.println("Shape trees added in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//long shapeReadStartTime = System.currentTimeMillis();

		Iterator<Shape3D> shapeIt = tree.getShapes(materials, nodeFactory).iterator();

		//int shapeCount = 0;
		while (shapeIt.hasNext())
		{
			modelGroup.addChild(shapeIt.next());
			//	shapeCount++;
		}

		//System.out.println("Created " + shapeCount + " shapes");
		//System.out.println("Created " + shapeCount + " shapes, took " + (System.currentTimeMillis() - shapeReadStartTime) + " milliseconds");
		//System.out.println("Total for createInstance (adding shape trees and creating shapes) was " + (System.currentTimeMillis() - startTime) + " milliseconds");

		return modelGroup;
	}
}
