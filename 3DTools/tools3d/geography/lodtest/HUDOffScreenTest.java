package tools3d.geography.lodtest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import tools3d.camera.old.OffScreenCam;
import tools3d.hud.Canvas3D2D;
import tools3d.hud.HUDElement;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class HUDOffScreenTest implements LocationUpdateListener
{
	private HUDElement bgElement0;

	private HUDElement bgElement90;

	private HUDElement bgElement180;

	private HUDElement bgElement270;

	private OffScreenCam cam;

	public HUDOffScreenTest(Canvas3D2D canvas, OffScreenCam cam)
	{
		this.cam = cam;
		bgElement0 = new HUDElement(200, 200);
		bgElement0.setLocation(0, 300);
		canvas.addElement(bgElement0);

		bgElement90 = new HUDElement(200, 200);
		bgElement90.setLocation(200, 300);
		canvas.addElement(bgElement90);

		bgElement180 = new HUDElement(200, 200);
		bgElement180.setLocation(400, 300);
		canvas.addElement(bgElement180);

		bgElement270 = new HUDElement(200, 200);
		bgElement270.setLocation(600, 300);
		canvas.addElement(bgElement270);

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
			bgElement0.getGraphics().drawImage(cam.renderImage(t0, 1000, 10000).getImage(), 0, 0, 200, 200, null);

			Transform3D t90 = new Transform3D();
			t90.rotY(Math.PI * 0.5f);
			t90.setTranslation(trans);
			bgElement90.getGraphics().drawImage(cam.renderImage(t90, 1000, 10000).getImage(), 0, 0, 200, 200, null);

			Transform3D t180 = new Transform3D();
			t180.rotY(Math.PI);
			t180.setTranslation(trans);
			bgElement180.getGraphics().drawImage(cam.renderImage(t180, 1000, 10000).getImage(), 0, 0, 200, 200, null);

			Transform3D t270 = new Transform3D();
			t270.rotY(Math.PI * 1.5f);
			t270.setTranslation(trans);
			bgElement270.getGraphics().drawImage(cam.renderImage(t270, 1000, 10000).getImage(), 0, 0, 200, 200, null);

			System.out.println("done in " + (System.currentTimeMillis() - start));
			lastPic = System.currentTimeMillis();
		}
	}

}
