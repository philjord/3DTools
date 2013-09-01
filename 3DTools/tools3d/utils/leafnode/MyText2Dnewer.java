package tools3d.utils.leafnode;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
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
public class MyText2Dnewer extends Shape3D
{

	// This table caches FontMetrics objects to avoid the huge cost
	// of re-retrieving metrics for a font we've already seen.
	private static HashMap<Font, FontMetrics> metricsTable = new HashMap<Font, FontMetrics>();

	private float scaleFactor = 1f / 256f;

	private Color textColor;

	private String text;

	private float rectVisibleWidth;

	private float rectVisibleHeight;

	private FontMetrics metrics;

	private Font font;

	private Appearance appearance;

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
	@SuppressWarnings("deprecation")
	public MyText2Dnewer(String text, Color3f color, String fontName, int fontSize, int fontStyle, float scaleFactor)
	{
		this.textColor = new Color(color.x, color.y, color.z, 1f);

		this.text = text;
		this.scaleFactor = scaleFactor;

		this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

		font = new Font(fontName, fontStyle, fontSize);

		if ((metrics = metricsTable.get(font)) == null)
		{
			metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
			metricsTable.put(font, metrics);
		}

		TransparencyAttributes transp = new TransparencyAttributes();
		transp.setTransparencyMode(TransparencyAttributes.BLENDED);
		transp.setTransparency(0f);
		appearance = new Appearance();
		appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		appearance.setTransparencyAttributes(transp);

		Material m = new Material();
		m.setLightingEnable(false);
		appearance.setMaterial(m);

		setString(text);
	}

	/*
	 * Changes text of this Text2D to 'text'. All other parameters (color, fontName, fontSize, fontStyle remain the
	 * same. @param text The string to be set.
	 */
	public void setString(String text)
	{
		this.text = text;

		ImageComponent imageComponent = setupImageComponent();

		setGeometry(createGeometry(imageComponent.getWidth(), imageComponent.getHeight()));
		appearance.setTexture(createTexture(imageComponent));
		setAppearance(appearance);
	}

	/**
	 * Creates a ImageComponent2D of the correct dimensions for the given font attributes. Draw the given text into the
	 * image in the given color. The background of the image is transparent (alpha = 0).
	 */

	BufferedImage currentImage;

	Graphics2D offscreenGraphics;

	Color transparentFill = new Color(1f, 1f, 1f, 0f);

	Color speechBubbleFill = new Color(1f, 1f, 1f, 1f);

	private ImageComponent setupImageComponent()
	{
		int descent = metrics.getMaxDescent();
		int textHeight = metrics.getHeight();
		int textWidth = metrics.stringWidth(text);

		rectVisibleWidth = textWidth * scaleFactor;
		rectVisibleHeight = textHeight * scaleFactor;

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
		if (currentImage == null || currentImage.getWidth() < rectVisibleWidth || currentImage.getHeight() < rectVisibleHeight)
		{
			currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			offscreenGraphics = currentImage.createGraphics();
			offscreenGraphics.setFont(font);
		}

		// First, erase the background to the text panel

		/*
		 * if (text.length() == 0) { // make it blank if the text is blank - set alpha to 0 myFill = new Color(0f, 0f,
		 * 0f, 0f); }
		 */
		offscreenGraphics.setColor(transparentFill);
		offscreenGraphics.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());

		offscreenGraphics.setColor(speechBubbleFill);
		// only color in the "written on" bit
		offscreenGraphics.fillRoundRect(0, 0, textWidth, textHeight, 4, 4);

		// Next, set desired text properties (font, color) and draw String
		offscreenGraphics.setColor(textColor);
		offscreenGraphics.drawString(text, 0, textHeight - descent);

		ImageComponent imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, currentImage);
		return imageComponent;
	}

	/**
	 * Create the ImageComponent and Texture object.
	 */
	private Texture2D createTexture(ImageComponent imageComponent)
	{
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, imageComponent.getWidth(), imageComponent.getHeight());
		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setImage(0, imageComponent);
		texture.setEnable(true);

		return texture;
	}

	/**
	 * Creates a rectangle of the given width and height and sets up texture coordinates to map the text image onto the
	 * whole surface of the rectangle (the rectangle is the same size as the text image)
	 */
	private QuadArray createGeometry(int width, int height)
	{
		float zPosition = 0f;
		float rectWidth = width * scaleFactor;
		float rectHeight = height * scaleFactor;
		float[] verts1 =
		{ rectWidth, 0f, zPosition, rectWidth, rectHeight, zPosition, 0f, rectHeight, zPosition, 0f, 0f, zPosition };
		float[] texCoords =
		{ 0f, -1f, 0f, 0f, (-1f), 0f, (-1f), -1f };

		QuadArray rect = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		rect.setCoordinates(0, verts1);
		rect.setTextureCoordinates(0, 0, texCoords);

		// and allow intersections
		rect.setCapability(Geometry.ALLOW_INTERSECT);

		return rect;
	}

	/**
	 * @return
	 */
	public float getRectVisibleHeight()
	{
		return rectVisibleHeight;
	}

	/**
	 * @return
	 */
	public float getRectVisibleWidth()
	{
		return rectVisibleWidth;
	}

	/**
	 * Gets the current scale factor being used in converting the image width/height to width/height values in 3D.
	 * 
	 * @return The current scale factor.
	 */
	public float getRectangleScaleFactor()
	{
		return scaleFactor;
	}

	/**
	 * @return
	 */
	public String getText()
	{
		return text;
	}

}
