package org.newdawn.loaders.ac3d;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.newdawn.loaders.FileFormatException;

/**
 * Parses the ac3d data file into a java model
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.3 $
 */
public class AC3DLoader
{

	/** The URL of the model */
	private URL modelURL;

	/** The reader used to read the lines from the file*/
	private BufferedReader reader;

	/** The base url, used to prepend for getting textures etc */
	//private String baseURL;

	/** The model we are loading */
	private AC3DModel model = new AC3DModel();

	/** 
	 * Creates new AC3DLoader from the given model
	 *
	 * @param modelLocation The location of the model
	 */
	private AC3DLoader(URL modelLocation)
	{
		//String tempURLString = modelLocation.toString();
		this.modelURL = modelLocation;

		//String file = tempURLString.substring(tempURLString.lastIndexOf("/") + 1);
		//System.out.println("File: " + file);
		//baseURL = tempURLString.substring(0, tempURLString.lastIndexOf("/") + 1);
		//System.out.println("baseURL: " + baseURL);
	}

	/**
	 * This is an entry point, pass in the model file name and out pops a model,
	 * in theory :), this one takes the filename and converts it to a url for you
	 *
	 * @return The model
	 * @param modelFilename The models file name
	 * @throws IOException Thrown if there is an IO Error
	 * @throws FileFormatException Thrown if the file does not match the AC3D specification
	 */
	public static AC3DModel load(String modelFilename) throws IOException, FileFormatException
	{
		URL url = ClassLoader.getSystemResource(modelFilename);

		if (url == null)
		{
			throw new FileNotFoundException("Could not find file: " + modelFilename);
		}

		return new AC3DLoader(url).load();
	}

	/**
	 * This is the main load method, pass in the model url and out pops a
	 * nicely optimised model, in theory :)
	 *
	 * @return The model containing the fully textured and materialed
	 *        object
	 * @param modelLocation The URL of the model
	 * @throws IOException Thrown if there is an IO error
	 * @throws FileFormatException Thrown if the file does not match the AC3D specifcation
	 */
	public static AC3DModel load(URL modelLocation) throws IOException, FileFormatException
	{
		return new AC3DLoader(modelLocation).load();
	}

	/**
	 * The real loading method
	 *
	 * @return The model containing the fully textured and materialed 
	 *         object 
	 */
	private AC3DModel load() throws IOException, FileFormatException
	{
		String line = new String();
		reader = new BufferedReader(new InputStreamReader(modelURL.openStream()));
		int materialCount = 0;
		//long startTime = System.currentTimeMillis();

		model.setHeader(loadHeader());
		while (line != null)
		{
			String token;
			line = reader.readLine();
			if (line != null)
			{
				//System.out.println("Read: " + line);
				token = new StringTokenizer(line, " ").nextToken();
				//System.out.println("Token is: " + token);
				if (token.equals("MATERIAL"))
				{
					model.addMaterial(loadMaterial(line, materialCount++));
				}
				else if (token.equals("OBJECT"))
				{
					model.addObject(loadObject(line, reader));
				}
			}
			else
			{
				//System.out.println("Done");
			}
		}
		//System.out.println(modelURL + " parsed in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		return model;
	}

	/**
	 * Reads the header block
	 */
	private AC3DHeader loadHeader() throws IOException, FileFormatException
	{
		String header = reader.readLine();
		String filetype = header.substring(0, 4);
		String versionText = header.substring(4);
		//int formatVersion = header.charAt(4) - 87;
		int formatVersion = Integer.parseInt(versionText, 16);
		if (!(header.substring(0, 4).equals("AC3D")))
		{
			System.out.println("File is not an AC3D file");
			System.out.println("Header read: " + header);
			throw new FileFormatException("File is not an AC3D file");
		}
		//System.out.println("Found AC3D file of format version " + formatVersion);
		return new AC3DHeader(filetype, formatVersion);
	}

