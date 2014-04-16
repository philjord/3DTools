package tools3d.navigation;

/**
 * @author pj
 */
public interface NavigationRotationStateListener
{
	public void inputStateChanged(boolean turnLeft, boolean turnRight, boolean turnUp, boolean turnDown);
}
