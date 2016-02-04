package tools3d.navigation;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;

import tools.WeakListenerList;

public class NavigationInputNewtMouseLocked implements MouseListener
{
	// multiplyer to get from pixels difference to radian turnage
	// eg 0.01f mean 100 pixels makes for 1 PI per second or 180 degrees

	private static final float FREE_LOOK_GROSS_ROTATE_FACTOR = -0.002f;

	private static final float FINE_RATIO_OF_GROSS = 0.3f;

	private static final int MAX_PIXEL_FOR_FINE_MOVEMENT = 3;

	// The canvas this handler is operating on
	private GLWindow glWindow;

	private NavigationProcessorInterface navigationProcesor;

	//private Point previousMouseLocation = new Point();

	//private Point centerLocation = new Point();

	//boolean isRecentering = false;

	private WeakListenerList<NavigationRotationStateListener> navigationRotationStateListeners = new WeakListenerList<NavigationRotationStateListener>();

	public NavigationInputNewtMouseLocked()
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
			glWindow.confinePointer(false);
			glWindow.removeMouseListener(this);
			glWindow.setPointerVisible(true);
		}

		glWindow = newGlWindow;
		if (glWindow != null)
		{
			glWindow.confinePointer(true);
			glWindow.addMouseListener(this);
			glWindow.setPointerVisible(false);
		}
	}

	public boolean hasGLWindow()
	{
		return glWindow != null;
	}

	private void recenterMouse()
	{
		if (glWindow != null)
		{
			// work out where the mouse should be
			glWindow.warpPointer(glWindow.getWidth() / 2, glWindow.getHeight() / 2);
		}
	}

	//private int previousMouseLocationx;
	//private int previousMouseLocationy;

	public void mouseMoved(MouseEvent e)
	{
		// ignore the warpPointer event above
		if (e.getX() != glWindow.getWidth() / 2 || e.getY() != glWindow.getHeight() / 2)
		{
			int dx = e.getX() - (glWindow.getWidth() / 2);
			int dy = e.getY() - (glWindow.getHeight() / 2);

			if (dx != 0 || dy != 0)
			{
				double scaledDeltaY = (double) dy * FREE_LOOK_GROSS_ROTATE_FACTOR;
				double scaledDeltaX = (double) dx * FREE_LOOK_GROSS_ROTATE_FACTOR;

				if (Math.abs(dy) < MAX_PIXEL_FOR_FINE_MOVEMENT && Math.abs(dx) < MAX_PIXEL_FOR_FINE_MOVEMENT)
				{
					scaledDeltaY *= FINE_RATIO_OF_GROSS;
					scaledDeltaX *= FINE_RATIO_OF_GROSS;
				}

				if (navigationProcesor != null)
				{
					navigationProcesor.changeRotation(scaledDeltaY, scaledDeltaX);
				}

			}
			//TODO: but how do I send all stopped messages? I'm on a move listener.
			fireListeners(dx < 0, dx > 0, dy > 0, dy < 0);

			recenterMouse();
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
		recenterMouse();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseWheelMoved(MouseEvent e)
	{
	}

}
