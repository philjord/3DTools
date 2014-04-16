/*
 * Created on Jan 3, 2006
 */
package tools3d.universe;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ExponentialFog;
import javax.media.j3d.Fog;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.LinearFog;
import javax.vecmath.Color3f;

import tools3d.environment.Sea;
import tools3d.environment.sky.Sky;
import tools3d.utils.Utils3D;

/**
 * @author Administrator
 *
 */
public class EnvironmentalUniverse extends VisualPhysicalUniverse
{
	private BranchGroup environmentGroup = new BranchGroup();

	private Sky sky = new Sky();

	private Sea sea = new Sea();

	private ExponentialFog fogFog = new ExponentialFog(new Color3f(1.0f, 1.0f, 1.0f), 0.5f);

	private LinearFog hazeFog = new LinearFog(new Color3f(0.9f, 1.0f, 0.9f), 130, 180);

	private LinearFog nightFog = new LinearFog(new Color3f(0.0f, 0.0f, 0.0f), 230, 280);

	private AmbientLight ambLight;

	private boolean isFoggy = false;

	private boolean isHazy = false;

	private boolean isNight = false;

	/**
	 * A universe with a locale created 
	 * and the fog and skybox and some branches added
	 */
	public EnvironmentalUniverse()
	{
		environmentGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		environmentGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		environmentGroup.setUserData("environment Group");

		environmentGroup.addChild(sea);

		// Create ambient light	and add it
		Color3f alColor = new Color3f(1, 1, 1);
		ambLight = new AmbientLight(true, alColor);
		ambLight.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		ambLight.setCapability(Light.ALLOW_COLOR_WRITE);
		ambLight.setInfluencingBounds(Utils3D.defaultBounds);
		environmentGroup.addChild(ambLight);

		// Add the fog
		fogFog.setCapability(Fog.ALLOW_INFLUENCING_BOUNDS_WRITE);
		environmentGroup.addChild(fogFog);

		hazeFog.setCapability(Fog.ALLOW_INFLUENCING_BOUNDS_WRITE);
		environmentGroup.addChild(hazeFog);

		nightFog.setCapability(Fog.ALLOW_INFLUENCING_BOUNDS_WRITE);
		environmentGroup.addChild(nightFog);

		theLocale.addBranchGraph(environmentGroup);
	}

	public void addToEnvironmentBranch(BranchGroup newGroup)
	{
		newGroup.setCapability(BranchGroup.ALLOW_DETACH);
		environmentGroup.addChild(newGroup);
	}

	public void setTime(int hours, int minutes, int seconds)
	{
		sky.setTime(hours, minutes, seconds);
		//TODO: if night time set night clips etc
	}

	public void toggleDayNight()
	{
		if (isNight)
		{
			setDay();
		}
		else
		{
			setNight();
		}
	}

	public void setNight()
	{
		sky.setNightTime();
		nightFog.setInfluencingBounds(Utils3D.defaultBounds);
		isNight = true;
	}

	public void setDay()
	{
		sky.setDayTime();
		nightFog.setInfluencingBounds(null);
		isNight = false;
	}

	public void toggleFog()
	{
		if (isFoggy)
		{
			setFogOff();
		}
		else
		{
			setFogOn();
		}
	}

	public void setFogOn()
	{
		fogFog.setInfluencingBounds(Utils3D.defaultBounds);
		sky.setFoggy();
		isFoggy = true;
	}

	public void setFogOff()
	{
		fogFog.setInfluencingBounds(null);
		sky.setDayTime();
		isFoggy = false;
	}

	public void toggleHaze()
	{
		if (isHazy)
		{
			setHazeOff();
		}
		else
		{
			setHazeOn();
		}
	}

	public void setHazeOn()
	{
		hazeFog.setInfluencingBounds(Utils3D.defaultBounds);
		//TODO: I'm thinking about this a bit, it's correct but not exactly what I'm looking for 
		sky.setHazy();
		isHazy = true;
	}

	public void setHazeOff()
	{
		hazeFog.setInfluencingBounds(null);
		sky.setDayTime();
		isHazy = false;
	}

	public void setAmbientLight(Color3f newColor)
	{
		ambLight.setColor(newColor);
	}

	public void setSkyEnabled(boolean enableSky)
	{
		if (enableSky)
		{
			if (!sky.isEnabled())
			{
				environmentGroup.addChild(sky);
			}
		}
		else
		{
			if (sky.isEnabled())
			{
				environmentGroup.removeChild(sky);
			}
		}
		sky.setEnabled(enableSky);
	}

	/**
	 * @return
	 */
	public Sea getSea()
	{
		return sea;
	}

	/**
	 * @return
	 */
	public Sky getSky()
	{
		return sky;
	}

}
