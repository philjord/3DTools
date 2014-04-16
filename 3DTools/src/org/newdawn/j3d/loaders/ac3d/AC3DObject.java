package org.newdawn.j3d.loaders.ac3d;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.newdawn.j3d.loaders.ac3d.tree.ShapeTree;
import org.newdawn.j3d.loaders.resourcepools.TextureResourcePool;
import org.newdawn.loaders.InvalidDataException;
import org.newdawn.loaders.resourcepools.ResourceLoadFailedException;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Containts the information needed on an AC3D object to make a jav3d
 * representation of it
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.4 $
 */
public class AC3DObject
{

	/** The texture URL */
	private URL textureURL;

	/** The texture path */
	private String texturePath;

	/** The rotatino matrix of this object*/
	// private Matrix3f rotation = null;
	/** The location vector of this object*/
	//private Vector3f location = null;
	/** The surfaces of this object*/
	private ArrayList<AC3DSurface> surfaces = new ArrayList<AC3DSurface>();

	/** This objects sub objects*/
	private ArrayList<AC3DObject> kids = new ArrayList<AC3DObject>();

	/** The transform of this object*/
	private Transform3D transform = new Transform3D();

	/** The transform of this objects after being transformed by the parents transform*/
	private Transform3D adjustedTransform;

	/** The real AC3DObject */
	private org.newdawn.loaders.ac3d.AC3DObject object;

	/** The parent node if it is another j3d AC3DObject */
	private AC3DObject parentObject = null;

	/** The parent node if it is the j3d AC3DModel */
	private AC3DModel theModel = null;

	/** Adjusted vertecies */
	private Point3f[] adjustedVerts;

	/** KEV - The smoothed normals */
	private Vector3f[] smoothedNormals;

	/**
	 * Creates new j3d AC3DObject from the provided information
	 *
	 * @param object The real AC3DObject for this node
	 * @param parentObject The j3d AC3DObject that is this nodes parent;
	 * @throws InvalidDataException Thrown if some data in the file is invald
	 * @throws ResourceLoadFailedException Thrown if a resource needed by the model fails to load
	 */
	public AC3DObject(org.newdawn.loaders.ac3d.AC3DObject object, AC3DObject parentObject) throws InvalidDataException,
			ResourceLoadFailedException
	{
		//long startTime = System.currentTimeMillis();

		if (object == null)
		{
			throw new NullPointerException("object must be non null");
		}
		if (parentObject == null)
		{
			throw new NullPointerException("parentObject must be non null");
		}
		this.object = object;
		this.parentObject = parentObject;
		init();
		//System.out.println("Object created as a child of " + parentObject);
		//System.out.println("Object (" + this + ") converted in " + (System.currentTimeMillis() - startTime) + " milliseconds");
	}

	/**
	 * Creates new j3d AC3DObject from the provided information
	 *
	 * @param object The real AC3DObject for this node
	 * @param theModel The j3d AC3DModel that is this nodes parent;
	 * @throws InvalidDataException Thrown if some data in the file is invalid
	 * @throws ResourceLoadFailedException Thrown if a resouce needed by the model failed to load
	 */
	public AC3DObject(org.newdawn.loaders.ac3d.AC3DObject object, AC3DModel theModel) throws InvalidDataException,
			ResourceLoadFailedException
	{
		//long startTime = System.currentTimeMillis();

		if (object == null)
		{
			throw new NullPointerException("object must be non null");
		}
		if (theModel == null)
		{
			throw new NullPointerException("theModel must be non null");
		}
		this.object = object;
		this.theModel = theModel;
		init();
		//System.out.println("Object created as a child of the model");
		//System.out.println("Object (" + this + ") converted in " + (System.currentTimeMillis() - startTime) + " milliseconds");
	}

