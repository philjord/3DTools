/*
 * Created on Jan 2, 2005
 */
package tools3d.geography;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import tools3d.utils.Utils3D;

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickTool;

/**
 * @author Administrator
 *
 */
public class LargeGeomteryLODGen
{
	public static BranchGroup getBranchGroup(String textureFile, LargeGeometry[] largeGeometries, float[] distances)
	{
		BranchGroup root = new BranchGroup();
		root.setPickable(false);

		// allocate array of switches based on the base level 0
		Switch[][][] switches = new Switch[largeGeometries[0].getNumSpaceX()][largeGeometries[0].getNumSpaceY()][largeGeometries[0].getNumSpaceZ()];

		for (int lg = 0; lg < largeGeometries.length; lg++)
		{
			LargeGeometry largeGeometry = largeGeometries[lg];

			float minX = largeGeometry.getMinX();
			float minY = largeGeometry.getMinY();
			float minZ = largeGeometry.getMinZ();

			float spaceSizeX = largeGeometry.getSpaceSizeX();
			float spaceSizeY = largeGeometry.getSpaceSizeY();
			float spaceSizeZ = largeGeometry.getSpaceSizeZ();

			//NOTE Y sizes do not match up!
			System.out.println("spaceSizeX " + spaceSizeX);
			System.out.println("spaceSizeY " + spaceSizeY);
			System.out.println("spaceSizeZ " + spaceSizeZ);

			double xExtent = largeGeometry.getMaxX() - largeGeometry.getMinX();
			double zExtent = largeGeometry.getMaxZ() - largeGeometry.getMinZ();

			Vector4f planeS = new Vector4f((float) (1 / xExtent), 0, 0, 0);
			Vector4f planeT = new Vector4f(0, 0, (float) (1 / zExtent), 0);
			TexCoordGeneration texGen = new TexCoordGeneration();
			texGen.setPlaneS(planeS);
			texGen.setPlaneT(planeT);
			Appearance app = largeGeometry.getApp();
			app.setTexCoordGeneration(texGen);

			TextureLoader tex = new TextureLoader(textureFile, "RGB", null);
			app.setTexture(tex.getTexture());

			TextureAttributes texAtt = new TextureAttributes();
			texAtt.setTextureMode(TextureAttributes.REPLACE);
			app.setTextureAttributes(texAtt);

			app.getMaterial().setShininess(128f);

			for (int x = 0; x < largeGeometry.getNumSpaceX(); x++)
			{
				for (int y = 0; y < largeGeometry.getNumSpaceY(); y++)
				{
					for (int z = 0; z < largeGeometry.getNumSpaceZ(); z++)
					{
						IndexedGeometryArray tsa = largeGeometry.getSubSpaces()[x][y][z];

						if (tsa != null)
						{
							//do we have a switch set up?
							if (switches[x][y][z] == null)
							{
								Switch sw = new Switch(0);
								sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_READ);
								sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_WRITE);
								switches[x][y][z] = sw;

								DistanceLOD lod = new DistanceLOD(distances);
								lod.addSwitch(sw);
								lod.setSchedulingBounds(Utils3D.defaultBounds);
								// note set position method is not as good as transform
								TransformGroup et = new TransformGroup();
								Transform3D t = new Transform3D();
								t.setTranslation(new Vector3f((x * spaceSizeX) + (spaceSizeX / 2) + minX, (y * spaceSizeY) + (spaceSizeY / 2) + minY,
										(z * spaceSizeZ) + (spaceSizeZ / 2) + minZ));
								et.setTransform(t);
								et.addChild(lod);
								root.addChild(et);

								root.addChild(sw);
							}

							Switch sw = switches[x][y][z];

							Shape3D s = new Shape3D();
							//	if (lg > 0)
							s.addGeometry(tsa);
							s.setAppearance(app);
							PickTool.setCapabilities(s, PickTool.INTERSECT_FULL);
							s.setPickable(false);

							// sometimes the subspaces might be touched differently,
							// so make sure the right num of children are attached
							if (lg > sw.numChildren())
							{
								for (int i = 0; i < lg - sw.numChildren(); i++)
								{
									sw.addChild(new Shape3D());
								}
							}
							sw.addChild(s);
						}
					}
				}
			}
		}

		return root;
	}
}
