package tools3d.environment.sky;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.image.TextureLoader;

public class SkyBox extends Background
{

	// TODO: this class TextureCubeMap appears to do all teh work of the below
	private TextureLoader daySkyTexture;

	private TextureLoader nightSkyTexture;

	private TextureLoader fogSkyTexture;

	private TextureLoader hazeSkyTexture;

	private TransformGroup transformGroup;

	private BranchGroup skyBoxBranch = new BranchGroup();

	private Appearance skyBoxApp;

	private final static float VAL = (float) Math.sqrt(2) / 2;

	private final static float SKYTOP = VAL; // VAL;//10540;

	private final static float SKYBOTTOM = -VAL; // -VAL;//-10540;

	private final static float SKYMIN = -VAL; // -500;

	private final static float SKYMAX = VAL; // 20580;

	private final static Point3f[] VERTS = new Point3f[8];

	private final static float T0 = 1.0f / 511.0f;

	private final static float T1 = 1.0f - T0;

	private final static float[] TOPTEX =
	{ T0, T0, T1, T0, T1, T1, T0, T1 };

	private final static float[] FRONTTEX =
	{ T0, T0, T1, T0, T1, T1, T0, T1 };

	private final static float[] BACKTEX =
	{ T1, T0, T1, T1, T0, T1, T0, T0 };

	private final static float[] RIGHTTEX =
	{ T1, T0, T1, T1, T0, T1, T0, T0 };

	private final static float[] LEFTTEX =
	{ T1, T0, T1, T1, T0, T1, T0, T0 };

	private final static float[] BOTTEX =
	{ T0, T0, T1, T0, T1, T1, T0, T1 };

	private final static float[][] TEXTURECOORDS =
	{ TOPTEX, FRONTTEX, BACKTEX, RIGHTTEX, LEFTTEX, BOTTEX };

	private final static int[] TOP =
	{ 6, 5, 4, 7 };

	private final static int[] FRONT =
	{ 0, 3, 7, 4 };

	private final static int[] BACK =
	{ 1, 5, 6, 2 };

	private final static int[] RIGHT =
	{ 2, 6, 7, 3 };

	private final static int[] LEFT =
	{ 0, 4, 5, 1 };

	private final static int[] BOTTOM =
	{ 0, 1, 2, 3 };

	private final static int[][] FACES =
	{ TOP, FRONT, BACK, RIGHT, LEFT, BOTTOM };

	private final static Texture[] TEXTURES = new Texture[6];

	static
	{
		// Lower square
		VERTS[0] = new Point3f(SKYMIN, SKYBOTTOM, SKYMIN);
		VERTS[1] = new Point3f(SKYMIN, SKYBOTTOM, SKYMAX);
		VERTS[2] = new Point3f(SKYMAX, SKYBOTTOM, SKYMAX);
		VERTS[3] = new Point3f(SKYMAX, SKYBOTTOM, SKYMIN);

		// Upper square
		VERTS[4] = new Point3f(SKYMIN, SKYTOP, SKYMIN);
		VERTS[5] = new Point3f(SKYMIN, SKYTOP, SKYMAX);
		VERTS[6] = new Point3f(SKYMAX, SKYTOP, SKYMAX);
		VERTS[7] = new Point3f(SKYMAX, SKYTOP, SKYMIN);
	}

	/**
	 * Creates a new instance of SkyBox.
	 */
	public SkyBox()
	{
		initTextures();

		TextureAttributes TexAttrSky = new TextureAttributes();
		TexAttrSky.setTextureMode(TextureAttributes.DECAL);

		this.setCapability(Background.ALLOW_GEOMETRY_WRITE);

		transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		skyBoxBranch.addChild(transformGroup);

		for (int i = 0; i < TEXTURES.length; ++i)
		{
			QuadArray q = createQuad(i);
			Appearance app = new Appearance();
			app.setTexture(TEXTURES[i]);
			app.setTextureAttributes(TexAttrSky);

			Shape3D shape = new Shape3D(q, app);
			transformGroup.addChild(shape);
		}

		setGeometry(skyBoxBranch);

		// BranchGroup clouds = (BranchGroup) NifShapeToj3d.loadShapes(new File("F:/Oblivion/meshes/sky/clouds.nif"));
		// setGeometry(clouds);

		// setCollidable(false);
		// setPickable(false);
	}

	private static void initTextures()
	{
		// top or up
		TEXTURES[0] = new TextureLoader("media/images/skybox_5.jpg", null).getTexture();
		// front or north
		TEXTURES[1] = new TextureLoader("media/images/skybox_3.jpg", null).getTexture();
		// back or south
		TEXTURES[2] = new TextureLoader("media/images/skybox_1.jpg", null).getTexture();
		// right or east
		TEXTURES[3] = new TextureLoader("media/images/skybox_4.jpg", null).getTexture();
		// left or west
		TEXTURES[4] = new TextureLoader("media/images/skybox_2.jpg", null).getTexture();
		// bottom or down
		TEXTURES[5] = new TextureLoader("media/images/skybox_6.jpg", null).getTexture();
	}

	private QuadArray createQuad(int faceidx)
	{
		final int vertcnt = 4;
		QuadArray quads = new QuadArray(vertcnt, GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.COORDINATES);

		// Repeatedly set texture coordinates.
		quads.setTextureCoordinates(0, 0, TEXTURECOORDS[faceidx]);

		for (int i = 0; i < 4; ++i)
		{
			quads.setCoordinate(i, VERTS[FACES[faceidx][i]]);
		}

		return quads;
	}

	/*
	 * private void setFace(QuadArray q, int startidx, int[] planeidx) { for (int i = 0; i < 4; ++i) {
	 * q.setCoordinate(startidx + i, VERTS[planeidx[i]]); } }
	 */

	public void setSkyImages(String baseImageName)
	{
		// TODO: this
	}

	public void p()
	{

		nightSkyTexture = new TextureLoader("media/images/nightSky.jpg", null);
		fogSkyTexture = new TextureLoader("media/images/fogSky.jpg", null);
		hazeSkyTexture = new TextureLoader("media/images/hazeSky.jpg", null);

		if (daySkyTexture == null || nightSkyTexture == null || fogSkyTexture == null)
		{
			System.out.println("Oi! either the day or the night sky is a bad file buddy.");
		}
		else
		{
			skyBoxApp.setTexture(daySkyTexture.getTexture());
		}
	}

	public void setNightSky()
	{
		System.out.println("set night sky");
		// skyBoxApp.setTexture(nightSkyTexture.getTexture());
	}

	public void setDaySky()
	{
		System.out.println("set day sky");
		// skyBoxApp.setTexture(daySkyTexture.getTexture());
	}

	public void setFogSky()
	{
		// skyBoxApp.setTexture(fogSkyTexture.getTexture());
	}

	public void setHazeSky()
	{
		// skyBoxApp.setTexture(hazeSkyTexture.getTexture());
	}

	public void setTransform(Transform3D trans)
	{
		transformGroup.setTransform(trans);
	}

}