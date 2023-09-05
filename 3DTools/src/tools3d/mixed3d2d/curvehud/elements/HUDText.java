package tools3d.mixed3d2d.curvehud.elements;

import java.io.IOException;

import org.jogamp.vecmath.Color4f;
import org.jogamp.vecmath.Point2f;

import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontSet;
import com.jogamp.graph.ui.shapes.Label;

import tools3d.mixed3d2d.Canvas3D2D;

public class HUDText {
	private boolean		alignHCenter		= true;

	private Label		textElement;

	private Label		textShadowElement;

	private Color4f		textColor			= new Color4f(1.0f, 1.0f, 0.8f, 1f);

	private Color4f		greyTextColor		= new Color4f(textColor);
	private Color4f		currentTextColor	= textColor;

	private Color4f		textShadowColor		= new Color4f(0.3f, 0.3f, 0.2f, 0.7f);

	private Font		textFont;

	private Font		greyTextFont;
	private Font		currentTextFont;

	private int			fontSize;

	private Canvas3D2D	currentCanvas;

	private Point2f		point;

	private String		text;

	/**
	 * Point is a -1 to 1 range
	 * @param point
	 * @param text
	 */
	public HUDText(Point2f point, String text) {
		this(point, 36, text);
	}

	public HUDText(Point2f point, int fontSize, String text) {
		this(point, fontSize, true, text);
	}

	public HUDText(Point2f point, int fontSize, boolean alignHCenter, String text) {
		this.text = text;
		this.alignHCenter = alignHCenter;
		this.point = point;
		this.fontSize = fontSize;
		greyTextColor.scale(0.8f);

		try {
			textFont = new UbuntuFontLoader().getDefault();
			greyTextFont = new UbuntuFontLoader().get(FontSet.FAMILY_REGULAR, FontSet.STYLE_ITALIC);
			currentTextFont = textFont;
		} catch (final IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}

	public void addToCanvas(Canvas3D2D canvas) {
		if (currentCanvas != null) {
			removeFromCanvas();
		}

		if (textShadowElement == null) {

			textShadowElement = new Label(0, currentTextFont, fontSize * 0.002f, text);
			textShadowElement.setEnabled(true);
			textShadowElement.setColor(textShadowColor.x, textShadowColor.y, textShadowColor.z, textShadowColor.w);

			textElement = new Label(0, currentTextFont, fontSize * 0.002f, text);
			textElement.setEnabled(true);
			textElement.setColor(currentTextColor.x, currentTextColor.y, currentTextColor.z, currentTextColor.w);

			if (alignHCenter) {
				float tew = textElement.getBounds().getWidth();
				float hOffset = (tew / 2.0f);
				textElement.moveTo(point.x - hOffset, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f - hOffset, point.y + 0.005f, 0f);
			} else {
				textElement.moveTo(point.x, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f, point.y + 0.005f, 0f);
			}
		}

		currentCanvas = canvas;
		canvas.addUIShape(textShadowElement);
		canvas.addUIShape(textElement);

	}

	public void removeFromCanvas() {
		if (currentCanvas != null) {
			currentCanvas.removeUIShape(textElement);
			currentCanvas.removeUIShape(textShadowElement);
			currentCanvas = null;
		}
	}

	public void setLocation(Point2f point) {
		this.point = point;
		if (textShadowElement != null) {
			if (alignHCenter) {
				float tew = textElement.getBounds().getWidth();
				float hOffset = (tew / 2.0f);
				textElement.moveTo(point.x - hOffset, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f - hOffset, point.y + 0.005f, 0f);
			} else {
				textElement.moveTo(point.x, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f, point.y + 0.005f, 0f);
			}
		}
	}

	public void setText(String newText) {
		this.text = newText;
		currentTextFont = textFont;
		currentTextColor = textColor;

		if (textShadowElement != null) {
			textShadowElement.setFont(currentTextFont);
			textShadowElement.setText(newText);
			textElement.setFont(currentTextFont);
			textElement.setText(newText);
			textElement.setColor(currentTextColor.x, currentTextColor.y, currentTextColor.z, currentTextColor.w);

			if (alignHCenter) {
				float tew = textElement.getBounds().getWidth();
				float hOffset = (tew / 2.0f);
				textElement.moveTo(point.x - hOffset, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f - hOffset, point.y + 0.005f, 0f);
			} else {
				textElement.moveTo(point.x, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f, point.y + 0.005f, 0f);
			}
		}

		//debug outliner
		//textElement.getGraphics().drawRect(0, 0, rectangle.width-1, rectangle.height-1); 

	}

	public void setTextGreyed(String newText) {
		this.text = newText;
		currentTextFont = greyTextFont;
		currentTextColor = greyTextColor;

		if (textShadowElement != null) {
			textElement.setColor(currentTextColor.x, currentTextColor.y, currentTextColor.z, currentTextColor.w);
			textElement.setFont(currentTextFont);
			textElement.setText(newText);
			textShadowElement.setText("");

			if (alignHCenter) {
				float tew = textElement.getBounds().getWidth();
				float hOffset = (tew / 2.0f);
				textElement.moveTo(point.x - hOffset, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f - hOffset, point.y + 0.005f, 0f);
			} else {
				textElement.moveTo(point.x, point.y, 0f);
				textShadowElement.moveTo(point.x - 0.005f, point.y + 0.005f, 0f);
			}
			
			//TODO: translate is waaay late much after test is changed, but these do not help
			//textElement.markShapeDirty();
			//textShadowElement.markShapeDirty();
			//textElement.markStateDirty();
			//textShadowElement.markStateDirty();
		}

	}
}