	/**
	 * Loads an AC3DMaterial from the data
	 *
	 * @param data The data from the file
	 * @param materialIndex The index of this material
	 * @return The <CODE>AC3DMaterial</CODE>
	 */
	public AC3DMaterial loadMaterial(String data, int materialIndex)
	{
		// The name of the material
		String name;
		// The colour of the material
		Color colour;
		// The colour of ambient light reflected
		Color amb;
		// The emited colour of this material
		Color emis;
		// The speculative colour of the material
		Color spec;
		// The shinyness
		float shininess;
		// The translucancy of the material
		float translucency;

		float r, g, b;
		StringTokenizer tokenizer = new StringTokenizer(data, " ");

		tokenizer.nextToken(); //Material

		name = tokenizer.nextToken();
		tokenizer.nextToken(); //rgb

		r = Float.parseFloat(tokenizer.nextToken());
		g = Float.parseFloat(tokenizer.nextToken());
		b = Float.parseFloat(tokenizer.nextToken());
		colour = new Color(r, g, b);

		tokenizer.nextToken();//amb
		r = Float.parseFloat(tokenizer.nextToken());
		g = Float.parseFloat(tokenizer.nextToken());
		b = Float.parseFloat(tokenizer.nextToken());
		amb = new Color(r, g, b);

		tokenizer.nextToken();//emis
		r = Float.parseFloat(tokenizer.nextToken());
		g = Float.parseFloat(tokenizer.nextToken());
		b = Float.parseFloat(tokenizer.nextToken());
		emis = new Color(r, g, b);

		tokenizer.nextToken();//spec
		r = Float.parseFloat(tokenizer.nextToken());
		g = Float.parseFloat(tokenizer.nextToken());
		b = Float.parseFloat(tokenizer.nextToken());
		spec = new Color(r, g, b);

		tokenizer.nextToken();//shi
		shininess = Float.parseFloat(tokenizer.nextToken());

		tokenizer.nextToken();//trans
		translucency = Float.parseFloat(tokenizer.nextToken());
		//System.out.println("Read material: " + name + " " + colour + " " + amb + " " + emis + " " + spec + " " + shinyness + " " + translucency);

		return new AC3DMaterial(materialIndex, name, colour, amb, emis, spec, shininess, translucency);
	}

	/**
	 * Loads an AC3DObject from the file
	 *
	 * @param reader The reader to read from
	 * @throws IOException Thrown if there is an IO Error
	 * @throws FileFormatException Thrown if the file does not match the AC3D specification
	 * @return The <CODE>AC3DObject</CODE>
	 */
	public AC3DObject loadObject(BufferedReader reader2) throws IOException, FileFormatException
	{
		String line = reader2.readLine();
		return loadObject(line, reader2);
	}

