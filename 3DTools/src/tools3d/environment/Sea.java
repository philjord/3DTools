package tools3d.environment;

import java.io.File;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3f;
import javax.vecmath.Vector4f;

import tools.ddstexture.DDSTextureLoader;

public class Sea extends BranchGroup
{
	private float size = 10000; // if this is too big triangle culling will occur

	protected float seaLevel = 0;

	private Appearance app = new Appearance();

	private BranchGroup currentSeaShape1;

	private BranchGroup currentSeaShape2;

	private BranchGroup currentSeaShape3;

	public Sea()
	{
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCollidable(false);
		setPickable(false);

		Material mat = new Material();

		mat.setShininess(1.0f); // land is not very shiny, generally
		mat.setDiffuseColor(0.4f, 0.4f, 0.4f);
		mat.setSpecularColor(0.0f, 0.0f, 0.0f);

		app.setMaterial(mat);

		Vector4f planeS = new Vector4f(0.1f, 0, 0, 0);
		Vector4f planeT = new Vector4f(0, 0, 0.1f, 0);
		TexCoordGeneration texGen = new TexCoordGeneration();
		texGen.setPlaneS(planeS);
		texGen.setPlaneT(planeT);

		app.setTexCoordGeneration(texGen);
		// TODO: just a stupid temp for now
		Texture tex = DDSTextureLoader.getTexture(new File("E:/game media/Oblivion/Textures/clutter/gems/sapphire1.dds"));
		app.setTexture(tex);

		PolygonAttributes ta = new PolygonAttributes();
		ta.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(ta);
		TransparencyAttributes trans = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.2f);
		app.setTransparencyAttributes(trans);
	}

	/**
	 * use Float.MIN_VALUE to disbale the sea
	 * 
	 * @param seaLevel
	 */
	public void setLevel(float seaLevel)
	{
		this.seaLevel = seaLevel;

		if (currentSeaShape1 != null)
		{
			removeChild(currentSeaShape1);
			currentSeaShape1 = null;
		}
		if (currentSeaShape2 != null)
		{
			removeChild(currentSeaShape2);
			currentSeaShape2 = null;
		}
		if (currentSeaShape3 != null)
		{
			removeChild(currentSeaShape3);
			currentSeaShape3 = null;
		}

		if (seaLevel != Float.MIN_VALUE)
		{
			QuadArray quads = new QuadArray(4, GeometryArray.COORDINATES);
			quads.setCoordinate(0, new Point3f(-size, seaLevel, -size));
			quads.setCoordinate(1, new Point3f(-size, seaLevel, size));
			quads.setCoordinate(2, new Point3f(size, seaLevel, size));
			quads.setCoordinate(3, new Point3f(size, seaLevel, -size));
			currentSeaShape1 = new BranchGroup();
			currentSeaShape1.setCapability(BranchGroup.ALLOW_DETACH);
			currentSeaShape1.addChild(new Shape3D(quads, app));
			addChild(currentSeaShape1);

			quads = new QuadArray(4, GeometryArray.COORDINATES);
			quads.setCoordinate(0, new Point3f(-size, seaLevel - 1.5f, -size));
			quads.setCoordinate(1, new Point3f(-size, seaLevel - 1.5f, size));
			quads.setCoordinate(2, new Point3f(size, seaLevel - 1.5f, size));
			quads.setCoordinate(3, new Point3f(size, seaLevel - 1.5f, -size));
			currentSeaShape2 = new BranchGroup();
			currentSeaShape2.setCapability(BranchGroup.ALLOW_DETACH);
			currentSeaShape2.addChild(new Shape3D(quads, app));
			addChild(currentSeaShape2);

			quads = new QuadArray(4, GeometryArray.COORDINATES);
			quads.setCoordinate(0, new Point3f(-size, seaLevel - 3f, -size));
			quads.setCoordinate(1, new Point3f(-size, seaLevel - 3f, size));
			quads.setCoordinate(2, new Point3f(size, seaLevel - 3f, size));
			quads.setCoordinate(3, new Point3f(size, seaLevel - 3f, -size));
			currentSeaShape3 = new BranchGroup();
			currentSeaShape3.setCapability(BranchGroup.ALLOW_DETACH);
			currentSeaShape3.addChild(new Shape3D(quads, app));
			addChild(currentSeaShape3);
		}
	}
}
