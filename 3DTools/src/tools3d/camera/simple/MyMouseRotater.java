package tools3d.camera.simple;

 
 
import java.util.Enumeration;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnBehaviorPost;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.MouseEvent;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.newt.MouseBehavior;

public class MyMouseRotater extends MouseBehavior
{
	double x_angle, y_angle;

	double x_factor = .015;

	double y_factor = .015;

	private MouseBehaviorCallback callback = null;

	public MyMouseRotater(Window c, TransformGroup transformGroup)
	{
		super(c, transformGroup);
		flags = flags | INVERT_INPUT;
	}

	public void initialize()
	{
		super.initialize();
		x_angle = 0;
		y_angle = 0;
		if ((flags & INVERT_INPUT) == INVERT_INPUT)
		{
			invert = true;
			x_factor *= -1;
			y_factor *= -1;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void processStimulus(Enumeration criteria)
	{
		WakeupCriterion wakeup;
		 
		MouseEvent evt;
		//	 	int id;
		//	 	int dx, dy;

		while (criteria.hasMoreElements())
		{
			wakeup = (WakeupCriterion) criteria.nextElement();

			if (wakeup instanceof WakeupOnBehaviorPost)
			{
				while (true)
				{
					// access to the queue must be synchronized
					synchronized (mouseq)
					{
						if (mouseq.isEmpty())
							break;
						evt = (MouseEvent) mouseq.remove(0);
						// consolidate MOUSE_DRAG events
						while ((evt.getEventType() == MouseEvent.EVENT_MOUSE_DRAGGED) && !mouseq.isEmpty()
								&& (((MouseEvent) mouseq.get(0)).getEventType() == MouseEvent.EVENT_MOUSE_DRAGGED))
						{
							evt = (MouseEvent) mouseq.remove(0);
						}
					}
					doProcess(evt);
				}
			}

		}
		wakeupOn(mouseCriterion);
	}

	void doProcess(MouseEvent evt)
	{
		int id;
		int dx, dy;

		processMouseEvent(evt);
		if (((buttonPress) && ((flags & MANUAL_WAKEUP) == 0)) || ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0)))
		{
			id = evt.getEventType();
			if ((id == MouseEvent.EVENT_MOUSE_DRAGGED) && !evt.isMetaDown() && !evt.isAltDown())
			{
				x = evt.getX();
				y = evt.getY();

				dx = x - x_last;
				dy = y - y_last;

				if (!reset)
				{
					double halfpi = Math.PI / 2;
					x_angle = dy * y_factor;
					x_angle = x_angle > halfpi ? halfpi : x_angle < -halfpi ? -halfpi : x_angle;
					y_angle = dx * x_factor;

					transformX.rotX(x_angle);
					transformY.rotY(y_angle);

					transformGroup.getTransform(currXform);

					Matrix4d mat = new Matrix4d();
					// Remember old matrix
					currXform.get(mat);

					// Translate to origin
					currXform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
					if (invert)
					{
						currXform.mul(currXform, transformX);
						currXform.mul(currXform, transformY);
					}
					else
					{
						currXform.mul(transformX, currXform);
						currXform.mul(transformY, currXform);
					}

					// Set old translation back
					Vector3d translation = new Vector3d(mat.m03, mat.m13, mat.m23);
					currXform.setTranslation(translation);

					// Update xform
					transformGroup.setTransform(currXform);

					if (callback != null)
						callback.transformChanged(MouseBehaviorCallback.ROTATE, currXform);
				}
				else
				{
					reset = false;
				}

				x_last = x;
				y_last = y;
			}
			else if (id == MouseEvent.EVENT_MOUSE_PRESSED)
			{
				x_last = evt.getX();
				y_last = evt.getY();
			}
		}
	}

	public void setupCallback(MouseBehaviorCallback callback)
	{
		this.callback = callback;
	}
}
