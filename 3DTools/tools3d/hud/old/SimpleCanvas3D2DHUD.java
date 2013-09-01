package tools3d.hud.old;

import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUD;

public class SimpleCanvas3D2DHUD extends HUD
{
	private HUDCrossHair hch;

	private HUDFPSCounter hfc;

	public SimpleCanvas3D2DHUD(Canvas3D2D canvas)
	{
		hfc = new HUDFPSCounter(canvas);
		addChild(hfc);
		hch = new HUDCrossHair(canvas, "cerb3.gif");
	}

	public void addFPSListener(FPSListener listener)
	{
		hfc.addFPSListener(listener);
	}

	public void removeFPSListener(FPSListener listener)
	{
		hfc.removeFPSListener(listener);
	}

}
