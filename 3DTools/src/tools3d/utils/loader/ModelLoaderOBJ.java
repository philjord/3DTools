package tools3d.utils.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Point3d;
import javax.vecmath.Vector4f;

import tools3d.geography.LargeGeometry;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickTool;

// simple class for preloading models on startup
public class ModelLoaderOBJ
{
	private static Hashtable<String, BranchGroup> loadedModels = new Hashtable<String, BranchGroup>();

	private static Hashtable<String, LargeGeometry> loadedGeometries = new Hashtable<String, LargeGeometry>();

	private static void loadModelIntoStore(String fileToLoad, int flags, String basePath)
	{
		File file = new File(fileToLoad);
		BranchGroup modelBranch = null;
		// let's do a qucik pre cache check

		double creaseAngle = 60.0;

		long startTime = System.currentTimeMillis();

		ObjectFile f = new ObjectFile(flags, (float) (creaseAngle * Math.PI / 180.0));
		f.setBasePath(basePath);
		Scene s = null;

		try
		{
			s = f.load(new BufferedReader(new FileReader(file)));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ParsingErrorException e)
		{
			e.printStackTrace();
		}
		catch (IncorrectFormatException e)
		{
			e.printStackTrace();
		}

		modelBranch = s.getSceneGroup();
		for (int i = 0; i < modelBranch.numChildren(); i++)
		{
			PickTool.setCapabilities(modelBranch.getChild(i), PickTool.INTERSECT_FULL);
		}

		long timeTaken = System.currentTimeMillis() - startTime;
		if (timeTaken > 500)
			System.out.println(fileToLoad + " in " + timeTaken + " ms");

		loadedModels.put(file.getPath(), modelBranch);
	}

	public static BranchGroup getTerrainModel(String fileToLoad, String textureFile)
	{
		File file = new File(fileToLoad);
		if (!loadedModels.containsKey(file.getPath()))
		{
			// hasn't been pre loaded, lets load it now
			int flags = ObjectFile.REVERSE;
			flags |= ObjectFile.TRIANGULATE;
			flags |= ObjectFile.STRIPIFY;
			loadModelIntoStore(file.getPath(), flags, "");
		}

		BranchGroup model = loadedModels.get(file.getPath());

		if (model == null)
		{
			// nope it's no good we'll just have to return a dummy
			System.out.println("obj Some silly bugger wants " + file.getPath() + " but the loader couldn't load it. ");
			model = new BranchGroup();
		}
		else if (textureFile != null && !textureFile.equals(""))
		{
			Shape3D shape = ((Shape3D) model.getChild(0));

			BoundingBox bb = (BoundingBox) shape.getBounds();
			Point3d l = new Point3d();
			bb.getLower(l);
			Point3d u = new Point3d();
			bb.getUpper(u);

			double xExtent = u.x - l.x;
			double zExtent = u.z - l.z;

			// little crap test to set a texture
			Appearance app = shape.getAppearance();

			Vector4f planeS = new Vector4f((float) (1 / xExtent), 0, 0, 0);
			Vector4f planeT = new Vector4f(0, 0, (float) (1 / zExtent), 0);
			TexCoordGeneration texGen = new TexCoordGeneration();
			texGen.setPlaneS(planeS);
			texGen.setPlaneT(planeT);
			app.setTexCoordGeneration(texGen);

			TextureLoader tex = new TextureLoader(textureFile, "RGB", null);
			app.setTexture(tex.getTexture());

			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.MODULATE);
			app.setTextureAttributes(ta);

			app.getMaterial().setShininess(0);
		}

		return model;
	}

	public static LargeGeometry getLargeGeometry(String fileToLoad)
	{

		File file = new File(fileToLoad);
		if (loadedGeometries.containsKey(file.getPath()))
		{
			return loadedGeometries.get(file.getPath());
		}
		else
		{
			LargeGeometry largeGeometry;

			Shape3D groundShape = ((Shape3D) ModelLoaderOBJ.getTerrainModel(fileToLoad, "").getChild(0));
			largeGeometry = new LargeGeometry(40, 10, 40, groundShape);

			loadedGeometries.put(file.getPath(), largeGeometry);
			return largeGeometry;
		}
	}

}
