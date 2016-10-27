package tools3d.camera;

import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.ViewPlatform;

public class LocatableViewPlatform extends ViewPlatform
{
	// this group can be used to work out (roughly) where the ViewPlatform is
	private TransformGroup shortCutGroup;

	public TransformGroup getShortCutGroup()
	{
		return shortCutGroup;
	}

	public void setShortCutGroup(TransformGroup shortCutGroup)
	{
		this.shortCutGroup = shortCutGroup;
	}
}
