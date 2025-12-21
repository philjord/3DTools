package tools3d.mixed3d2d.curvehud.elements;

import com.jogamp.graph.ui.shapes.CrossHair;

import tools3d.mixed3d2d.Canvas3D2D;

public class HUDCrossHair {
	private CrossHair crossHairCtr;

	public HUDCrossHair(Canvas3D2D canvas) {
		addToCanvas(canvas);
	}

	public HUDCrossHair() {
	}

	public void addToCanvas(Canvas3D2D canvas) {
		crossHairCtr = new CrossHair(0, 0.05f, 0.05f, 0.002f);
		crossHairCtr.setVisible(true);
		crossHairCtr.moveTo(0f, 0f, -0.1f);
		crossHairCtr.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		canvas.addUIShape(crossHairCtr);
	}

	public void removeFromCanvas(Canvas3D2D canvas) {
		canvas.removeUIShape(crossHairCtr);
	}

}
