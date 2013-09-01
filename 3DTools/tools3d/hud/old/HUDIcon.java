package tools3d.hud.old;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TransparencyAttributes;

import com.sun.j3d.utils.image.TextureLoader;

public class HUDIcon extends Shape3D
{
	private ImageComponent imageComponent;

	private float width = 0f;

	private float height = 0f;

	public HUDIcon(String imageName, float width, float height)
	{
		TextureLoader tex = new TextureLoader(imageName, "RGB", null);
		this.imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGB, tex.getImage().getImage());
		this.width = width;
		this.height = height;

		updateText2D();
	}

	private void updateText2D()
	{
		Texture2D t2d = setupTexture(imageComponent);

		QuadArray rect = setupGeometry();
		setGeometry(rect);

		Appearance appearance = setupAppearance(t2d);
		setAppearance(appearance);
	}

	/**
	 * Create the ImageComponent and Texture object.
	 */
	private Texture2D setupTexture(ImageComponent imageComponent)
	{
		Texture2D t2d = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, imageComponent.getWidth(), imageComponent.getHeight());
		t2d.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		t2d.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		t2d.setImage(0, imageComponent);
		t2d.setEnable(true);

		return t2d;
	}

	/**
	 * Creates a rectangle of the given width and height and sets up
	 * texture coordinates to map the text image onto the whole surface
	 * of the rectangle (the rectangle is the same size as the text image)
	 */
	private QuadArray setupGeometry()
	{
		float zPosition = 0f;
		float rectWidth = width;
		float rectHeight = height;
		float[] verts1 =
		{ rectWidth, 0f, zPosition, rectWidth, rectHeight, zPosition, 0f, rectHeight, zPosition, 0f, 0f, zPosition };
		float[] texCoords =
		{ 0f, -1f, 0f, 0f, (-1f), 0f, (-1f), -1f };

		QuadArray rect = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		rect.setCoordinates(0, verts1);
		rect.setTextureCoordinates(0, 0, texCoords);

		return rect;
	}

	/**
	 * Creates Appearance for this Shape3D.  This sets transparency
	 * for the object (we want the text to be "floating" in space,
	 * so only the text itself should be non-transparent.  Also, the
	 * appearance disables lighting for the object; the text will
	 * simply be colored, not lit.
	 */
	private Appearance setupAppearance(Texture2D t2d)
	{
		TransparencyAttributes transp = new TransparencyAttributes();
		transp.setTransparencyMode(TransparencyAttributes.BLENDED);
		transp.setTransparency(0f);
		Appearance appearance = new Appearance();
		appearance.setTransparencyAttributes(transp);
		appearance.setTexture(t2d);

		Material m = new Material();
		m.setLightingEnable(false);
		appearance.setMaterial(m);

		return appearance;
	}

}
