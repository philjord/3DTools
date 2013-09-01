package tools3d.navigation;

/**
 * @author pj
 */
public interface NavigationTranslationStateListener
{
	public void inputStateChanged(boolean walkHeldDown, boolean runHeldDown, boolean backHeldDown, boolean strafLeftHeldDown,
			boolean strafRightHeldDown, boolean upHeldDown, boolean downHeldDown);
}
