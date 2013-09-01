package tools3d.environment.sky;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import tools3d.utils.Utils3D;

public class StarScape extends BranchGroup
{
	private SkyBox skyBox = new SkyBox();

	private DirectionalLight sunLight;

	// private SunSphere sun = new SunSphere();

	public StarScape()
	{
		this.setCapability(BranchGroup.ALLOW_DETACH);
		skyBox.setApplicationBounds(Utils3D.defaultBounds);

		addChild(skyBox);

		// Create sun light and add it
		Color3f slColor = new Color3f(0.9f, 0.9f, 0.9f);
		sunLight = new DirectionalLight(slColor, new Vector3f(0.2f, -1.0f, -0.2f));
		sunLight.setInfluencingBounds(Utils3D.defaultBounds);
		sunLight.setCapability(Light.ALLOW_STATE_WRITE);
		sunLight.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
		addChild(sunLight);

	}

	public void setImage(String skyImage)
	{
		skyBox.setSkyImages(skyImage);
	}

	public void setTransform(Transform3D trans)
	{
		skyBox.setTransform(trans);
	}

}
