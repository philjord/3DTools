package org.newdawn.j3d.loaders.ac3d.tree;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import org.newdawn.j3d.loaders.ac3d.AC3DMaterial;
import org.newdawn.j3d.loaders.ac3d.AC3DSurface;
import org.newdawn.j3d.loaders.ac3d.NodeFactory;
import org.newdawn.j3d.loaders.resourcepools.TextureResourcePool;

import org.newdawn.loaders.resourcepools.ResourceLoadFailedException;

/**
 * The tree that represents the the AC3DModel, nodes are placed on the texture,
 * the material and the polygon attrbiutes 
 * (line, poly, two/sing sided)
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.5 $
 */
public class ShapeTree
{

	/** The geom nodes */
	private HashMap<GeometryNodeKey, GeometryNode> nodes = new HashMap<GeometryNodeKey, GeometryNode>();

	/** sub trees */
	private ArrayList<ShapeTree> subTrees = new ArrayList<ShapeTree>();

	/**
	 * Creates new AC3DModelTree
	 */
	public ShapeTree()
	{
		//System.out.println("Creating new ShapeTree");
	}

	/**
	 * Adds a surface to the tree
	 * @param surface The surface to add
	 * @param textureName The name of the texture to apply to this surface
	 */
	public void addSurface(AC3DSurface surface, String textureName)
	{
		GeometryNodeKey key = new GeometryNodeKey(surface.isTwoSided(), surface.isLine(), surface.getMaterial(), textureName);
		GeometryNode node = nodes.get(key);

		//System.out.println("Adding geometry to " + key.toString());

		if (node == null)
		{
			//System.out.println("Creating geometry node " + key.toString());
			node = new GeometryNode(surface.isTwoSided(), surface.isLine(), surface.getMaterial(), textureName);
			nodes.put(key, node);
		}

		node.addGeometry(surface.getGeometry());
	}

	/**
	 * Adds a subtree
	 *
	 * @param tree The subtree to add
	 */
	public void addTree(ShapeTree tree)
	{
		subTrees.add(tree);
	}

	/**
	 * Gets the shapes for this tree
	 *
	 * @return The shapes
	 * @param materials List of all the AC3DMaterials used in the model
	 * @param nodeFactory The node factory to used when building the shapes
	 * @throws ResourceLoadFailedException Thrown if a resouce needed to build the shape failed to load
	 */
	public ArrayList<Shape3D> getShapes(AC3DMaterial[] materials, NodeFactory nodeFactory) throws ResourceLoadFailedException
	{
		ArrayList<Shape3D> tempShapes = new ArrayList<Shape3D>();
		Iterator<ShapeTree> subIt = subTrees.iterator();
		Iterator<GeometryNodeKey> keyIt = nodes.keySet().iterator();

		while (subIt.hasNext())
		{
			tempShapes.addAll(subIt.next().getShapes(materials, nodeFactory));
		}

		while (keyIt.hasNext())
		{
			GeometryNodeKey key = keyIt.next();
			GeometryNode node = nodes.get(key);
			Iterator<Geometry> geomIt = node.getGeometrys().iterator();
			AC3DMaterial material;

			Shape3D shape = nodeFactory.createShape3D();
			//int geomCount =0;
			while (geomIt.hasNext())
			{
				shape.addGeometry(geomIt.next());
				//     geomCount++;
			}

			//System.out.println("Added " + geomCount + " geometrys to shape");

			material = materials[node.getMaterialIndex()];
			shape.setAppearance(getAppearance(node.isTwoSided(), node.isLine(), material, node.getTextureName()));

			nodeFactory.finishOffShape(shape);

			tempShapes.add(shape);
		}

		//System.out.println("Returing " + tempShapes.size() + " shape3D(s)");
		return tempShapes;
	}

	/**
	 * Gets the <CODE>Appearance</CODE> object from the setting provided
	 *
	 * @param isTwoSided Idicates the sidedness, <CODE>true</CODE> for two sided
	 * @param isLine Indicates a line or polygon, <CODE>true</CODE> for line
	 * @param ac3dMaterial The <CODE>AC3DMaterial</CODE> describing the attributes of this appearance
	 * @param textureName The full name of the texture to apply
	 * @throws ResourceLoadFailedException Thrown if a resource needed fails to load
	 * @return The <CODE>Appearance</CODE> object
	 */
	public Appearance getAppearance(boolean isTwoSided, boolean isLine, AC3DMaterial ac3dMaterial, String textureName)
			throws ResourceLoadFailedException
	{
		Appearance appearance = new Appearance();
		Material material = new Material();
		TransparencyAttributes transparency;
		TextureAttributes textureAttributes = new TextureAttributes();
		ColoringAttributes colouringAttributes = new ColoringAttributes();
		PolygonAttributes polyAttributes = new PolygonAttributes();

		material.setLightingEnable(true);
		material.setAmbientColor(ac3dMaterial.getAmbience());
		material.setDiffuseColor(ac3dMaterial.getColour());
		material.setEmissiveColor(ac3dMaterial.getEmissive());
		material.setSpecularColor(ac3dMaterial.getSpecular());
		material.setShininess(ac3dMaterial.getShininess());

		if (ac3dMaterial.getTranslucency() > 0)
		{
			transparency = new TransparencyAttributes(TransparencyAttributes.BLENDED, ac3dMaterial.getTranslucency(),
					TransparencyAttributes.BLEND_SRC_ALPHA, TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA);
		}
		else
		{
			transparency = new TransparencyAttributes(TransparencyAttributes.NONE, 0f);
		}

		textureAttributes.setTextureMode(TextureAttributes.MODULATE);

		colouringAttributes.setShadeModel(ColoringAttributes.NICEST);

		if (isTwoSided)
		{
			// set up pooly attributes for twosided lines
			//set back face culling off, and normal flipping on for two sided shapes
			polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
			polyAttributes.setBackFaceNormalFlip(true);
		}
		else
		{
			//set back face culling on, and normal flipping off for one sided shapes
			polyAttributes.setCullFace(PolygonAttributes.CULL_BACK);
			polyAttributes.setBackFaceNormalFlip(false);
		}
		if (isLine)
		{
			//set the polygon type to line for lines
			polyAttributes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		}
		else
		{
			//set the polygon type to line for lines
			polyAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		}

		appearance = new Appearance();
		appearance.setMaterial(material);
		appearance.setTextureAttributes(textureAttributes);
		appearance.setColoringAttributes(colouringAttributes);
		appearance.setPolygonAttributes(polyAttributes);
		appearance.setTransparencyAttributes(transparency);
		if ((textureName != null) && (!textureName.equals("")))
		{
			try
			{
				appearance.setTexture(TextureResourcePool.getTexture(new URL(textureName)));
			}
			catch (MalformedURLException e)
			{
				throw new ResourceLoadFailedException("texture " + textureName + " is invalid");
			}
		}
		return appearance;
	}

}
