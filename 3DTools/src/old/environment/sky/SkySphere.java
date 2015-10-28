package old.environment.sky;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BranchGroup;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

public class SkySphere extends Background
{
	private TextureLoader daySkyTexture;

	private TextureLoader nightSkyTexture;

	private TextureLoader fogSkyTexture;

	private TextureLoader hazeSkyTexture;

	private Appearance skyBoxApp;

	public SkySphere()
	{
		BranchGroup skyBoxBranch = new BranchGroup();
		Sphere sphereObj = new Sphere(1.0f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_NORMALS_INWARD | Primitive.GENERATE_TEXTURE_COORDS, 45);
		skyBoxApp = sphereObj.getAppearance();
		skyBoxApp.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		skyBoxBranch.addChild(sphereObj);
		setGeometry(skyBoxBranch);

		daySkyTexture = new TextureLoader("media/images/daySky2.jpg", null);
		nightSkyTexture = new TextureLoader("media/images/nightSky.jpg", null);
		fogSkyTexture = new TextureLoader("media/images/fogSky.jpg", null);
		hazeSkyTexture = new TextureLoader("media/images/hazeSky.jpg", null);

		if (daySkyTexture == null || nightSkyTexture == null || fogSkyTexture == null)
		{
			System.out.println("Oi! either the day or the night sky is a bad file buddy.");
		}
		else
		{
			skyBoxApp.setTexture(daySkyTexture.getTexture());
		}
	}

	public void setNightSky()
	{
		skyBoxApp.setTexture(nightSkyTexture.getTexture());
	}

	public void setDaySky()
	{
		skyBoxApp.setTexture(daySkyTexture.getTexture());
	}

	public void setFogSky()
	{
		skyBoxApp.setTexture(fogSkyTexture.getTexture());
	}

	public void setHazeSky()
	{
		skyBoxApp.setTexture(hazeSkyTexture.getTexture());
	}

}