	/**
	 * Loads an AC3DObject from the file
	 *
	 * @param line The first line of this tag
	 * @param reader The reader to read from
	 * @throws IOException Thrown if there is an IO Error
	 * @throws FileFormatException Thrown if the file does not match the AC3D specification
	 * @return The <CODE>AC3DObject</CODE>
	 */
	public AC3DObject loadObject(String line, BufferedReader reader2) throws IOException, FileFormatException
	{
		// The type of the object
		int type;
		// The object name
		String name = null;
		// The texture name
		String textureName = null;
		// The rotation matrix of this object
		float[] rotation =
		{ 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
		// The location vector of this object
		float[] location = new float[3];
		// The objects verticies
		float[][] verts = new float[0][0];
		// Texture repeat values
		float textureRepeatX = 1, textureRepeatY = 1;
		// Texture offset values
		float textureOffsetx = 0f, textureOffsety = 0f;
		// The object we are creating
		AC3DObject object;
		// temporary stor of surfaces
		ArrayList<AC3DSurface> tempSurfaces = new ArrayList<AC3DSurface>();

		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		String token, stringType;

		tokenizer.nextToken();
		stringType = token = tokenizer.nextToken();
		if (stringType.equals("world"))
		{
			type = AC3DObject.TYPE_WORLD;
		}
		else if (stringType.equals("poly"))
		{
			type = AC3DObject.TYPE_POLY;
		}
		else if (stringType.equals("group"))
		{
			type = AC3DObject.TYPE_GROUP;
		}
		else
		{
			throw new FileFormatException("Object type \"" + stringType + "\" is not valid");
		}
		//System.out.println("Object type: " + type);

		while (!(token.equals("kids")))
		{
			line = reader2.readLine();
			tokenizer = new StringTokenizer(line);
			token = tokenizer.nextToken();
			if (token.equals("name"))
			{
				name = tokenizer.nextToken();
				//System.out.println("name: " + name);
			}
			else if (token.equals("data"))
			{
				// I think this is just one line, the data block is a single line (maybe)
				line = reader2.readLine();
				//System.out.println("data tags unsupported");
			}
			else if (token.equals("texture"))
			{
				//Read the first quote
				tokenizer.nextToken("\"");
				//read up to the second quote
				textureName = tokenizer.nextToken("\"");
			}
			else if (token.equals("texoff"))
			{
				textureOffsetx = Float.parseFloat(tokenizer.nextToken());
				textureOffsety = Float.parseFloat(tokenizer.nextToken());
			}
			else if (token.equals("texrep"))
			{
				textureRepeatX = Float.parseFloat(tokenizer.nextToken());
				textureRepeatY = Float.parseFloat(tokenizer.nextToken());
				//System.out.println("repy: " + repy + " repx: " + repx);
			}
			else if (token.equals("rot"))
			{
				//System.out.println("rot tag");
				for (int i = 0; i < 9; i++)
				{
					rotation[i] = Float.parseFloat(tokenizer.nextToken());
				}
			}
			else if (token.equals("loc"))
			{
				//System.out.println("loc tag");
				for (int i = 0; i < 3; i++)
				{
					location[i] = Float.parseFloat(tokenizer.nextToken());
				}
			}
			else if (token.equals("url"))
			{
				System.out.println("url tag unsuported");
			}
			else if (token.equals("numvert"))
			{
				int numvert = Integer.parseInt(tokenizer.nextToken());
				verts = new float[numvert][3];

				for (int i = 0; i < numvert; i++)
				{
					line = reader2.readLine();
					tokenizer = new StringTokenizer(line, " ");
					verts[i][0] = Float.parseFloat(tokenizer.nextToken());
					verts[i][1] = Float.parseFloat(tokenizer.nextToken());
					verts[i][2] = Float.parseFloat(tokenizer.nextToken());
				}
			}
			else if (token.equals("numsurf"))
			{
				int numsurf = Integer.parseInt(tokenizer.nextToken());
				//System.out.println("Reading " + numsurf + " surfaces");
				for (int i = 0; i < numsurf; i++)
				{
					//System.out.println("Reading surface " + i);
					AC3DSurface surface = loadSurface(reader2);

					// check we are a line, or that we have at least 3 vertecies
					// as a poly with 3 vertecies is broked
					if ((surface.isLine()) || (surface.getVertexReferenceCount() >= 3))
					{
						tempSurfaces.add(surface);
					}
					else
					{
						//System.out.println("Read broken surface");
						numsurf--;
						i--;
					}
					//System.out.println("Loaded surface " + i);
				}
			}
		}

		//at this point we should have all we need to create the object, we can
		//add the kids later
		object = new AC3DObject(type, name, textureName, rotation, location, verts, textureRepeatX, textureRepeatY, textureOffsetx,
				textureOffsety);
		Iterator<AC3DSurface> surfIt = tempSurfaces.iterator();
		while (surfIt.hasNext())
		{
			object.addSurface(surfIt.next());
		}

		//System.out.println("token is " + token);

		// there is always one, and only onw kids token
		if (token.equals("kids"))
		{
			int numKids = Integer.parseInt(tokenizer.nextToken());

			//System.out.println("Adding " + numKids + " to " + object.getName() + object);
			for (int i = 0; i < numKids; i++)
			{
				object.addObject(loadObject(reader2));
			}
		}
		else
		{
			//Something is wrong with the file, the file spec says the the
			//surfaces and kids are the last tags, we have found something 
			// else
			throw new FileFormatException("\"" + token.toString() + "\"" + " found where only a 'kids' should be");
		}
		return object;
	}

	/**
	 * Loads an AC3DSurface from the file
	 *
	 * @param reader The reader to use
	 * @throws IOException Thrown if an IO error happens
	 * @throws FileFormatException Thrown if the file does not match the AC3D specification
	 * @return The <CODE>AC3DSurface</CODE>
	 */
	public AC3DSurface loadSurface(BufferedReader reader2) throws IOException, FileFormatException
	{
		// The type of this surface
		int type;
		// is two sided?
		boolean twoSided;
		// is shaded
		boolean shaded;
		// materials index
		int material = -1;
		// The vertecies on this surface 
		int[] surfVerts;
		// The texture coordiantes for each vertex
		float[][] textCoords;

		String token;
		String line = reader2.readLine();
		StringTokenizer tokenizer = new StringTokenizer(line, " ");

		//System.out.println("Surface: " + line);

		tokenizer.nextToken();
		int flags = Integer.parseInt(tokenizer.nextToken().substring(2), 16);
		type = (flags & 0x0f);
		//System.out.println("Type is: " + type);
		shaded = ((flags >> 4) & 1) == 1;
		//System.out.println("Shaded: " + shaded);
		twoSided = ((flags >> 5) & 1) == 1;
		//System.out.println("Two sided: " + twoSided);

		//read next token
		line = reader2.readLine();
		tokenizer = new StringTokenizer(line, " ");
		token = tokenizer.nextToken();
		if (token.equals("mat"))
		{
			material = Integer.parseInt(tokenizer.nextToken());
			//read next token
			line = reader2.readLine();
			tokenizer = new StringTokenizer(line, " ");
			token = tokenizer.nextToken();
		}
		int numRefs = Integer.parseInt(tokenizer.nextToken());

		surfVerts = new int[numRefs];
		textCoords = new float[numRefs][2];
		for (int i = 0; i < numRefs; i++)
		{
			int vertRef;
			line = reader2.readLine();
			tokenizer = new StringTokenizer(line, " ");
			token = tokenizer.nextToken();
			vertRef = Integer.parseInt(token);
			surfVerts[i] = vertRef;
			textCoords[i][0] = Float.parseFloat(tokenizer.nextToken());
			textCoords[i][1] = Float.parseFloat(tokenizer.nextToken());
		}

		return new AC3DSurface(type, twoSided, shaded, material, surfVerts, textCoords);
	}

	/**
	 * Test method
	 *
	 * @param args A bunch of args
	 */
	public static void main(String[] args)
	{
		System.out.println("Trying to load " + args[0]);

		try
		{
			if (args[0].equals("URL"))
			{
				load(new URL(args[1]));
			}
			else
			{
				load(args[0]);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

}
