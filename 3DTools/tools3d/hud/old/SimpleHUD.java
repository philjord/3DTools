package tools3d.hud.old;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import tools3d.utils.Utils3D;

public class SimpleHUD extends BranchGroup
{
	private FPSCounter fpsCounter;

	// front Clip is the closest something can be to the camera without
	// being clipped for being to close
	private float frontClipAndABit = 1f;

	public SimpleHUD(float frontClip)
	{
		this.frontClipAndABit = frontClip * 1.1f;
		addFPS();
		addCrossHair();

		/*ModelClip modelClip = new ModelClip();
		modelClip.addScope(this);
		modelClip.setInfluencingBounds(Utils3D.defaultBounds);
		
		addChild(modelClip);*/

		/*LabelOverlay overlay = new LabelOverlay(canvas, new Rectangle(10, 10, 128, 128), "hello world");
		overlay.setColor(Color.BLUE);
		overlay.setBackgroundColor(Color.RED);
		overlay.setVisible(true);
		addChild(overlay.getRoot());
		overlay.repaint();
		overlay.initialize();*/

	}

	private void addFPS()
	{
		TransformGroup vtg = Utils3D.createTranslatedTG(new Vector3f(-frontClipAndABit / 2.5f, frontClipAndABit / 3, -frontClipAndABit));
		fpsCounter = new FPSCounter();
		vtg.addChild(fpsCounter);
		addChild(vtg);
	}

	private void addCrossHair()
	{
		CrossHair crossHair = new CrossHair(0.002f);
		TransformGroup vtg = Utils3D.createTranslatedTG(new Vector3f(0f, 0f, -frontClipAndABit));
		vtg.addChild(crossHair);
		addChild(vtg);
	}

}
