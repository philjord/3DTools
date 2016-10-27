package tools3d.camera;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Group;

import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.ViewingPlatform;

public abstract class Dolly extends ViewingPlatform implements IDolly
{
	public Dolly()
	{
		this(1);
	}

	public Dolly(int numTransforms)
	{
		super(numTransforms);
		//note 62 is the default, but it's a hard number to find so I'm explicitly setting it here.
		this.getViewPlatform().setActivationRadius(62f);

		PlatformGeometry pg = new PlatformGeometry();
		pg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		pg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPlatformGeometry(pg);

	}

	@Override
	public void setHudShape(BranchGroup hudShapeRoot)
	{
		this.getPlatformGeometry().addChild(hudShapeRoot);
	}
}
