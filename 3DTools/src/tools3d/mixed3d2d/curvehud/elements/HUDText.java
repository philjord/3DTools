package tools3d.mixed3d2d.curvehud.elements;

import java.io.IOException;

import javax.vecmath.Color4f;
import javax.vecmath.Point2f;

import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.font.FontSet;
import com.jogamp.opengl.test.junit.graph.demos.ui.Label;

import tools3d.mixed3d2d.Canvas3D2D;

public class HUDText
{
	private boolean alignHCenter = true;

	private Label textElement;

	private Label textShadowElement;

	private Color4f textColor = new Color4f(1.0f, 1.0f, 0.8f, 1f);

	private Color4f textGreyColor = new Color4f(textColor);

	private Color4f textShadowColor = new Color4f(0.3f, 0.3f, 0.2f, 0.7f);

	private Font textFont;

	private Font greyTextFont;

	private Canvas3D2D currentCanvas;

	private Point2f point;

	public HUDText(Canvas3D2D canvas3d, Point2f point)
	{
		this(canvas3d, point, 36);
	}

	public HUDText(Canvas3D2D canvas3d, Point2f point, int fontSize)
	{
		this(canvas3d, point, fontSize, true);
	}

	public HUDText(Canvas3D2D canvas3d, Point2f point, int fontSize, boolean alignHCenter)
	{
		this.alignHCenter = alignHCenter;
		this.point = point;
		textGreyColor.scale(0.8f);

		try
		{
			textFont = FontFactory.get(FontFactory.UBUNTU).getDefault();
			greyTextFont = FontFactory.get(FontFactory.UBUNTU).get(FontSet.FAMILY_REGULAR, FontSet.STYLE_ITALIC);
		}
		catch (final IOException ioe)
		{
			throw new RuntimeException(ioe);
		}

		textShadowElement = new Label(canvas3d.getVertexFactory(), 0, textFont, fontSize * 0.002f, "");
		textShadowElement.setEnabled(true);
		textShadowElement.translate(point.x + 0.01f, point.y + 0.01f, 0f);
		textShadowElement.setColor(textShadowColor.x, textShadowColor.y, textShadowColor.z, textShadowColor.w);

		textElement = new Label(canvas3d.getVertexFactory(), 0, greyTextFont, fontSize * 0.002f, "");
		textElement.setEnabled(true);
		textElement.translate(point.x, point.y, 0f);
		textElement.setColor(textColor.x, textColor.y, textColor.z, textColor.w);
		addToCanvas(canvas3d);
	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		if (currentCanvas != null)
		{
			removeFromCanvas();
		}
		currentCanvas = canvas;
		canvas.addUIShape(textElement);
		canvas.addUIShape(textShadowElement);
	}

	public void removeFromCanvas()
	{
		if (currentCanvas != null)
		{
			currentCanvas.removeUIShape(textElement);
			currentCanvas.removeUIShape(textShadowElement);
			currentCanvas = null;
		}
	}

	public void setLocation(float x, float y)
	{
		textElement.translate(x, y, 0f);
		textShadowElement.translate(x + 0.01f, y + 0.01f, 0f);
	}

	public void setText(String newText)
	{

		int hOffset = 0;
		if (alignHCenter)
		{
			//FIXME: noHalign
			//int sw = textElement.getGraphics().getFontMetrics(textFont).stringWidth(newText);
			//hOffset = (rectangle.width / 2) - (sw / 2);
		}

		//textShadowElement.setText(newText);
		textElement.setColor(textColor.x, textColor.y, textColor.z, textColor.w);
		textElement.setFont(textFont);
		textElement.setText(newText);

		//debug outliner
		//textElement.getGraphics().drawRect(0, 0, rectangle.width-1, rectangle.height-1); 

	}

	public void setTextGreyed(String newText)
	{
		int hOffset = 0;
		if (alignHCenter)
		{
			//int sw = textElement.getGraphics().getFontMetrics(textFont).stringWidth(newText);
			//hOffset = (rectangle.width / 2) - (sw / 2);
		}

		textElement.setColor(textGreyColor.x, textGreyColor.y, textGreyColor.z, textGreyColor.w);
		textElement.setFont(greyTextFont);
		textElement.setText(newText);

		textShadowElement.setText("");

	}
}
