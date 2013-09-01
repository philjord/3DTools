package tools3d.navigation;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.j3d.Canvas3D;
import javax.swing.SwingUtilities;
import javax.vecmath.Point2f;

import tools.WeakListenerList;

public class NavigationInputAWTMouse implements MouseListener, MouseMotionListener
{
	// multiplyer to get from pixels difference to radian turnage
	// eg 0.01f mean 100 pixels makes for 1 PI per second or 180 degrees
	private static final float ROTATE_FACTOR = 0.01f;

	private static final float FREE_LOOK_GROSS_ROTATE_FACTOR = -0.002f;

	private static final float FINE_RATIO_OF_GROSS = 0.3f;

	private static final int MAX_PIXEL_FOR_FINE_MOVEMENT = 3;

	// The canvas this handler is operating on
	private Canvas3D canvas;

	private NavigationProcessorInterface navigationProcesor;

	private Cursor invisibleCursor;

	// The position where the mouse started it's last press,
	// used to communicate between mouse pressed and mouse dragged
	private Point2f startMousePos = new Point2f();

	private Robot robot;

	private Point previousMouseLocation = new Point();

	private Point centerLocation = new Point();

	boolean isRecentering = false;

	boolean isFreeLook = false;

	private WeakListenerList<NavigationRotationStateListener> navigationRotationStateListeners = new WeakListenerList<NavigationRotationStateListener>();

	public NavigationInputAWTMouse()
	{
		invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible");

		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			System.out.println("Can't create Robot");
		}

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

	public void setCanvas(Canvas3D newCanvas)
	{
		// remove the old canvas listening
		if (canvas != null)
		{
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);
			setFreelook(false);
		}

		canvas = newCanvas;
		if (canvas != null)
		{
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			// setFreelook(true);
		}
	}

	private void setFreelook(boolean b)
	{
		if (b)
		{
			isFreeLook = true;
			recenterMouse();

			// blank the Cursor out
			canvas.setCursor(invisibleCursor);
		}
		else
		{
			isFreeLook = false;
			canvas.setCursor(Cursor.getDefaultCursor());
		}

	}

	private void recenterMouse()
	{
		if (canvas != null && robot != null)
		{
			centerLocation.x = canvas.getWidth() / 2;
			centerLocation.y = canvas.getHeight() / 2;
			SwingUtilities.convertPointToScreen(centerLocation, canvas);
			isRecentering = true;

			// NOTE: this mechanism assumes than when a mouse move is fired from inside a current
			// mouse move event then the next mouse move event to be called from the event queue
			// will be the fired event. That is to say the event system cannot stack up 5 or 6 mouse moves in the AWT
			// event
			// queue, if it does this code will assume the next mouse move event is the one from the robot call
			// and ignore it. Then when the robot called mouse move comes down the wire it will be processed like a
			// normal call
			// this is basically chaos time. simple experiments have shown the robot call is always the next event on
			// the queue.
			robot.mouseMove(centerLocation.x, centerLocation.y);
			previousMouseLocation.x = centerLocation.x;
			previousMouseLocation.y = centerLocation.y;
			SwingUtilities.convertPointFromScreen(previousMouseLocation, canvas);
		}
	}

	// deburner
	Point mousePoint = new Point();

	public void mouseMoved(MouseEvent e)
	{
		if (isFreeLook)
		{
			// this event is from the re-centering the mouse - ignore it
			if (isRecentering)
			{
				isRecentering = false;
			}
			else
			{

				//NOTE DX and DY are from mouse usage so are reversed for the 3d coords
				int dx = e.getX() - previousMouseLocation.x;
				int dy = e.getY() - previousMouseLocation.y;

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

				mousePoint.setLocation(e.getPoint());
				SwingUtilities.convertPointToScreen(mousePoint, canvas);
				if (centerLocation.distance(mousePoint) > 100)
				{
					recenterMouse();
				}
			}

			previousMouseLocation.x = e.getX();
			previousMouseLocation.y = e.getY();
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

	public void mouseExited(MouseEvent evt)
	{
		if (isFreeLook)
		{
			recenterMouse();
		}
		if (navigationProcesor != null)
		{
			// call in case dragging has set the roation per seconds values
			navigationProcesor.setRotationPerSec(0, 0);
			fireListeners(false, false, false, false);

		}
	}

	// deburner
	private Point2f mouseDifference = new Point2f();

	/** The latest position of the mouse from the last event */
	private Point2f latestMousePos = new Point2f();

	public void mouseDragged(MouseEvent evt)
	{
		if (!isFreeLook)
		{
			latestMousePos.set(evt.getX(), evt.getY());
			mouseDifference.sub(startMousePos, latestMousePos);
			if (navigationProcesor != null)
			{
				navigationProcesor.setRotationPerSec(mouseDifference.y * ROTATE_FACTOR, mouseDifference.x * ROTATE_FACTOR);

				fireListeners(mouseDifference.y > 0, mouseDifference.y < 0, mouseDifference.x > 0, mouseDifference.x < 0);

			}
		}
	}

	/**
	 * record when the drag started (if a drag happens)
	 */
	public void mousePressed(MouseEvent evt)
	{
		if (!isFreeLook)
		{
			startMousePos.set(evt.getX(), evt.getY());
		}
	}

	/**
	 * Process a mouse release to return all the values back to normal.
	 */
	public void mouseReleased(MouseEvent evt)
	{
		if (navigationProcesor != null)
		{
			navigationProcesor.setRotationPerSec(0, 0);
			fireListeners(false, false, false, false);
		}
		if (evt.getButton() == MouseEvent.BUTTON3)
		{
			if (!isFreeLook)
			{
				setFreelook(true);
			}
			else
			{
				setFreelook(false);
			}
		}
	}

	public void mouseEntered(MouseEvent evt)
	{
		canvas.requestFocus();
	}

	public void mouseClicked(MouseEvent evt)
	{
	}

 
}
