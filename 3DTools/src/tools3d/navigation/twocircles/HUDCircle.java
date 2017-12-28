package tools3d.navigation.twocircles;

import com.jogamp.opengl.hudbasics.graph.demos.ui.CrossHair;
import com.jogamp.opengl.hudbasics.graph.demos.ui.LabelButton;

import tools3d.mixed3d2d.Canvas3D2D;

public class HUDCircle
{
	private LabelButton labelButton;

	public HUDCircle(Canvas3D2D canvas)
	{

		addToCanvas(canvas);
	}

	public HUDCircle()
	{

	}

	public void addToCanvas(Canvas3D2D canvas)
	{
		labelButton = new LabelButton(canvas.getVertexFactory(), canvas.getRenderMode(), canvas.getFont(), "", 0.25f, 0.25f);
		labelButton.setEnabled(true);
		labelButton.translate(0f, 0f, -0.1f);
		labelButton.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		canvas.addUIShape(labelButton);
	}

	public void removeFromCanvas(Canvas3D2D canvas)
	{
		canvas.removeUIShape(labelButton);
	}

}
