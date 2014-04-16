package tools3d.utils.leafnode;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

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
import javax.vecmath.Color3f;

/**
 * A Text2D object is a representation of a string as a texture mapped rectangle. The texture for the rectangle shows
 * the string as rendered in the specified color with a transparent background. The appearance of the characters is
 * specified using the font indicated by the font name, size and style (see java.awt.Font). The approximate height of
 * the rendered string will be the font size times the rectangle scale factor, which has a default value of 1/256. For
 * example, a 12 point font will produce characters that are about 12/256 = 0.047 meters tall. The lower left corner of
 * the rectangle is located at (0,0,0) with the height extending along the positive y-axis and the width extending along
 * the positive x-axis.
 */
public class MyText2D extends Shape3D
{

	// This table caches FontMetrics objects to avoid the huge cost
	// of re-retrieving metrics for a font we've already seen.
	private static HashMap<Font, FontMetrics> metricsTable = new HashMap<Font, FontMetrics>();

	private float rectangleScaleFactor = 1f / 256f;

	private Color3f color = new Color3f();

	private String fontName;

	private int fontSize, fontStyle;

	private String text;

	private float rectVisibleWidth;

	private float rectVisibleHeight;

	/**
	 * Creates a Shape3D object which holds a rectangle that is texture-mapped with an image that has the specified text
	 * written with the specified font parameters.
	 * 
	 * @param text
	 *            The string to be written into the texture map.
	 * @param color
	 *            The color of the text string.
	 * @param fontName
	 *            The name of the Java font to be used for the text string.
	 * @param fontSize
	 *            The size of the Java font to be used.
	 * @param fontStyle
	 *            The style of the Java font to be used.
	 */
	public MyText2D(String text, Color3f color, String fontName, int fontSize, int fontStyle)
	{
		this.color.set(color);
		this.fontName = fontName;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.text = text;

		this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		this.setPickable(false);

		updateText2D(text, color, fontName, fontSize, fontStyle);
	}

	/*
	 * Changes text of this Text2D to 'text'. All other parameters (color, fontName, fontSize, fontStyle remain the
	 * same. @param text The string to be set.
	 */
	public void setString(String text)
	{
		this.text = text;
		updateText2D(text, color, fontName, fontSize, fontStyle);
	}

	private void updateText2D(String t, Color3f c, String fName, int fSize, int fStyle)
	{
		setAppearance(null);
		ImageComponent imageComponent = setupImage(t, c, fName, fSize, fStyle);

		Texture2D t2d = setupTexture(imageComponent);

		QuadArray rect = setupGeometry(imageComponent.getWidth(), imageComponent.getHeight());
		setGeometry(rect);

		Appearance appearance = setupAppearance(t2d);
		setAppearance(appearance);
	}

	/**
	 * Sets the scale factor used in converting the image width/height to width/height values in 3D.
	 * 
	 * @param newScaleFactor
	 *            The new scale factor.
	 */
	public void setRectangleScaleFactor(float newScaleFactor)
	{
		rectangleScaleFactor = newScaleFactor;
		updateText2D(text, color, fontName, fontSize, fontStyle);
	}

	/**
	 * Gets the current scale factor being used in converting the image width/height to width/height values in 3D.
	 * 
	 * @return The current scale factor.
	 */
	public float getRectangleScaleFactor()
	{
		return rectangleScaleFactor;
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
	 * Creates a ImageComponent2D of the correct dimensions for the given font attributes. Draw the given text into the
	 * image in the given color. The background of the image is transparent (alpha = 0).
	 */
	@SuppressWarnings("deprecation")
	private ImageComponent setupImage(String text, Color3f color, String fontName, int fontSize, int fontStyle)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Font font = new Font(fontName, fontStyle, fontSize);

		FontMetrics metrics;
		if ((metrics = metricsTable.get(font)) == null)
		{
			metrics = toolkit.getFontMetrics(font);
			metricsTable.put(font, metrics);
		}

		int descent = metrics.getMaxDescent();
		int textHeight = metrics.getHeight();
		int textWidth = metrics.stringWidth(text);

		rectVisibleWidth = (textWidth) * rectangleScaleFactor;
		rectVisibleHeight = (textHeight) * rectangleScaleFactor;

		// Need to make width/height powers of 2 because of Java3d texture
		// size restrictions
		int pow = 1;
		int width = textWidth;
		for (int i = 1; i < 32; ++i)
		{
			pow *= 2;
			if (width <= pow)
				break;
		}
		width = Math.max(width, pow);

		pow = 1;
		int height = textHeight;
		for (int i = 1; i < 32; ++i)
		{
			pow *= 2;
			if (height <= pow)
				break;
		}
		height = Math.max(height, pow);

		// For now, jdk 1.2 only handles ARGB format, not the RGBA we want
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics offscreenGraphics = bImage.createGraphics();

		// First, erase the background to the text panel
		Color myFill = new Color(1f, 1f, 1f, 1f);
		if (text.length() == 0)
		{
			// make it blank if the text is blank - set alpha to 0
			myFill = new Color(0f, 0f, 0f, 0f);
		}
		offscreenGraphics.setColor(myFill);
		// only color in the "written on" bit
		offscreenGraphics.fillRoundRect(0, 0, textWidth, textHeight, 4, 4);

		// Next, set desired text properties (font, color) and draw String
		offscreenGraphics.setFont(font);
		Color myTextColor = new Color(color.x, color.y, color.z, 1f);
		offscreenGraphics.setColor(myTextColor);
		offscreenGraphics.drawString(text, 0, textHeight - descent);

		ImageComponent imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
		return imageComponent;
	}

	/**
	 * Creates a rectangle of the given width and height and sets up texture coordinates to map the text image onto the
	 * whole surface of the rectangle (the rectangle is the same size as the text image)
	 */
	private QuadArray setupGeometry(int width, int height)
	{
		float zPosition = 0f;
		float rectWidth = width * rectangleScaleFactor;
		float rectHeight = height * rectangleScaleFactor;
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
	 * Creates Appearance for this Shape3D. This sets transparency for the object (we want the text to be "floating" in
	 * space, so only the text itself should be non-transparent. Also, the appearance disables lighting for the object;
	 * the text will simply be colored, not lit.
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

	public float getRectVisibleHeight()
	{
		return rectVisibleHeight;
	}

	public float getRectVisibleWidth()
	{
		return rectVisibleWidth;
	}

	public String getText()
	{
		return text;
	}

}
