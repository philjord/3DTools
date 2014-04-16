package tools3d.geography.gen;

import java.awt.image.BufferedImage;

import javax.vecmath.Color3f;

import org.j3d.geom.GeometryData;
import org.j3d.geom.UnsupportedTypeException;
import org.j3d.geom.terrain.ColorRampGenerator;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * @author main
 *
 */
public class GroundGeometryDataCreator
{
	public static GeometryData create(int xSize, int zSize, String seedheightImageName)
	{
		GeometryData terrainData = new GeometryData();
		try
		{
			TextureLoader tex = new TextureLoader(seedheightImageName, "RGB", null);
			BufferedImage img = tex.getImage().getImage();
			HeightDataCreator hdc = new HeightDataCreator(0, 10);
			float[][] seedTerrain = hdc.createHeightField(img);

			// Prepare the data container

			terrainData.geometryType = GeometryData.TRIANGLE_STRIPS;
			terrainData.geometryComponents = GeometryData.TEXTURE_2D_DATA | GeometryData.NORMAL_DATA;

			// Setup the colour generator
			float[] heights =
			{ -5, 0, 10, 20 };
			Color3f[] heightColors =
			{ new Color3f(0, 0, 0.1f), new Color3f(0.4f, 0, 0), new Color3f(0.3f, 0.3f, 0.1f), new Color3f(0.2f, 0.2f, 0.2f), };
			ColorRampGenerator colorGenerator = new ColorRampGenerator(heights, heightColors);

			// Setup the height jitterer 
			FractalTerrainGenerator terrainGenerator = new FractalTerrainGenerator(xSize, zSize, 0.2f, true, -2, 2, 1.05f, 0, seedTerrain);

			// Createthe height and color data
			terrainGenerator.generate(terrainData);
			colorGenerator.generate(terrainData);

			terrainData.numStrips = terrainData.stripCounts.length;
		}
		catch (UnsupportedTypeException ute)
		{
			System.out.println("Geometry type is not supported");
			return null;
		}
		return terrainData;

	}
}
