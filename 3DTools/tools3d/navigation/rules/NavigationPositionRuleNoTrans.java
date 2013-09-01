package tools3d.navigation.rules;

import javax.vecmath.Vector3f;


public class NavigationPositionRuleNoTrans implements NavigationPositionRule
{

	private boolean active = false;

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public NavigationPositionRuleNoTrans()
	{
	}

	@Override
	public Vector3f applyRule(Vector3f desiredTranslation, Vector3f currentLocation)
	{
		desiredTranslation.set(0, 0, 0);
		return desiredTranslation;
	}
}
