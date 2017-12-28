package tools3d.navigation.twocircles;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;

import tools.WeakListenerList;
import tools3d.navigation.NavigationProcessorInterface;
import tools3d.navigation.NavigationTranslationStateListener;

public class NavigationInputNewtMove implements MouseListener
{

	public final static float FORWARD_RATE = 8.0f;

	public final static float FAST_FORWARD_RATE = 100.0f;

	public final static float BACKWARD_RATE = 3.5f;

	public final static float STRAFF_RATE = 3.0f;

	public static float VERTICAL_RATE = 10.0f; //not final to allow faster flight

	public static int FORWARD_KEY = KeyEvent.VK_W;

	public static int FAST_KEY = KeyEvent.VK_E;

	public static int BACK_KEY = KeyEvent.VK_S;

	public static int LEFT_KEY = KeyEvent.VK_A;

	public static int RIGHT_KEY = KeyEvent.VK_D;

	public static int UP_KEY = KeyEvent.VK_Q;

	public static int DOWN_KEY = KeyEvent.VK_Z;

	private boolean allowVerticalMovement = false;

	// The canvas this handler is operating on
	private GLWindow glWindow;

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

	public NavigationInputNewtMove()
	{

	}

	public void setNavigationProcessor(NavigationProcessorInterface navigationProcesor)
	{
		this.navigationProcesor = navigationProcesor;
	}

	public void setWindow(GLWindow newGlWindow)
	{
		// remove the old canvas listening
		if (glWindow != null)
		{
			glWindow.removeMouseListener(this);
		}

		glWindow = newGlWindow;
		if (glWindow != null)
		{
			glWindow.addMouseListener(this);
		}
	}

	public boolean hasGLWindow()
	{
		return glWindow != null;
	}

	public void addNavigationTranslationStateListener(NavigationTranslationStateListener navigationTranslationStateListener)
	{
		navigationTranslationStateListeners.add(navigationTranslationStateListener);
	}

	public void removeNavigationTranslationStateListener(NavigationTranslationStateListener navigationTranslationStateListener)
	{
		navigationTranslationStateListeners.remove(navigationTranslationStateListener);
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
			ntsl.inputStateChanged(walkHeldDown, runHeldDown, backHeldDown, strafLeftHeldDown, strafRightHeldDown, upHeldDown,
					downHeldDown);
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

	@Override
	public void mousePressed(MouseEvent e)
	{
		//only use left hand side of screen, bottom half for now
		for (int i = 0; i < e.getPointerCount(); i++)
		{
			int ex = e.getX(i);
			int ey = e.getY(i);

			// free flight is on the top half of screen one 1/8th of screen
			if (isAllowVerticalMovement() && ey < (glWindow.getHeight() / 2) && ex < (glWindow.getWidth() / 8))
			{
				upHeldDown = false;
				downHeldDown = false;

				if (ey < (glWindow.getHeight() / 4) && !upHeldDown)
				{
					upHeldDown = true;
				}
				else if (ey > (glWindow.getHeight() / 4) && !downHeldDown)
				{
					downHeldDown = true;
				}
				setTranslationChange();
			}
			else if (ex < (glWindow.getWidth() / 2) && ey > (glWindow.getHeight() / 2))
			{
				backHeldDown = false;
				walkHeldDown = false;
				strafLeftHeldDown = false;
				strafRightHeldDown = false;
				// notice lower Y is higher up screen! which is not natural at all!

				//relative to center of quarter screen
				int rex = ex - (glWindow.getWidth() / 4);
				int rey = ey - ((glWindow.getHeight() / 2) + (glWindow.getHeight() / 4));

				// fast is top 1/4 of square (==1/8 screen)
				// back is bottom 1/4 
				// left and right is each 1/4
				// central 1/2 x 1/2 is just move forward
				//int halfSizeH = (glWindow.getHeight() / 4);
				int quarterSizeH = (glWindow.getHeight() / 8);
				int quarterSizeW = (glWindow.getWidth() / 8);

				// run removed as troubling
				/*if (rey <= -quarterSizeH && !runHeldDown)
				{
					runHeldDown = true;
					setTranslationChange();
				}
				else 
				*/
				if (rex < -quarterSizeW)
				{
					strafLeftHeldDown = true;
				}
				else if (rex > quarterSizeW)
				{
					strafRightHeldDown = true;
				}
				else if (rey >= quarterSizeH)
				{
					backHeldDown = true;
				}
				else if (rey < quarterSizeH)
				{
					walkHeldDown = true;
				}

				
				setTranslationChange();
			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// find out if it's touching one of the areas for movement
		for (int i = 0; i < e.getPointerCount(); i++)
		{
			int ex = e.getX(i);
			int ey = e.getY(i);

			if (isAllowVerticalMovement() && ey < (glWindow.getHeight() / 2) && ex < (glWindow.getWidth() / 8))
			{
				upHeldDown = false;
				downHeldDown = false;
				setTranslationChange();
			}
			else if (ex < (glWindow.getWidth() / 2) && ey > (glWindow.getHeight() / 2))
			{
				runHeldDown = false;
				walkHeldDown = false;
				backHeldDown = false;
				strafLeftHeldDown = false;
				strafRightHeldDown = false;
				setTranslationChange();
			}

		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		mousePressed(e);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		mouseReleased(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		mousePressed(e);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		mousePressed(e);
	}

	@Override
	public void mouseWheelMoved(MouseEvent e)
	{
		System.out.println("What mouse wheel moved?");
	}

}
