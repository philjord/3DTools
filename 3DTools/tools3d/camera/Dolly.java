package tools3d.camera;

import tools3d.utils.scenegraph.LocationUpdateListener;

import com.sun.j3d.utils.universe.ViewingPlatform;

public abstract class Dolly extends ViewingPlatform implements LocationUpdateListener
{
	public Dolly(int numTransforms)
	{
		super(numTransforms);
		//note 62 is the default, but it's a hard number to find so I'm explicitly setting it here.
		this.getViewPlatform().setActivationRadius(62f);

	}
}
