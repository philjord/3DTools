package tools3d.navigation.rules;

import javax.vecmath.Quat4f;

public interface NavigationRotationRule
{
	// note this is the new roation is absolute terms not relative to current
	public Quat4f applyRule(Quat4f desiredRotation, Quat4f currentRotation);

	public void setActive(boolean a);

	public boolean isActive();
}
