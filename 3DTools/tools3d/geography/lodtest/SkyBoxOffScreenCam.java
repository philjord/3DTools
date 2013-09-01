package tools3d.geography.lodtest;

import java.awt.image.BufferedImage;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import tools3d.camera.old.OffScreenCam;
import tools3d.utils.Utils3D;
import tools3d.utils.scenegraph.LocationUpdateListener;

import com.sun.j3d.utils.image.TextureLoader;

public class SkyBoxOffScreenCam extends Background implements LocationUpdateListener
{
	private final static float VAL = (float) Math.sqrt(2) / 2;

	private final static float SKYTOP = VAL; //VAL;//10540;

	private final static float SKYBOTTOM = -VAL; // -VAL;//-10540;

	private final static float SKYMIN = -VAL; //-500;

	private final static float SKYMAX = VAL; //20580;

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

	private final static float[][] TEXTURECOORDS =
	{ TOPTEX, FRONTTEX, BACKTEX, RIGHTTEX, LEFTTEX };

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

	private final static int[][] FACES =
	{ TOP, FRONT, BACK, RIGHT, LEFT };

	private final static Texture[] TEXTURES = new Texture[5];

	static
	{
		//	  Lower square
		VERTS[0] = new Point3f(SKYMIN, SKYBOTTOM, SKYMIN);
		VERTS[1] = new Point3f(SKYMIN, SKYBOTTOM, SKYMAX);
		VERTS[2] = new Point3f(SKYMAX, SKYBOTTOM, SKYMAX);
		VERTS[3] = new Point3f(SKYMAX, SKYBOTTOM, SKYMIN);

		//	  Upper square
		VERTS[4] = new Point3f(SKYMIN, SKYTOP, SKYMIN);
		VERTS[5] = new Point3f(SKYMIN, SKYTOP, SKYMAX);
		VERTS[6] = new Point3f(SKYMAX, SKYTOP, SKYMAX);
		VERTS[7] = new Point3f(SKYMAX, SKYTOP, SKYMIN);
	}

	private Appearance[] apps = new Appearance[5];

	private OffScreenCam cam;

	/**
	* Creates a new instance of SkyBox.
	*/
	public SkyBoxOffScreenCam(OffScreenCam cam)
	{
		this.cam = cam;
		setApplicationBounds(Utils3D.defaultBounds);
		initTextures();

		TextureAttributes TexAttrSky = new TextureAttributes();
		TexAttrSky.setTextureMode(TextureAttributes.DECAL);

		BranchGroup skyBoxBranch = new BranchGroup();

		for (int i = 0; i < TEXTURES.length; ++i)
		{
			QuadArray q = createQuad(i);
			Appearance app = new Appearance();
			Texture2D tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, 256, 256);
			tex.setCapability(Texture.ALLOW_IMAGE_WRITE);
			BufferedImage bImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			ImageComponent2D imageCom = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
			tex.setImage(0, imageCom);
			app.setTexture(tex);
			app.setTextureAttributes(TexAttrSky);
			app.setCapability(Appearance.ALLOW_TEXTURE_READ);

			apps[i] = app;

			Shape3D shape = new Shape3D(q, app);
			skyBoxBranch.addChild(shape);
		}

		setGeometry(skyBoxBranch);

		setCollidable(false);
		setPickable(false);
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

	long lastPic = 0;

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		long start = System.currentTimeMillis();
		if (start - lastPic > 5000)
		{
 

			System.out.println("trying it now!");

			Transform3D t0 = new Transform3D();
			t0.rotY(0);
			t0.setTranslation(trans);
			ImageComponent2D imageComponent0 = new ImageComponent2D(ImageComponent.FORMAT_RGBA, cam.renderImage(t0, 1000, 10000).getImage());
			apps[1].getTexture().setImage(0, imageComponent0);

			Transform3D t90 = new Transform3D();
			t90.rotY(Math.PI * 0.5f);
			t90.setTranslation(trans);
			ImageComponent2D imageComponent90 = new ImageComponent2D(ImageComponent.FORMAT_RGBA, cam.renderImage(t90, 1000, 10000).getImage());
			apps[4].getTexture().setImage(0, imageComponent90);

			Transform3D t180 = new Transform3D();
			t180.rotY(Math.PI);
			t180.setTranslation(trans);
			ImageComponent2D imageComponent180 = new ImageComponent2D(ImageComponent.FORMAT_RGBA, cam.renderImage(t180, 1000, 10000).getImage());
			apps[2].getTexture().setImage(0, imageComponent180);

			Transform3D t270 = new Transform3D();
			t270.rotY(Math.PI * 1.5f);
			t270.setTranslation(trans);
			ImageComponent2D imageComponent270 = new ImageComponent2D(ImageComponent.FORMAT_RGBA, cam.renderImage(t270, 1000, 10000).getImage());
			apps[3].getTexture().setImage(0, imageComponent270);

			System.out.println("done in " + (System.currentTimeMillis() - start));
			lastPic = System.currentTimeMillis();
		}
	}

}