package tools3d.navigation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import tools.WeakListenerList;

public class NavigationInputAWTKey implements KeyListener
{

	public final static float FORWARD_RATE = 8.0f;

	public final static float FAST_FORWARD_RATE = 100.0f;

	public final static float BACKWARD_RATE = 3.5f;

	public final static float STRAFF_RATE = 3.0f;

	public static float VERTICAL_RATE = 10.0f; //not final to allow faster flight

	public final static int FORWARD_KEY = KeyEvent.VK_W;

	public final static int FAST_KEY = KeyEvent.VK_E;

	public final static int BACK_KEY = KeyEvent.VK_S;

	public final static int LEFT_KEY = KeyEvent.VK_A;

	public final static int RIGHT_KEY = KeyEvent.VK_D;

	public final static int UP_KEY = KeyEvent.VK_Q;

	public final static int DOWN_KEY = KeyEvent.VK_Z;

	private boolean allowVerticalMovement = false;

	// The canvas this handler is operating on
	private NavigationProcessorInterface navigationProcesor;

	// My extra key pushing bits
	private boolean walkHeldDown = false;

	private boolean runHeldDown = false;

	private boolean backHeldDown = false;

	private boolean strafLeftHeldDown = false;

	private boolean strafRightHeldDown = false;

	private boolean upHeldDown = false;

	private boolean downHeldDown = false;

	private WeakListenerList<NavigationTranslationStateListener> navigationTranslationStateListeners = new WeakListenerList<NavigationTranslationStateListener>();

	public NavigationInputAWTKey(NavigationProcessorInterface navigationProcesor)
	{
		this.navigationProcesor = navigationProcesor;
	}

	public void addNavigationTranslationStateListener(NavigationTranslationStateListener navigationTranslationStateListener)
	{
		navigationTranslationStateListeners.add(navigationTranslationStateListener);
	}

	public void removeNavigationTranslationStateListener(NavigationTranslationStateListener navigationTranslationStateListener)
	{
		navigationTranslationStateListeners.remove(navigationTranslationStateListener);
	}

	public void keyTyped(KeyEvent e)
	{
	}

	public void keyPressed(KeyEvent e)
	{
		// NOTE keyPressed will occur multiple times if a key is
		// held down by the auto repeat system.
		// hence this crap below

		if (e.getKeyCode() == FAST_KEY && !runHeldDown)
		{
			runHeldDown = true;
			setTranslationChange();
		}
		else if (e.getKeyCode() == FORWARD_KEY && !walkHeldDown)
		{
			walkHeldDown = true;
			setTranslationChange();
		}
		else if (e.getKeyCode() == BACK_KEY && !backHeldDown)
		{
			backHeldDown = true;
			setTranslationChange();
		}
		else if (e.getKeyCode() == LEFT_KEY && !strafLeftHeldDown)
		{
			strafLeftHeldDown = true;
			setTranslationChange();
		}
		else if (e.getKeyCode() == RIGHT_KEY && !strafRightHeldDown)
		{
			strafRightHeldDown = true;
			setTranslationChange();
		}
		else if (isAllowVerticalMovement() && e.getKeyCode() == UP_KEY && !upHeldDown)
		{
			upHeldDown = true;
			setTranslationChange();
		}
		else if (isAllowVerticalMovement() && e.getKeyCode() == DOWN_KEY && !downHeldDown)
		{
			downHeldDown = true;
			setTranslationChange();
		}
	}

	private void setTranslationChange()
	{
		if (runHeldDown)
		{
			navigationProcesor.setZChange(FAST_FORWARD_RATE);
		}
		else if (backHeldDown)
		{
			navigationProcesor.setZChange(-BACKWARD_RATE);
		}
		else if (walkHeldDown)
		{
			navigationProcesor.setZChange(FORWARD_RATE);
		}
		else
		{
			navigationProcesor.setZChange(0);
		}

		if (strafLeftHeldDown && !strafRightHeldDown)
		{
			navigationProcesor.setXChange(-STRAFF_RATE);
		}
		else if (strafRightHeldDown && !strafLeftHeldDown)
		{
			navigationProcesor.setXChange(STRAFF_RATE);
		}
		else
		{
			navigationProcesor.setXChange(0);
		}
		if (isAllowVerticalMovement())
		{
			if (upHeldDown && !downHeldDown)
			{
				navigationProcesor.setYChange(VERTICAL_RATE);
			}
			else if (downHeldDown && !upHeldDown)
			{
				navigationProcesor.setYChange(-VERTICAL_RATE);
			}
			else
			{
				navigationProcesor.setYChange(0);
			}
		}
		// tell the listeners
		for (NavigationTranslationStateListener ntsl : navigationTranslationStateListeners)
		{
			ntsl.inputStateChanged(walkHeldDown, runHeldDown, backHeldDown, strafLeftHeldDown, strafRightHeldDown, upHeldDown, downHeldDown);
		}
	}

	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == FAST_KEY)
		{
			runHeldDown = false;
			setTranslationChange();
		}
		else if (e.getKeyCode() == FORWARD_KEY)
		{
			walkHeldDown = false;
			setTranslationChange();
		}
		else if (e.getKeyCode() == BACK_KEY)
		{
			backHeldDown = false;
			setTranslationChange();
		}
		else if (e.getKeyCode() == LEFT_KEY)
		{
			strafLeftHeldDown = false;
			setTranslationChange();
		}
		else if (e.getKeyCode() == RIGHT_KEY)
		{
			strafRightHeldDown = false;
			setTranslationChange();
		}
		else if (isAllowVerticalMovement() && e.getKeyCode() == UP_KEY)
		{
			upHeldDown = false;
			setTranslationChange();
		}
		else if (isAllowVerticalMovement() && e.getKeyCode() == DOWN_KEY)
		{
			downHeldDown = false;
			setTranslationChange();
		}

	}

	public boolean isAllowVerticalMovement()
	{
		return allowVerticalMovement;
	}

	public void setAllowVerticalMovement(boolean allowVerticalMovement)
	{
		this.allowVerticalMovement = allowVerticalMovement;
	}

}