package tools3d.navigation;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.j3d.Canvas3D;
import javax.swing.SwingUtilities;

import tools.WeakListenerList;

public class NavigationInputAWTMouseLocked implements MouseListener, MouseMotionListener, FocusListener
{
	// multiplyer to get from pixels difference to radian turnage
	// eg 0.01f mean 100 pixels makes for 1 PI per second or 180 degrees

	private static final float FREE_LOOK_GROSS_ROTATE_FACTOR = -0.002f;

	private static final float FINE_RATIO_OF_GROSS = 0.3f;

	private static final int MAX_PIXEL_FOR_FINE_MOVEMENT = 3;

	// The canvas this handler is operating on
	private Canvas3D canvas;

	private NavigationProcessorInterface navigationProcesor;

	private Cursor invisibleCursor;

	private Robot robot;

	private Point previousMouseLocation = new Point();

	private Point centerLocation = new Point();

	boolean isRecentering = false;

	private boolean hasFocus = false;

	private WeakListenerList<NavigationRotationStateListener> navigationRotationStateListeners = new WeakListenerList<NavigationRotationStateListener>();

	public NavigationInputAWTMouseLocked()
	{
		invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0),
				"invisible");

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
			canvas.removeFocusListener(this);
			canvas.setCursor(Cursor.getDefaultCursor());
		}

		canvas = newCanvas;
		if (canvas != null)
		{

			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			recenterMouse();
			canvas.setCursor(invisibleCursor);

			hasFocus = true;
			canvas.addFocusListener(this);
		}
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		hasFocus = true;
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		hasFocus = false;
	}

	private void recenterMouse()
	{
		if (canvas != null && robot != null && hasFocus)
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
		if (hasFocus)
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

	@Override
	public void mouseExited(MouseEvent evt)
	{
		recenterMouse();
	}

	@Override
	public void mouseDragged(MouseEvent evt)
	{
		//TODO: is this a problem?
		mouseMoved(evt);
	}

	@Override
	public void mousePressed(MouseEvent evt)
	{
	}

	@Override
	public void mouseReleased(MouseEvent evt)
	{
	}

	@Override
	public void mouseEntered(MouseEvent evt)
	{
	}

	public void mouseClicked(MouseEvent evt)
	{
	}

	public boolean hasCanvas()
	{
		return canvas != null;
	}

}
