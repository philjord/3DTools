/*
 * Created on Jan 4, 2006
 */
package tools3d.environment.sky;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import tools3d.utils.Utils3D;

/**
 * @author Administrator
 * 
 */
public class Sky extends BranchGroup
{
	private boolean enabled = false;

	private SkyBox skyBox = new SkyBox();

	private DirectionalLight sunLight;

	private DirectionalLight moonLight;

	private SunSphere sun = new SunSphere();

	private MoonSphere moon = new MoonSphere();

	private boolean dayTime = true;

	public Sky()
	{
		this.setCapability(BranchGroup.ALLOW_DETACH);
		skyBox.setApplicationBounds(Utils3D.defaultBounds);
		addChild(skyBox);

		// Create sun light and add it
		Color3f slColor = new Color3f(1.0f, 1.0f, 1.0f);
		sunLight = new DirectionalLight(slColor, new Vector3f(0.2f, -1.0f, -0.2f));
		sunLight.setInfluencingBounds(Utils3D.defaultBounds);
		sunLight.setCapability(Light.ALLOW_STATE_WRITE);
		sunLight.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
		addChild(sunLight);

		// Create moon light and add it
		Color3f mlColor = new Color3f(0.2f, 0.2f, 0.2f);
		moonLight = new DirectionalLight(mlColor, new Vector3f(0.2f, -1.0f, -0.2f));
		moonLight.setInfluencingBounds(Utils3D.defaultBounds);
		moonLight.setCapability(Light.ALLOW_STATE_WRITE);
		moonLight.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);

		addChild(moonLight);

	}

	public void setTime(int hours, int minutes, int seconds)
	{
		if (enabled)
		{
			float x = 0;
			float y = 0;
			float z = 0;

			int totalSecs = (hours * 60 * 60) + (minutes * 60) + seconds;

			double alpha = (totalSecs / (24d * 60d * 60d));

			double angle = (totalSecs / (24d * 60d * 60d)) * (Math.PI * 2d);
			// for now just rotate around the z axis
			x = (float) -Math.sin(angle) + 0.5f;
			y = (float) -Math.cos(angle) + 0.5f;

			Vector3f sunPosition = new Vector3f(x, y, z);
			sunPosition.scale(400);
			sun.setPosition(sunPosition);
			Vector3f moonPosition = new Vector3f(-x, -y, -z);
			moonPosition.scale(400);
			moon.setPosition(moonPosition);

			Vector3f sunLightDirection = new Vector3f(-x, -y, -z);
			sunLight.setDirection(sunLightDirection);

			Vector3f moonLightDirection = new Vector3f(x, y, z);
			moonLight.setDirection(moonLightDirection);

			// is it day time?
			if (alpha > 0.25 && alpha < 0.75)
			{
				setDayTime();
			}
			else
			{
				// it is night time set the night time up!
				setNightTime();
			}
		}

	}

	public void setNightTime()
	{
		if (dayTime)
		{
			sunLight.setEnable(false);
			moonLight.setEnable(true);
			skyBox.setNightSky();
			dayTime = false;
		}
	}

	public void setDayTime()
	{
		if (!dayTime)
		{
			sunLight.setEnable(true);
			moonLight.setEnable(false);
			skyBox.setDaySky();
			dayTime = true;
		}
	}

	public void setFoggy()
	{
		skyBox.setFogSky();
	}

	/**
	 * 
	 */
	public void setHazy()
	{
		skyBox.setHazeSky();
	}

	/**
	 * @param skyExists
	 * @param skyImage
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		sunLight.setEnable(enabled);
		moonLight.setEnable(enabled);
	}

	public void setImage(String skyImage)
	{
		skyBox.setSkyImages(skyImage);
	}

	/**
	 * @return
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

}
