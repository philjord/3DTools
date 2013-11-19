package tools3d.utils.scenegraph;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;

/** A behavior that updates on a frequency based on distance from viewer, 
 * so more distance animations can run slower
 * current 1 frame 5 frames 15 frames and 50 frame
 * 
 * @author philip
 *
 */
public abstract class VaryingLODBehaviour extends Behavior
{

	// Wakeup condition for node
	private WakeupOnElapsedFrames wakeupFrameFast = new WakeupOnElapsedFrames(1, true);

	private WakeupOnElapsedFrames wakeupFrame5 = new WakeupOnElapsedFrames(5, true);

	private WakeupOnElapsedFrames wakeupFrame15 = new WakeupOnElapsedFrames(15, true);

	private WakeupOnElapsedFrames wakeupFrame50 = new WakeupOnElapsedFrames(50, true);

	// Node to operate on.
	private Node node = null;

	// deburners
	private Point3d viewPosition = new Point3d();

	private Transform3D xform = new Transform3D();

	private float[] dists = null;

	static final double EPSILON = 1.0e-6;

	/**
	 * Dists must be 3 floats! 40,120,280 is fine
	 *TODO: like knots add frames and dist arrays 
	 * @param node
	 */
	public VaryingLODBehaviour(Node node, float[] dists)
	{
		node.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		this.node = node;
		this.dists = dists;
	}

	/**
	 * Initialize method that sets up initial wakeup criteria.
	 */
	public void initialize()
	{
		// Insert wakeup condition into queue
		wakeupOn(wakeupFrameFast);
	}

	/**
	 * Process stimulus method that computes appropriate transform.
	 * @param criteria an enumeration of the criteria that caused the
	 * stimulus
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void processStimulus(Enumeration criteria)
	{
		process();

		if (node == null || dists == null)
		{
			//System.out.println("somefing null");
			wakeupOn(wakeupFrame50);
			return;
		}

		//  get viewplatforms's location in virutal world
		View v = this.getView();
		if (v == null)
		{
			//System.out.println("view null");
			wakeupOn(wakeupFrame50);
			return;
		}
		Canvas3D canvas = v.getCanvas3D(0);

		// rotate about axis
		canvas.getCenterEyeInImagePlate(viewPosition);
		canvas.getImagePlateToVworld(xform); // xform is imagePlateToLocal
		xform.transform(viewPosition);

		node.getLocalToVworld(xform);

		xform.invert(); // xform is now vWorldToLocal

		// transform the eye position into the billboard's coordinate system
		xform.transform(viewPosition);

		// I wager viewPosition is the eye point in the local transforms coordinates, I wager?
		//so let's just use the length for setting the wakeup
		double dist = Math.sqrt((viewPosition.x * viewPosition.x) + (viewPosition.y * viewPosition.y) + (viewPosition.z * viewPosition.z));
		//System.out.println("dist " + dist);
		//		Insert wakeup condition into queue
		if (dist < dists[0])
		{
			wakeupOn(wakeupFrameFast);
		}
		else if (dist < dists[1])
		{
			wakeupOn(wakeupFrame5);
		}
		else if (dist < dists[2])
		{
			wakeupOn(wakeupFrame15);
		}
		else
		{
			wakeupOn(wakeupFrame50);
		}

	}

	protected abstract void process();

}