	/**
	 * Initialises the rest of the object
	 */
	private void init() throws InvalidDataException, ResourceLoadFailedException
	{

		//long startTime = System.currentTimeMillis();
		float[][] vertData = object.getVerticies();

		//System.out.println("Object " + this + " Got vertecies is " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//startTime = System.currentTimeMillis();

		//Set the roation part of our transform
		transform.set(new Matrix3f(object.getRotation()));
		//System.out.println("Rotation is \n\r" + new Matrix3f(object.getRotation()));
		//set the location part of our transform
		transform.setTranslation(new Vector3f(object.getLocation()));
		//System.out.println("Translation is \n\r" + new Vector3f(object.getLocation()));
		//copy it to the adjusted transform
		adjustedTransform = new Transform3D(transform);
		//System.out.println("Uadjusted transform \n\r" + adjustedTransform);
		//adjust it with opur parents transform

		//System.out.println("Object " + this + " Set transform in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//startTime = System.currentTimeMillis();

		if (parentObject != null)
		{
			//System.out.println("Parents transform is \n\r" + parentObject.getAdjustedTransform());
			adjustedTransform.mul(parentObject.getAdjustedTransform());
		}
		//System.out.println("Adjusted transform \n\r" + adjustedTransform);

		//System.out.println("Object " + this + " Adjusted transform in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//startTime = System.currentTimeMillis();

		if (parentObject != null)
		{
			texturePath = parentObject.getTexturePath();
		}
		else
		{
			texturePath = theModel.getTexturePath();
		}
		if (!(object.getTextureName() == null))
		{
			try
			{
				textureURL = new URL(texturePath + object.getTextureName());
			}
			catch (MalformedURLException e)
			{
				throw new InvalidDataException("Texture URL \"" + texturePath + object.getTextureName() + "\" is invalid");
			}

			// If we call this here then the texture will be cached for when we
			// really need it
			TextureResourcePool.getTexture(textureURL);
		}

		//System.out.println("Object " + this + " Got texture in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//startTime = System.currentTimeMillis();

		Iterator<org.newdawn.loaders.ac3d.AC3DObject> objIt = (object.getKids()).iterator();
		while (objIt.hasNext())
		{
			kids.add(new AC3DObject(objIt.next(), this));
		}

		//System.out.println("Object " + this + " Sorted kids in in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//startTime = System.currentTimeMillis();

		//System.out.println("Object adjusting " + vertData.length + " verts with transform \n\r" + adjustedTransform);
		adjustedVerts = new Point3f[vertData.length];
		for (int i = 0; i < adjustedVerts.length; i++)
		{
			adjustedVerts[i] = new Point3f(vertData[i][0], vertData[i][1], vertData[i][2]);
			//System.out.println("unadjusted vert[" + i + "] is " + adjustedVerts[i].toString());

			adjustedTransform.transform(adjustedVerts[i]);
			//System.out.println("adjusted vert[" + i + "] is " + adjustedVerts[i].toString());
		}

		// KEV
		generateSmoothedNormalArray(object);

		//System.out.println("Object " + this + " Adjusted vertecies in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		//startTime = System.currentTimeMillis();

		//set up the surfcaes

		ArrayList<org.newdawn.loaders.ac3d.AC3DSurface> realSurfaces = object.getSurfaces();
		Iterator<org.newdawn.loaders.ac3d.AC3DSurface> surfIt = realSurfaces.iterator();
		//	int surfCount = 0;
		while (surfIt.hasNext())
		{
			org.newdawn.loaders.ac3d.AC3DSurface realSurf;
			realSurf = surfIt.next();
			surfaces.add(new AC3DSurface(realSurf, this));
			//	surfCount++;
		}
		//System.out.println("Object " + this + " converted " + surfCount + " surfaces in " + (System.currentTimeMillis() - startTime) + " milliseconds");
	}

