package tools3d.mixed3d2d;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.PickConeRay;
import org.jogamp.java3d.PickCylinderRay;
import org.jogamp.java3d.PickRay;
import org.jogamp.java3d.PickShape;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.View;

import com.jogamp.newt.event.MouseEvent;

/**
 * Rip off of PickCanvas which will set the appropriate pick shape for use by so0methign else (like physics based picking)
 */
public class CanvasPickRayGen //extends PickTool 
{

	private PickShape pickShape;

	/** The canvas we are picking into */
	private Canvas3D canvas;

	/* the pick tolerance, default to 2.0 */
	private float tolerance = 2.0f;

	private int save_xpos;

	private int save_ypos;

	/** Constructor with Canvas3D for mouse events and BranchGroup to be picked.
	 */
	public CanvasPickRayGen(Canvas3D c)
	{
		canvas = c;

	}

	/** Inquire the canvas to be used for picking operations.
	@return the canvas.
	 */
	public Canvas3D getCanvas()
	{
		return canvas;
	}

	/** Set the picking tolerance.  Objects within this distance
	 * (in pixels) 
	 * to the mouse x,y location will be picked.  The default tolerance is 2.0.
	 * @param t The tolerance
	 * @exception IllegalArgumentException if the tolerance is less than 0.
	 */
	public void setTolerance(float t)
	{
		if (t < 0.0f)
		{
			throw new IllegalArgumentException();
		}
		tolerance = t;

		if (pickShape != null)
		{
			// reset pickShape
			pickShape = null;
			setShapeLocation(save_xpos, save_ypos);
		}
	}

	/** Get the pick tolerance. 
	 */
	public float getTolerance()
	{
		return tolerance;
	}

	public void setShapeLocation(MouseEvent mevent)
	{
		setShapeLocation(mevent.getX(), mevent.getY());
	}

	/** Set the pick location. Defines the location on the canvas where the
	    pick is to be performed (upper left corner of canvas is 0,0).
	@param xpos the X position of the picking point
	@param ypos the Y position of the picking point
	*/
	public void setShapeLocation(int xpos, int ypos)
	{
		Transform3D motion = new Transform3D();
		Point3d eyePosn = new Point3d();
		Point3d mousePosn = new Point3d();
		Vector3d mouseVec = new Vector3d();
		boolean isParallel = false;
		//		double radius = 0.0;
		double spreadAngle = 0.0;

		this.save_xpos = xpos;
		this.save_ypos = ypos;
		canvas.getCenterEyeInImagePlate(eyePosn);
		canvas.getPixelLocationInImagePlate(xpos, ypos, mousePosn);

		if ((canvas.getView() != null) && (canvas.getView().getProjectionPolicy() == View.PARALLEL_PROJECTION))
		{
			// Correct for the parallel projection: keep the eye's z
			// coordinate, but make x,y be the same as the mouse, this
			// simulates the eye being at "infinity"
			eyePosn.x = mousePosn.x;
			eyePosn.y = mousePosn.y;
			isParallel = true;
		}

		// Calculate radius for PickCylinderRay and spread angle for PickConeRay
		Vector3d eyeToCanvas = new Vector3d();
		eyeToCanvas.sub(mousePosn, eyePosn);
		double distanceEyeToCanvas = eyeToCanvas.length();

		Point3d deltaImgPlate = new Point3d();
		canvas.getPixelLocationInImagePlate(xpos + 1, ypos, deltaImgPlate);

		Vector3d ptToDelta = new Vector3d();
		ptToDelta.sub(mousePosn, deltaImgPlate);
		double distancePtToDelta = ptToDelta.length();
		distancePtToDelta *= tolerance;

		if (!canvas.getView().getCompatibilityModeEnable())
		{
			canvas.getImagePlateToVworld(motion);
		}
		else
		{
			canvas.getView().getViewPlatform().getLocalToVworld(motion);
		}

		/*
		System.out.println("mouse position " + xpos + " " + ypos);
		System.out.println("before, mouse " + mousePosn + " eye " + eyePosn);
		*/

		motion.transform(eyePosn);
		//		start = new Point3d(eyePosn); // store the eye position
		motion.transform(mousePosn);
		mouseVec.sub(mousePosn, eyePosn);
		mouseVec.normalize();

		/*
		System.out.println(motion + "\n");
		System.out.println("after, mouse " + mousePosn + " eye " + eyePosn + 
			 " mouseVec " + mouseVec);
			 */

		if (tolerance == 0.0)
		{
			if ((pickShape != null) && (pickShape instanceof PickRay))
			{
				((PickRay) pickShape).set(eyePosn, mouseVec);
			}
			else
			{
				pickShape = new PickRay(eyePosn, mouseVec);
			}
			//      pickShape = (PickShape) new PickConeRay (eyePosn,
			//		mouseVec,1.0*Math.PI/180.0);
		}
		else
		{
			if (isParallel)
			{
				// Parallel projection, use a PickCylinderRay
				distancePtToDelta *= motion.getScale();
				if ((pickShape != null) && (pickShape instanceof PickCylinderRay))
				{
					((PickCylinderRay) pickShape).set(eyePosn, mouseVec, distancePtToDelta);
				}
				else
				{
					pickShape = new PickCylinderRay(eyePosn, mouseVec, distancePtToDelta);
				}
			}
			else
			{
				// Perspective projection, use a PickConeRay

				// Calculate spread angle
				spreadAngle = Math.atan(distancePtToDelta / distanceEyeToCanvas);

				if ((pickShape != null) && (pickShape instanceof PickConeRay))
				{
					((PickConeRay) pickShape).set(eyePosn, mouseVec, spreadAngle);
				}
				else
				{
					pickShape = new PickConeRay(eyePosn, mouseVec, spreadAngle);
				}
			}
		}
	}

	/**
	 * Will be either a 
	 * if (tolerance == 0.0) PickRay
	 * Perspective projection, use a PickConeRay
	 * Parallel projection, use a PickCylinderRay
	 * 
	 * So very likely to be a PickRay or PickConeRay based on tolerance
	 * 
	 * @return
	 */
	public PickShape getPickShape()
	{
		return pickShape;
	}
}
