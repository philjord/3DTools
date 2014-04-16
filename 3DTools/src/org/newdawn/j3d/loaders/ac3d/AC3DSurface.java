package org.newdawn.j3d.loaders.ac3d;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

/**
 * Contains all the information for a surface
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.5 $
 */
public class AC3DSurface
{

	/** Crease angle definitions. */
	public static final double FLAT_CREASE_ANGLE = 0.0, SMOOTH_CREASE_ANGLE = Math.PI;

	/** The real AC3DSurface */
	private org.newdawn.loaders.ac3d.AC3DSurface surface;

	/** The AC3DObject that is this surfaces parent object */
	private AC3DObject parentObject;

	/** The constructed geometry */
	private Geometry geometry = null;

	/**
	 * Creates new j3d AC3DSurface and builds the geometry
	 *
	 * @param surface The AC3DSurface
	 * @param parentObject The AC3DObject that is this surfaces parent object
	 */
	public AC3DSurface(org.newdawn.loaders.ac3d.AC3DSurface surface, AC3DObject parentObject)
	{
		this.surface = surface;
		this.parentObject = parentObject;
		//rebuildGeometry();
	}

	/**
	 * Builds the geometry
	 */
	public void rebuildGeometry()
	{
		GeometryInfo geoInfo;
		Point3f[] verts;
		TexCoord2f[] textCoords;
		float[][] surfTextCoords = surface.getTextureCoordinates();
		int[] vertexReferences = surface.getVertexReferences();
		int coordNum = vertexReferences.length;
		float textureOffsetX = parentObject.getTextureOffsetX();
		float textureOffsetY = parentObject.getTextureOffsetY();
		float textureRepeatX = parentObject.getTextureRepeatX();
		float textureRepeatY = parentObject.getTextureRepeatY();
		Point3f[] surfVerts = parentObject.getAdjustedVertecies();

		//System.out.println("Got " + surfVerts.length + " adjusted vertecies for " + coordNum + " vertecies in this surface");

		// closed lines have 1 extra vertex (a copy of the first vetex is the 
		// last vertex) so make the vertex array 1 bigger
		if (surface.getType() == org.newdawn.loaders.ac3d.AC3DSurface.CLOSED_LINE)
		{
			verts = new Point3f[coordNum + 1];
		}
		else
		{
			verts = new Point3f[coordNum];
		}

		if (surfVerts == null)
		{
			System.out.println("surfVerts is null");
		}

		for (int i = 0; i < coordNum; i++)
		{
			verts[i] = surfVerts[vertexReferences[i]];
			//System.out.println("geom vertex " + i + " set to " + verts[i].toString());
		}

		// copy over that last vertex with the first vetex for a closed line
		if (surface.getType() == org.newdawn.loaders.ac3d.AC3DSurface.CLOSED_LINE)
		{
			verts[coordNum] = surfVerts[0];
		}

		textCoords = new TexCoord2f[surfTextCoords.length];
		//System.out.println("Dealing with " + surfTextCoords.length + " texture ccoordinates");
		for (int i = 0; i < surfTextCoords.length; i++)
		{
			textCoords[i] = new TexCoord2f(
			//(surfTextCoords[i][0] + textureOffsetX)*textureRepeatX,
			//(surfTextCoords[i][1] + textureOffsetY)*textureRepeatY);
					(surfTextCoords[i][0] * textureRepeatX) + textureOffsetX, (surfTextCoords[i][1] * textureRepeatY) + textureOffsetY);
		}

		if (isLine())
		{
			//System.out.println("Surface is a line");
			LineArray line;

			line = new LineArray(coordNum, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
			line.setCoordinates(0, verts);
			//line.setTextureCoordinateParams(1,2);
			line.setTextureCoordinates(0, 0, textCoords);
			geometry = line;
		}
		else
		{
			//System.out.println("Surface is not a line");
			geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

			//System.out.println("Adding " + verts.length + " vertecies to the geometry for surface");
			geoInfo.setCoordinates(verts);
			geoInfo.setTextureCoordinateParams(1, 2);
			geoInfo.setTextureCoordinates(0, textCoords);
			//This sets up the number of vertecies in each strip, in our case, one strip with all the vertecies
			geoInfo.setStripCounts(new int[]
			{ coordNum });
			// generate normals
			if (surface.isShaded())
			{
				// KEV - Instead of generating the smoothed normals here, we'll use the 
				// generated one from the parent object
				//new NormalGenerator(SMOOTH_CREASE_ANGLE).generateNormals(geoInfo);
				Vector3f[] smoothedNormals = parentObject.getSmoothedNormals();

				Vector3f[] normals = new Vector3f[verts.length];

				for (int i = 0; i < coordNum; i++)
				{
					normals[i] = smoothedNormals[vertexReferences[i]];
				}

				geoInfo.setNormals(normals);
			}
			else
			{
				new NormalGenerator(FLAT_CREASE_ANGLE).generateNormals(geoInfo);
			}
			// stripify
			new Stripifier().stripify(geoInfo);

			geometry = geoInfo.getGeometryArray();
		}
	}

	/**
	 * Is this surface a line?
	 *
	 * @return True if this surface is a line
	 */
	public boolean isLine()
	{
		if (surface.getType() != org.newdawn.loaders.ac3d.AC3DSurface.POLYGON)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Is this surface two sided?
	 *
	 * @return True if this surface is two sided
	 */
	public boolean isTwoSided()
	{
		return surface.isTwoSided();
	}

	/**
	 * Gets the material id of this surface
	 *
	 * @return The material ID
	 */
	public int getMaterial()
	{
		return surface.getMaterial();
	}

	/**
	 * Gets the geometry of this surface
	 *
	 * @return The Geometry
	 */
	public Geometry getGeometry()
	{
		//System.out.println("Returning surfaces geometry");
		rebuildGeometry();
		return geometry;
	}

}
