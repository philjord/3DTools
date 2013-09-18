package tools3d.navigation.rules.deprecated;

import javax.vecmath.Vector3f;

public interface NavigationPositionRule
{
	/**
	 * Note any implementio of this 
	 * MUST alter the incoming translation, I should probably remove the return value to indicate this.
	 * @param avatarDesiredTranslation
	 * @param avatarTranslation
	 * @return
	 */
	public Vector3f applyRule(Vector3f avatarDesiredTranslation, Vector3f avatarTranslation);

	public void setActive(boolean a);

	public boolean isActive();

}
