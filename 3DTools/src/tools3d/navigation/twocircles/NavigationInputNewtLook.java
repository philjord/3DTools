package tools3d.navigation.twocircles;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;

import tools.WeakListenerList;
import tools3d.navigation.NavigationProcessorInterface;
import tools3d.navigation.NavigationRotationStateListener;

/**
 * Dragged listener, for Android where no recenter can happen, so mouse down defines the center
 * @author phil
 *
 */
public class NavigationInputNewtLook implements MouseListener
{
	// multiplyer to get from pixels difference to radian turnage
	// eg 0.01f mean 100 pixels makes for 1 PI per second or 180 degrees

	private static final float FREE_LOOK_GROSS_ROTATE_FACTOR = -0.005f;

	// The canvas this handler is operating on
	private GLWindow glWindow;

	private NavigationProcessorInterface navigationProcesor;

	//private Point previousMouseLocation = new Point();

	//private Point centerLocation = new Point();

	//boolean isRecentering = false;

	private WeakListenerList<NavigationRotationStateListener> navigationRotationStateListeners = new WeakListenerList<NavigationRotationStateListener>();

	public NavigationInputNewtLook()
	{

		//	glwindow confinePointer(boolean grab) woot!
		//public final void confinePointer(boolean grab)

		//Description copied from interface: Window
		//Confine the pointer to this window, ie. pointer jail.

		//Before jailing the mouse pointer, the window request the focus and the pointer is centered in the window.

		//In combination w/ Window.warpPointer(int, int) and maybe Window.setPointerVisible(boolean) a simple mouse navigation can be realized.
	}

	public void addNavigationRotationStateListener(NavigationRotationStateListener navigationRotationStateListener)
	{
		navigationRotationStateListeners.add(navigationRotationStateListener);
	}

	public void removeNavigationRotationStateListener(NavigationRotationStateListener navigationRotationStateListener)
	{
		navigationRotationStateListeners.remove(navigationRotationStateListener);
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

	private int mouseDownLocationx;
	private int mouseDownLocationy;

	public void mouseMoved(MouseEvent e)
	{
		//only use left hand side of screen, bottom half for now
		for (int i = 0; i < e.getPointerCount(); i++)
		{
			int ex = e.getX(i);
			int ey = e.getY(i);
			//Only listen to right side, bottom half of screen (until circle exists)
			if (ex > (glWindow.getWidth() / 2) && ey > (glWindow.getHeight() / 2))
			{
				int dx = ex - mouseDownLocationx;
				int dy = ey - mouseDownLocationy;

				if (dx != 0 || dy != 0)
				{
					double scaledDeltaX = (double) dx * FREE_LOOK_GROSS_ROTATE_FACTOR;
					double scaledDeltaY = (double) dy * FREE_LOOK_GROSS_ROTATE_FACTOR;

					if (navigationProcesor != null)
					{
						navigationProcesor.changeRotation(scaledDeltaY, scaledDeltaX);
					}

					mouseDownLocationx = e.getX();
					mouseDownLocationy = e.getY();

				}
				fireListeners(dx < 0, dx > 0, dy > 0, dy < 0);
			}
		}
	}

	private void fireListeners(boolean turnLeft, boolean turnRight, boolean turnUp, boolean turnDown)
	{
		// tell the listeners
		for (NavigationRotationStateListener nrsl : navigationRotationStateListeners)
		{
			nrsl.inputStateChanged(turnLeft, turnRight, turnUp, turnDown);
		}
	}

	@Override
	public void mouseDragged(MouseEvent evt)
	{
		mouseMoved(evt);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent evt)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		for (int i = 0; i < e.getPointerCount(); i++)
		{
			int ex = e.getX(i);
			int ey = e.getY(i);
			//Only listen to right side, bottom half of screen (until circle exists)
			if (ex > (glWindow.getWidth() / 2) && ey > (glWindow.getHeight() / 2))
			{
				mouseDownLocationx = ex;
				mouseDownLocationy = ey;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		//ignored as a mouse down will need to happen
	}

	@Override
	public void mouseWheelMoved(MouseEvent e)
	{
	}

}
