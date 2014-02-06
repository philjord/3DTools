package tools3d.camera;

import tools3d.mixed3d2d.Canvas3D2D;

public interface ICameraPanel
{
	public Canvas3D2D getCanvas3D2D();

	public void startRendering();

	public void setSceneAntialiasingEnable(boolean aaRequired);

	public void stopRendering();

	public IDolly getDolly();

	public void setDolly(IDolly dolly);

}