	/**
	 * KEV - Generates the smoothed normal array based on building a geometry array
	 * of all the sub surfaces and smothing using a normal generator.
	 *
	 * @param object The object being modelled
	 */
	private void generateSmoothedNormalArray(org.newdawn.loaders.ac3d.AC3DObject object2)
	{
		ArrayList<org.newdawn.loaders.ac3d.AC3DSurface> realSurfaces = object2.getSurfaces();
		int[] strips = new int[realSurfaces.size()];
		int[][] coordTemp = new int[realSurfaces.size()][];
		Point3f[] coords;

		int totalVerts = 0;
		for (int i = 0; i < realSurfaces.size(); i++)
		{
			org.newdawn.loaders.ac3d.AC3DSurface current = realSurfaces.get(i);

			int[] vertexReferences = current.getVertexReferences();
			strips[i] = vertexReferences.length;

			coordTemp[i] = vertexReferences;
			totalVerts += vertexReferences.length;
		}

		int c = 0;
		coords = new Point3f[totalVerts];
		for (int i = 0; i < realSurfaces.size(); i++)
		{
			for (int j = 0; j < coordTemp[i].length; j++)
			{
				coords[c] = adjustedVerts[coordTemp[i][j]];
				c++;
			}
		}

		// KEV - now we have a list of our vertices, form a set of normals for smoothing
		GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
		geoInfo.setCoordinates(coords);
		geoInfo.setStripCounts(strips);
		new NormalGenerator(AC3DSurface.SMOOTH_CREASE_ANGLE).generateNormals(geoInfo);

		smoothedNormals = new Vector3f[adjustedVerts.length];
		Vector3f[] duplicateNormals = geoInfo.getNormals();
		int[] normalIndex = geoInfo.getNormalIndices();

		/*System.out.println("Coords put in :"+coords.length);
		System.out.println("Normal Indicies :"+normalIndex.length);
		System.out.println("Normals pulled out : "+duplicateNormals.length);
		System.out.println("AdjustedVertices : "+adjustedVerts.length);
		System.out.println("TotalVerts : "+totalVerts);*/

		c = 0;
		for (int i = 0; i < realSurfaces.size(); i++)
		{
			for (int j = 0; j < coordTemp[i].length; j++)
			{
				smoothedNormals[coordTemp[i][j]] = duplicateNormals[normalIndex[c]];
				c++;
			}
		}
	}

	/**
	 * Gets the adjustedTransform (the transfrom of this object after being
	 * transformed by it's parents adjusted transform (if any)
	 *
	 * @return The adjustedTransform
	 */
	public Transform3D getAdjustedTransform()
	{
		return new Transform3D(adjustedTransform);
	}

	/**
	 * Gets the verticies as Point3f[]
	 *
	 * @return The verticies
	 */
	public Point3f[] getAdjustedVertecies()
	{
		if (adjustedVerts == null)
		{
			System.out.println("adjustedVerts is null");
		}
		return adjustedVerts;
	}

	/**
	 * KEV
	 * Get the smoothed normals as Vector3f[]
	 *
	 * @return The smoothed normals based on the complete object
	 */
	public Vector3f[] getSmoothedNormals()
	{
		if (smoothedNormals == null)
		{
			System.out.println("smoothedNormals is null");
		}
		return smoothedNormals;
	}

	/**
	 * proxy method
	 *
	 * @return The real AC3DObject data
	 */
	public float getTextureOffsetX()
	{
		return object.getTextureOffsetX();
	}

	/**
	 * proxy method
	 *
	 * @return The real AC3DObject data
	 */
	public float getTextureOffsetY()
	{
		return object.getTextureOffsetY();
	}

	/**
	 * proxy method
	 *
	 * @return The real AC3DObject data
	 */
	public float getTextureRepeatX()
	{
		return object.getTextureRepeatX();
	}

	/**
	 * proxy method
	 *
	 * @return The real AC3DObject data
	 */
	public float getTextureRepeatY()
	{
		return object.getTextureRepeatY();
	}

	/**
	 * Returns the texture path
	 *
	 * @return The ralativeTexturePath
	 */
	public String getTexturePath()
	{
		return texturePath;
	}

	/**
	 * Adds this nodes shape tree to the provided tree
	 * 
	 * @param tree The tree to add our tree too
	 */
	public void addShapeTreeToTree(ShapeTree tree)
	{
		ShapeTree subTree = new ShapeTree();

		addSurfacesToTree(subTree, false);
		tree.addTree(subTree);
	}

	/**
	 * Adds our surfaces to this tree
	 *
	 * @param tree The tree to add the surffaces too
	 */
	public void addSurfacesToTree(ShapeTree tree)
	{
		addSurfacesToTree(tree, true);
	}

	/**
	 * Adds our surfaces to this tree
	 *
	 * @param tree The tree to add the surffaces too
	 * @param optimise Should we optimise the tree?
	 */
	public void addSurfacesToTree(ShapeTree tree, boolean optimise)
	{
		Iterator<AC3DObject> objIt = kids.iterator();
		Iterator<AC3DSurface> surfIt = surfaces.iterator();

		while (objIt.hasNext())
		{
			if (optimise)
			{
				objIt.next().addSurfacesToTree(tree);
			}
			else
			{
				objIt.next().addShapeTreeToTree(tree);
			}
		}

		while (surfIt.hasNext())
		{
			String textureURLasString = null;
			if (textureURL != null)
			{
				textureURLasString = textureURL.toString();
			}
			tree.addSurface(surfIt.next(), textureURLasString);
		}
	}
}
