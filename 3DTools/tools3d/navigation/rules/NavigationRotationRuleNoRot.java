package tools3d.navigation.rules;

import javax.vecmath.Quat4f;


public class NavigationRotationRuleNoRot implements NavigationRotationRule
{

	private boolean active = false;

	public NavigationRotationRuleNoRot()
	{
	}

	public Quat4f applyRule(Quat4f desiredRotation, Quat4f currentRotation)
	{
		desiredRotation.set(currentRotation);
		return desiredRotation;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
}
