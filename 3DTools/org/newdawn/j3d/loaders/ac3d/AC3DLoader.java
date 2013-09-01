package org.newdawn.j3d.loaders.ac3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.newdawn.loaders.FileFormatException;
import org.newdawn.loaders.InvalidDataException;
import org.newdawn.loaders.resourcepools.ResourceLoadFailedException;

/**
 * This is a wrapper around around the org.newdawn.loaders.ac3d.AC3DLoader class,
 * that allows the user to not worry about loading the object and then generation
 * the j3d parts.
 *
 * Main class for the Java3D AC3D loader
 * See <a href="http://www.newdawnsoftware.com">New dawn software's</a> resource page
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.3 $
 */
public class AC3DLoader
{

	/** The URL of the model */
	private URL modelURL;

	/** The base texture url */
	private String texturePath;

	/** The node factory used to build nodes */
	//private NodeFactory nodeFactory;

	/** 
	 * Creates new AC3DLoader from the given model
	 *
	 * @param modelLocation The location of the model
	 * @param relativeTexturePath The path to appent to the base path for the textures
	 */
	private AC3DLoader(URL modelLocation, String relativeTexturePath)
	{
		String tempURLString = modelLocation.toString();
		String baseURL;
		this.modelURL = modelLocation;

		//String file = tempURLString.substring(tempURLString.lastIndexOf("/") + 1);
		//System.out.println("File: " + file);
		baseURL = tempURLString.substring(0, tempURLString.lastIndexOf("/") + 1);
		//System.out.println("baseURL: " + baseURL);
		if (relativeTexturePath != null)
		{
			if (relativeTexturePath.charAt(relativeTexturePath.length() - 1) != '/')
			{
				relativeTexturePath = relativeTexturePath + "/";
			}
			this.texturePath = baseURL + relativeTexturePath;
		}
		else
		{
			this.texturePath = baseURL;
		}
		//System.out.println("texturePath: " + texturePath);
	}

	/**
	 * This is an entry point, pass in the model file name and out pops a model,
	 * in theory :), this one takes the filename and converts it to a url for you
	 *
	 * @return The model containing the fully textured and materialed
	 *      object
	 * @param modelFilename The models file name
	 * @throws IOException Thrown if there was an IO error
	 * @throws FileFormatException Thrown if the file is not formatted correctly to the AC3D standards
	 * @throws InvalidDataException Thrown if the some data in the file is invalid
	 * @throws ResourceLoadFailedException Thrown if a resouce needed for the model could not be loaded
	 */
	public static AC3DModel load(String modelFilename) throws IOException, FileFormatException, InvalidDataException,
			ResourceLoadFailedException
	{
		URL url = ClassLoader.getSystemResource(modelFilename);

		if (url == null)
		{
			throw new FileNotFoundException("Could not find file: " + modelFilename);
		}

		return new AC3DLoader(url, null).load();
	}

	/**
	 * This is an entry point, pass in the model file name and out pops a model,
	 * in theory :)
	 *
	 * @return The model containing the fully textured and materialed
	 *     object
	 * @param modelLocation The URL for the model
	 * @throws IOException Thrown if there was an IO error
	 * @throws FileFormatException Thrown if the file is not formatted correctly to the AC3D standards
	 * @throws InvalidDataException Thrown if the some data in the file is invalid
	 * @throws ResourceLoadFailedException Thrown if a resouce needed for the model could not be loaded
	 */
	public static AC3DModel load(URL modelLocation) throws IOException, FileFormatException, InvalidDataException,
			ResourceLoadFailedException
	{
		return new AC3DLoader(modelLocation, null).load();
	}

	/**
	 * This is an entry point, pass in the model file name and out pops a model,
	 * in theory :)
	 *
	 * @return The model containing the fully textured and materialed
	 *     object
	 * @param texturePath The path (relative the the model) for the textures
	 * @param modelFilename The models file name
	 * @throws IOException Thrown if there was an IO error
	 * @throws FileFormatException Thrown if the file is not formatted correctly to the AC3D standards
	 * @throws InvalidDataException Thrown if the some data in the file is invalid
	 * @throws ResourceLoadFailedException Thrown if a resouce needed for the model could not be loaded
	 */
	public static AC3DModel load(String modelFilename, String texturePath) throws IOException, FileFormatException, InvalidDataException,
			ResourceLoadFailedException
	{
		URL url = ClassLoader.getSystemResource(modelFilename);

		if (url == null)
		{
			throw new FileNotFoundException("Could not find file: " + modelFilename);
		}

		return new AC3DLoader(url, texturePath).load();
	}

	/**
	 * This is an entry point, pass in the model file name and out pops a model,
	 * in theory :)
	 *
	 * @return The model containing the fully textured and materialed
	 *    object
	 * @param modelLocation The URL for the model
	 * @param texturePath The path (relative to the model) where the textures are stored
	 * @throws IOException Thrown if there was an IO error
	 * @throws FileFormatException Thrown if the file is not formatted correctly to the AC3D standards
	 * @throws InvalidDataException Thrown if the some data in the file is invalid
	 * @throws ResourceLoadFailedException Thrown if a resouce needed for the model could not be loaded
	 */
	public static AC3DModel load(URL modelLocation, String texturePath) throws IOException, FileFormatException, InvalidDataException,
			ResourceLoadFailedException
	{
		return new AC3DLoader(modelLocation, texturePath).load();
	}

	/**
	 * The real loading method
	 *
	 * @return The model containing the fully textured and materialed 
	 *         object 
	 */
	private AC3DModel load() throws IOException, FileFormatException, InvalidDataException, ResourceLoadFailedException
	{
		//long startTime = System.currentTimeMillis();
		AC3DModel theModel = new AC3DModel(org.newdawn.loaders.ac3d.AC3DLoader.load(modelURL), texturePath);
		//System.out.println(modelURL + " loaded and converted in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		return theModel;
	}

	/**
	 * Test method
	 *
	 * @param args A bunch of args
	 */
	public static void main(String[] args)
	{
		//System.out.println("Trying to load " + args[1]);

		try
		{
			if (args[0].equals("URL"))
			{
				load(new URL(args[1]));
			}
			else
			{
				load(args[1]);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
}
