package tools3d.camera;

import org.jogamp.java3d.BranchGroup;

import tools3d.utils.scenegraph.LocationUpdateListener;

public interface IDolly extends LocationUpdateListener
{
	public abstract void setHudShape(BranchGroup hudShapeRoot);
}
